package com.duduws.ads.main;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cmcm.adsdk.BitmapListener;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.CMAdManagerFactory;
import com.cmcm.adsdk.ImageDownloadListener;
import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.model.CallbackListener;
import com.duduws.ads.utils.DspHelper;
import com.duduws.ads.utils.FuncUtils;
import com.duduws.ads.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Pengz on 16/7/20.
 */
public class AdsManager {
    private static final String TAG = "AdApplication";
    private Context context;
    private static AdsManager instance = null;
    private static String CM_APP_ID = "";
    public CallbackListener listener = null;

    private ArrayList<Integer> lockList = new ArrayList<>();
    private ArrayList<Integer> netList = new ArrayList<>();
    private ArrayList<Integer> appEnterList = new ArrayList<>();
    private ArrayList<Integer> appExitList = new ArrayList<>();

    private AdsManager(Context context){
        this.context = context;
    }

    public static AdsManager getInstance(Context context){
        if (instance == null){
            instance = new AdsManager(context);
        }
        return instance;
    }

    /**
     * 展示广告
     * @param id  广告编号  1 FB  2 ADMOB  3 CM
     * @param type  类型  1 插屏  2 视频
     */
    public void show(int id, int type, CallbackListener listener){
        this.listener = listener;
        long time = DspHelper.getDspSpotLastTime(context);
        if (!DateUtils.isToday(time)){
            //重置数据
            DspHelper.resetData(context);
        }

        int channel = DspHelper.getDspSpotLockChannel(context);
        if (channel != ConstDefine.DSP_GLOABL) {
            if (FuncUtils.hasActiveNetwork(context)) {
                //展示广告
                DspHelper.showAds(context, channel, ConstDefine.TRIGGER_TYPE_UNLOCK);
            }else{
                listener.onCallback(-2, null);
            }
        }else{
            listener.onCallback(-1, null);
        }
    }

    public void init() {
        //设置打印日志
        MLog.setLogEnable(true);

        //初始化信息
        initConfigInfo();

        //初始化SDK
        if (!TextUtils.isEmpty(CM_APP_ID)) {
            CMAdManager.applicationInit(context, CM_APP_ID, "");
            CMAdManagerFactory.setImageDownloadListener(new MyImageLoadListener());
            //是否允许打印日志
            CMAdManager.enableLog();
        }else{
            MLog.e(TAG, "CM appId is empty !");
        }
    }

