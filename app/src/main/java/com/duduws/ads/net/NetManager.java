package com.duduws.ads.net;

import android.content.Context;
import android.text.TextUtils;

import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.model.SiteModel;
import com.duduws.ads.utils.AdsPreferences;
import com.duduws.ads.utils.Base64;
import com.duduws.ads.utils.DspHelper;
import com.duduws.ads.utils.FuncUtils;
import com.duduws.ads.utils.XXTea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/7/30 11:34
 */
public class NetManager {
    private static final String TAG = "NetManager";
    private static NetManager instance;
    private Context context;

    private NetManager(Context context) {
        this.context = context;
    }

    public static NetManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetManager(context);
        }
        return instance;
    }

    /**
     * 请求服务器
     */
    public void startRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (FuncUtils.hasActiveNetwork(context)) {
                    JSONObject jsonObject = NetHelper.getRequestInfo(context);
                    String str = new String(Base64.encode(XXTea.encrypt(jsonObject.toString().getBytes(), ConstDefine.XXTEA_KEY.getBytes())));
                    String response = NetHelper.sendPost(ConstDefine.SERVER_URL, str);
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            response = new String(XXTea.decrypt(Base64.decode(response.toCharArray()), ConstDefine.XXTEA_KEY.getBytes()));
                            parseRequest(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 解析服务器返回数据
     * @param response
     */
    private void parseRequest(String response) {
        MLog.i(TAG, response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject == null){
                return;
            }
            //解析服务器返回状态码
            int resCode = jsonObject.optInt("code");
            if (resCode == ConstDefine.SERVER_RES_SUCCESS) {
                //重置屏敝标志
                ConfigDefine.AD_MASK_FLAG = false;
                AdsPreferences.getInstance(context).setBoolean(DspHelper.AD_MASK_FLAG, false);
                //扩展信息
                JSONObject extendObj = null;
                long conTime = ConstDefine.DEFAULT_NEXT_CONNECT_TIME;
                try{
                    extendObj = jsonObject.getJSONObject("extend");
                    conTime = (extendObj == null) ? extendObj.optInt("net_con_interval") : ConstDefine.DEFAULT_NEXT_CONNECT_TIME;
                    //设置是否允许延时广告
                    if (!extendObj.isNull("delay_ads_channel")){
                        JSONArray mArr = extendObj.getJSONArray("delay_ads_channel");
                        for (int i=0; i<mArr.length(); i++){
                            JSONObject jObj = mArr.getJSONObject(i);
                            DspHelper.setDelayAdsFlag(context, jObj.getInt("cid"), jObj.getBoolean("flag"));
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //设置下次请求服务器时间
                DspHelper.setNetConTime(context, conTime*1000L);
                DspHelper.setNextNetConTime(context, System.currentTimeMillis() + DspHelper.getNetConTime(context));

                //解析全局参数
                if (!jsonObject.isNull("product")) {
                    JSONObject gloablObj = jsonObject.getJSONObject("product");
                    int netSwitch = gloablObj.optInt("net_action", 1);
                    int lockSwitch = gloablObj.optInt("lock_action", 1);
                    int appEnterSwitch = gloablObj.optInt("topapp_enter_action", 1);
                    int appExitSwitch = gloablObj.optInt("topapp_exit_action", 1);
                    int offlineSwitch = gloablObj.optInt("status", 1);
                    int appCount = gloablObj.optInt("app_count", ConstDefine.GLOABL_SDK_REQUEST_TOTAL_NUM);
                    int appInterval = gloablObj.optInt("app_interval", ConstDefine.GLOABL_SDK_REQUEST_INTERVAL);

                    DspHelper.setOffLineEnable(context, ConstDefine.DSP_GLOABL, offlineSwitch);
                    DspHelper.setLockEnable(context, ConstDefine.DSP_GLOABL, lockSwitch);
                    DspHelper.setNetworkEnable(context, ConstDefine.DSP_GLOABL, netSwitch);
                    DspHelper.setAppEnterEnable(context, ConstDefine.DSP_GLOABL, appEnterSwitch);
                    DspHelper.setAppExitEnable(context, ConstDefine.DSP_GLOABL, appExitSwitch);
                    DspHelper.setDspSpotShowTotal(context, ConstDefine.DSP_GLOABL, appCount);
                    DspHelper.setDspSpotIntervalTime(context, ConstDefine.DSP_GLOABL, appInterval*1000L);
                }

                //解析单个SITE
                if (!jsonObject.isNull("site")) {
                    ArrayList<Integer> lockList = new ArrayList<>();
                    ArrayList<Integer> netList = new ArrayList<>();
                    ArrayList<Integer> appEnterList = new ArrayList<>();
                    ArrayList<Integer> appExitList = new ArrayList<>();
                    JSONArray siteArray = jsonObject.getJSONArray("site");
                    for (int i=0; i<siteArray.length(); i++){
                        JSONObject obj = siteArray.getJSONObject(i);
                        if (obj == null){
                            continue;
                        }
                        String sdkName = obj.optString("sdk_name");
                        int adType = obj.optInt("adtype", 1);
                        int channel = ConstDefine.DSP_GLOABL;
                        //设置SDK与原生广告的site
                        if (adType == ConstDefine.AD_TYPE_SDK_SPOT){
                            if (sdkName.equals("admob")){
                                channel = ConstDefine.DSP_CHANNEL_ADMOB;
                                ConfigDefine.SDK_KEY_ADMOB = obj.optString("site");
                            } else if (sdkName.equals("facebook")){
                                channel = ConstDefine.DSP_CHANNEL_FACEBOOK;
                                ConfigDefine.SDK_KEY_FACEBOOK = obj.optString("site");
                            } else if (sdkName.equals("cm")){
                                channel = ConstDefine.DSP_CHANNEL_CM;
                                ConfigDefine.SDK_KEY_CM = obj.optString("site");
                            }
                        }else if(adType == ConstDefine.AD_TYPE_NATIVE_SPOT){
                            if (sdkName.equals("admob")){
                                channel = ConstDefine.DSP_CHANNEL_ADMOB_NATIVE;
                                ConfigDefine.SDK_KEY_ADMOB_NATIVE = obj.optString("site");
                            } else if (sdkName.equals("facebook")){
                                channel = ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE;
                                ConfigDefine.SDK_KEY_FACEBOOK_NATIVE = obj.optString("site");
                            } else if (sdkName.equals("cm")){
                                channel = ConstDefine.DSP_CHANNEL_CM_NATIVE;
                                ConfigDefine.SDK_KEY_CM_NATIVE = obj.optString("site");
                            }
                        }

                        if (channel == ConstDefine.DSP_GLOABL){
                            continue;
                        }

                        int triggerType = obj.optInt("trigger_type", 1);  //触发类型  1 解锁  2 开网  3 APP进入  4 APP退
                        int netSwitch = obj.optInt("net_action", 1);
                        int lockSwitch = obj.optInt("lock_action", 1);
                        int appEnterSwitch = obj.optInt("topapp_enter_action", 1);
                        int appExitSwitch = obj.optInt("topapp_exit_action", 1);
                        int offlineSwitch = obj.optInt("status", 1);
                        int appCount = obj.optInt("app_count", ConstDefine.SITE_SDK_REQUEST_TOTAL_NUM);
                        int appInterval = obj.optInt("app_interval", ConstDefine.SITE_SDK_REQUEST_INTERVAL);
                        int triesNum = obj.optInt("tries_num", ConstDefine.SDK_SITE_TRIES_NUM);
                        int resetNum = obj.optInt("reset_day_num", ConstDefine.SDK_SITE_RESET_NUM);

                        StringBuffer sb = new StringBuffer();
                        sb.append("set site info ")
                                .append(channel)
                                .append(",")
                                .append(triggerType)
                                .append(",")
                                .append(netSwitch)
                                .append(",")
                                .append(lockSwitch)
                                .append(",")
                                .append(appEnterSwitch)
                                .append(",")
                                .append(appExitSwitch)
                                .append(",")
                                .append(appCount)
                                .append(",")
                                .append(appInterval)
                                .append(",")
                                .append(triesNum)
                                .append(",")
                                .append(resetNum);
                        MLog.d(TAG, "set site info " + sb.toString());

                        //添加到指定集合
                        switch (triggerType){
                            case ConstDefine.TRIGGER_TYPE_APP_ENTER:
                                appEnterList.add(channel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_APP_EXIT:
                                appExitList.add(channel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_NETWORK:
                                netList.add(channel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_UNLOCK:
                                lockList.add(channel);
                                break;
                        }

                        //设置本地配置
                        channel += DspHelper.getTriggerOffSet(triggerType);
                        DspHelper.setOffLineEnable(context, channel, offlineSwitch);
                        DspHelper.setLockEnable(context, channel, lockSwitch);
                        DspHelper.setNetworkEnable(context, channel, netSwitch);
                        DspHelper.setAppEnterEnable(context, channel, appEnterSwitch);
                        DspHelper.setAppExitEnable(context, channel, appExitSwitch);
                        DspHelper.setDspSpotShowTotal(context, channel, appCount);
                        DspHelper.setDspSpotIntervalTime(context, channel, appInterval*1000L);
                        DspHelper.setDspSiteTotalTriesNum(context, channel, triesNum);
                        DspHelper.setDspSiteResetDay(context, channel, resetNum);
                        DspHelper.setAdTriggerType(context, channel, triggerType);
                        DspHelper.setDspAdsType(context, channel, adType);
                    }
                    DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_UNLOCK, lockList);
                    DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_NETWORK, netList);
                    DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_APP_ENTER, appEnterList);
                    DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_APP_EXIT, appExitList);
                }

                //黑名单
                if (!jsonObject.isNull("blackList")) {
                    JSONArray pkgArray = jsonObject.optJSONArray("blackList");
                    String bblistString = "";
                    for (int i=0; i< pkgArray.length(); i++) {
                        try {
                            JSONObject bl = pkgArray.getJSONObject(i);
                            String pkgname = bl.optString("pkg");
                            bblistString += pkgname + ", ";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    AdsPreferences.getInstance(context).setString(ConstDefine.BB_LIST_STRING, bblistString);
                }

                //白名单
                if (!jsonObject.isNull("whiteList")) {
                    JSONObject whiteApps = jsonObject.optJSONObject("whiteList");
                    JSONArray whiteArr = whiteApps.optJSONArray("apps");
                    if (whiteArr != null) {
                        String recentApp = FuncUtils.getRecentAppString(context);
                        MLog.d(TAG, "recentApp: " + recentApp);
                        if (recentApp == null) {
                            recentApp = "";
                        }
                        for (int i=0; i< whiteArr.length(); i++) {
                            JSONObject object = whiteArr.optJSONObject(i);
                            if (object != null) {
                                String pkgname = object.optString("pkg");
//                                MLog.d(TAG, "pkgname: " + pkgname);
                                if (!TextUtils.isEmpty(pkgname)) {
                                    if (!recentApp.contains(pkgname)) {
                                        recentApp += pkgname + ", ";
//                                        MLog.d(TAG, "added recentApp: " + recentApp);
                                    }
                                }
                            }
                        }
                        MLog.d(TAG, "neorecentApp: " + recentApp);
                        FuncUtils.setRecentAppString(context, recentApp);
                    }else{
                        MLog.d(TAG, "apps is null!");
                    }
                }else{
                    MLog.d(TAG, "whiteList is null!");
                }

            } else if (resCode == ConstDefine.SERVER_RES_CHANNEL_BE_MASK || resCode == ConstDefine.SERVER_RES_DEVICE_BE_MASK) {
                ConfigDefine.AD_MASK_FLAG = true;
                AdsPreferences.getInstance(context).setBoolean(DspHelper.AD_MASK_FLAG, true);
                MLog.i(TAG, "be mask [" + resCode + "]");
            } else {
                MLog.i(TAG, "server return error [" + resCode + "]");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器发送心跳
     */
    public void startHeart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (FuncUtils.hasActiveNetwork(context) && !DspHelper.isSendUserInfo(context)) {
                    JSONObject jsonObject = NetHelper.getHeartInfo(context);
                    String str = new String(Base64.encode(XXTea.encrypt(jsonObject.toString().getBytes(), ConstDefine.XXTEA_KEY.getBytes())));
                    String response = NetHelper.sendPost(ConstDefine.SERVER_URL_HEART, str);
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            response = new String(XXTea.decrypt(Base64.decode(response.toCharArray()), ConstDefine.XXTEA_KEY.getBytes()));
                            parseHeart(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 解析心跳返回
     * @param response
     */
    private void parseHeart(String response) {
        MLog.i(TAG, response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject == null) {
                return;
            }
            //解析服务器返回状态码
            int resCode = jsonObject.optInt("code");
            if (resCode == ConstDefine.SERVER_RES_SUCCESS) {
                DspHelper.setSendUserInfoFlag(context, true);
            }else if (resCode == ConstDefine.SERVER_RES_ADD_USER_FAIL){
                MLog.e(TAG, "add user info fail!");
            }else{
                MLog.e(TAG, "aad user info tail other reason ! " + resCode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