    private void initConfigInfo(){
        ConfigDefine.APP_KEY_UMENG      = FuncUtils.getManifestApplicationMetaData(context, "APP_KEY_UMENG");
        ConfigDefine.APP_VERSION 		= FuncUtils.getManifestApplicationMetaData(context, "APP_VERSION");
        ConfigDefine.APP_CHANNEL_ID		= FuncUtils.getManifestApplicationMetaData(context, "APP_CHANNEL_ID");
        ConfigDefine.APP_COOPERATION_ID	= FuncUtils.getManifestApplicationMetaData(context, "APP_COOPERATION_ID");
        ConfigDefine.APP_PRODUCT_ID		= FuncUtils.getManifestApplicationMetaData(context, "APP_PRODUCT_ID");
        ConfigDefine.APP_PROTOCOL		= FuncUtils.getManifestApplicationMetaData(context, "APP_PROTOCOL");

        //设置渠道Site
        String fb_site = FuncUtils.getManifestApplicationMetaData(context, "SITE_FACEBOOK");
        initSiteInfo(ConstDefine.DSP_CHANNEL_FACEBOOK, fb_site);
        String admob_site = FuncUtils.getManifestApplicationMetaData(context, "SITE_ADMOB");
        initSiteInfo(ConstDefine.DSP_CHANNEL_ADMOB, admob_site);
        String cm_site = FuncUtils.getManifestApplicationMetaData(context, "SITE_CM");
        initSiteInfo(ConstDefine.DSP_CHANNEL_CM, cm_site);
        DspHelper.DSP_MAP.clear();
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_UNLOCK, lockList);
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_NETWORK, netList);
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_APP_ENTER, appEnterList);
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_APP_EXIT, appExitList);

        //设置默认值
        //全局DSP时间间隔
        String dspIntervalGloabl = FuncUtils.getManifestApplicationMetaData(context, "GLOABL_INTERVAL");
        dspIntervalGloabl = dspIntervalGloabl.substring(3, dspIntervalGloabl.length());
        DspHelper.setDspSpotIntervalTime(context, ConstDefine.DSP_GLOABL, Integer.parseInt(dspIntervalGloabl)*1000L);
        //单个SITE时间间隔
        String siteInterval = FuncUtils.getManifestApplicationMetaData(context, "SITE_INTERVAL");
        siteInterval = siteInterval.substring(3, siteInterval.length());
        DspHelper.setDspSpotIntervalTime(context, ConstDefine.DSP_CHANNEL_FACEBOOK, Integer.parseInt(siteInterval)*1000L);
        DspHelper.setDspSpotIntervalTime(context, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, Integer.parseInt(siteInterval)*1000L);
        DspHelper.setDspSpotIntervalTime(context, ConstDefine.DSP_CHANNEL_ADMOB, Integer.parseInt(siteInterval)*1000L);
        DspHelper.setDspSpotIntervalTime(context, ConstDefine.DSP_CHANNEL_CM, Integer.parseInt(siteInterval)*1000L);
        //网络连接时间
        String networkTime = FuncUtils.getManifestApplicationMetaData(context, "NETWORK_TIME");
        networkTime = networkTime.substring(3, networkTime.length());
        DspHelper.setNetConTime(context, Integer.parseInt(networkTime));

        MLog.e(TAG, "APP_KEY_UMENG = " + ConfigDefine.APP_KEY_UMENG);
        MLog.e(TAG, "APP_VERSION = " + ConfigDefine.APP_VERSION);
        MLog.e(TAG, "APP_CHANNEL_ID = " + ConfigDefine.APP_CHANNEL_ID);
        MLog.e(TAG, "APP_COOPERATION_ID = " + ConfigDefine.APP_COOPERATION_ID);
        MLog.e(TAG, "APP_PRODUCT_ID = " + ConfigDefine.APP_PRODUCT_ID);
        MLog.e(TAG, "APP_PROTOCOL = " + ConfigDefine.APP_PROTOCOL);
        MLog.e(TAG, "GLOABL_INTERVAL = " + dspIntervalGloabl);
        MLog.e(TAG, "SITE_INTERVAL = " + siteInterval);
        MLog.e(TAG, "NETWORK_TIME = " + networkTime);

        Iterator iterator = DspHelper.DSP_MAP.entrySet().iterator();
        while (iterator.hasNext()){
            MLog.e(TAG, "----------------------------------------");
            Map.Entry entry = (Map.Entry)iterator.next();
            int key = (Integer)entry.getKey();
            ArrayList<Integer> val = (ArrayList<Integer>) entry.getValue();
            MLog.e(TAG, "key --> " + key + "  size: " + val.size());
            for (int i=0; i<val.size(); i++){
                MLog.e(TAG, "" + val.get(i));
            }
            MLog.e(TAG, "----------------------------------------");
        }
    }

    private void initSiteInfo(int type, String json){
        try {
            int diff = ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE - ConstDefine.DSP_CHANNEL_FACEBOOK;
            JSONObject jsonObject = new JSONObject(json);
            if (!jsonObject.isNull("sdk")){
                JSONObject sdk = jsonObject.optJSONObject("sdk");
                initSubSite(type, sdk);
            }
            if (!jsonObject.isNull("native")){
                JSONObject nativeObj = jsonObject.optJSONObject("native");
                initSubSite(type+diff, nativeObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initSubSite(int channel, JSONObject jsonObject){
        //解锁
        if (!jsonObject.isNull("unlock")){
            if (!TextUtils.isEmpty(jsonObject.optString("unlock"))){
                lockList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_UNLOCK);
            DspHelper.setDspSite(context, offset, jsonObject.optString("unlock"));
            if (channel == ConstDefine.DSP_CHANNEL_CM || channel == ConstDefine.DSP_CHANNEL_CM_NATIVE){
                String cmId = jsonObject.optString("unlock");
                if (!TextUtils.isEmpty(cmId)){
                    CM_APP_ID = cmId.substring(0, 4);
                }
            }
        }
        //开网
        if (!jsonObject.isNull("net")){
            if (!TextUtils.isEmpty(jsonObject.optString("net"))){
                netList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_NETWORK);
            DspHelper.setDspSite(context, offset, jsonObject.optString("net"));
        }
        //APP进入
        if (!jsonObject.isNull("enter")){
            if (!TextUtils.isEmpty(jsonObject.optString("enter"))){
                appEnterList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_APP_ENTER);
            DspHelper.setDspSite(context, offset, jsonObject.optString("enter"));
        }
        //APP退出
        if (!jsonObject.isNull("exit")){
            if (!TextUtils.isEmpty(jsonObject.optString("exit"))){
                appExitList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_APP_EXIT);
            DspHelper.setDspSite(context, offset, jsonObject.optString("exit"));
        }
    }

    /**
     * Image loader must setted  if you integrate interstitial Ads in your App.
     */
    class MyImageLoadListener implements ImageDownloadListener {

        @Override
        public void getBitmap(String url, final BitmapListener imageListener) {
            if(TextUtils.isEmpty(url)){
                if(imageListener != null) {
                    imageListener.onFailed("url is null");
                }
                return;
            }
            //You can use your own VolleyUtil for image loader
            VolleyUtil.loadImage(url, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (imageListener != null) {
                        imageListener.onFailed(volleyError.getMessage());
                    }
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (imageContainer != null && imageContainer.getBitmap() != null) {
                        if (imageListener != null) {
                            imageListener.onSuccessed(imageContainer.getBitmap());
                        }
                    }
                }
            });
        }
    }
}
