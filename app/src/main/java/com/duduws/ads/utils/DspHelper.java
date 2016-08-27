package com.duduws.ads.utils;

import android.content.Context;
import android.content.Intent;

import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.view.AdmobActivity;
import com.duduws.ads.view.CmActivity;
import com.duduws.ads.view.FacebookActivity;
import com.duduws.ads.view.Facebook_Native_Activity;

import java.util.ArrayList;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/3 10:00
 */
public class DspHelper {
    private static final String TAG = "DspHelper";
    //插屏数组
    private static ArrayList<Integer> DSP_SPOT_LIST = new ArrayList<Integer>();
    //banner数组
    private static ArrayList<Integer> DSP_BANNER_LIST = new ArrayList<Integer>();

    //App进入退出数组
    private static ArrayList<Integer> DSP_APP_LIST = new ArrayList<Integer>();

    static {
        //设置默认值
        DSP_SPOT_LIST.add(ConstDefine.DSP_CHANNEL_FACEBOOK);
        DSP_SPOT_LIST.add(ConstDefine.DSP_CHANNEL_CM);
        DSP_SPOT_LIST.add(ConstDefine.DSP_CHANNEL_ADMOB);
        //App的进入和退出单独走控制流程，目前只触发facebook native
        DSP_APP_LIST.add(ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE);
    }

    //延时广告标志
    private static final String DELAY_ADS_FLAG = StrUtils.deCrypt("delay_ads_flag");
    //当前是否有广告弹出
    private static final String CURRENT_ADS_SHOW_FLAG = StrUtils.deCrypt("current_ads_show_flag");
    private static boolean CURRENT_ADS_SHOW_FLAG_EX = false;
    //广告屏敝标志
    public static final String AD_MASK_FLAG = StrUtils.deCrypt("ad_mask_flag");
    //解锁开关
    private static final String DSP_FLAG_LOCK = StrUtils.deCrypt("dsp_flag_lock");
    //应用进入开关
    private static final String DSP_FLAG_ENTER = StrUtils.deCrypt("dsp_flag_enter");
    //应用退出开关
    private static final String DSP_FLAG_EXIT = StrUtils.deCrypt("dsp_flag_exit");
    //网络变化开关
    private static final String DSP_FLAG_NET = StrUtils.deCrypt("dsp_flag_net");
    //最后一次插屏的展示时间
    private static final String DSP_SPOT_LAST_SHOW_TIME = StrUtils.deCrypt("dsp_spot_last_show_time");
    //单个SITE展示的时间间隔
    private static final String DSP_SITE_SHOW_INTERVAL = StrUtils.deCrypt("dsp_site_spot_interval");
    //单个SITE下次展示的时间
    private static final String DSP_SITE_SHOW_NEXT_TIME = StrUtils.deCrypt("dsp_site_show_next_time");
    //单个SITE展示的总次数
    private static final String DSP_SITE_SHOW_TOTAL = StrUtils.deCrypt("dsp_site_show_total");
    //单个SITE已经展示的次数
    private static final String DSP_SITE_SHOW_NUM = StrUtils.deCrypt("dsp_site_show_num");
    //单个SITE已经请求的次数
    private static final String DSP_SITE_REQUEST_NUM = StrUtils.deCrypt("dsp_site_request_num");
    //渠道尝试次数重置周期
    private static final String SDK_SITE_RESET_NUM = StrUtils.deCrypt("sdk_site_reset_num");
    //渠道尝试次数
    private static final String SDK_SITE_TRIES_NUM = StrUtils.deCrypt("sdk_site_tries_num");
    //渠道尝试总次数
    private static final String SDK_SITE_TRIES_TOTAL_NUM = StrUtils.deCrypt("sdk_site_tries_total_num");
    //渠道尝试次数用完标志
    private static final String SDK_SITE_TRIES_FLAG = StrUtils.deCrypt("sdk_site_tries_flag");
    //尝试次数达到上限的时间
    private static final String SDK_SITE_TRIES_TIME = StrUtils.deCrypt("sdk_site_tries_time");
    //触发类型
    public static final String AD_TRIGGER_TYPE = StrUtils.deCrypt("ad_trigger_type");
    //附带广告
    public static final String AD_EXTRA_SITE = StrUtils.deCrypt("ad_extra_site");
    //连接网络时间
    private static final String NET_CONN_TIME = StrUtils.deCrypt("net_conn_time");
    //心跳时间
    private static final String NET_HEART_TIME = StrUtils.deCrypt("net_heart_time");
    //默认联网时间间隔
    private static final String DEFAULT_NEXT_CONNECT_TIME = StrUtils.deCrypt("default_next_connect_time");
    //默认心跳时间间隔
    private static final String DEFAULT_NEXT_HEART_TIME = StrUtils.deCrypt("default_next_heart_time");
    //广告位触发类型
    private static final String DSP_ADS_TRIGGER_TYPE = StrUtils.deCrypt("dsp_ads_trigger_type");
    //广告位类型
    private static final String DSP_ADS_TYPE = StrUtils.deCrypt("dsp_ads_type");

    /**
     * 设置当前广告展示标志
     * @param context
     * @param flag
     */
    public static void setCurrentAdsShowFlag(Context context, boolean flag){
//        AdsPreferences.getInstance(context).setBoolean(CURRENT_ADS_SHOW_FLAG, flag);
        CURRENT_ADS_SHOW_FLAG_EX = flag;
    }

    /**
     * 获取当前广告展示标志
     * @param context
     * @return
     */
    public static boolean getCurrentAdsShowFlag(Context context){
//        return AdsPreferences.getInstance(context).getBoolean(CURRENT_ADS_SHOW_FLAG, false);
        return CURRENT_ADS_SHOW_FLAG_EX;
    }

    /**
     * 设置网络连接时间间隔
     * @param context
     * @param time
     */
    public static void setNetConTime(Context context, long time) {
        AdsPreferences.getInstance(context).setLong(DEFAULT_NEXT_CONNECT_TIME, time);
    }

    /**
     * 获取网络连接时间间隔
     * @param context
     * @return
     */
    public static long getNetConTime(Context context) {
        return AdsPreferences.getInstance(context).getLong(DEFAULT_NEXT_CONNECT_TIME, ConstDefine.DEFAULT_NEXT_CONNECT_TIME*1000L);
    }

    /**
     * 设置下次联网时间
     * @param context
     * @param time
     */
    public static void setNextNetConTime(Context context, long time) {
        AdsPreferences.getInstance(context).setLong(NET_CONN_TIME, time);
    }

    /**
     * 获取下次联网时间
     * @param context
     * @return
     */
    public static long getNextNetConTime(Context context) {
        return AdsPreferences.getInstance(context).getLong(NET_CONN_TIME, 0L);
    }

    /**
     * 设置心跳时间间隔
     * @param context
     * @param time
     */
    public static void setHeartConTime(Context context, long time) {
        AdsPreferences.getInstance(context).setLong(DEFAULT_NEXT_HEART_TIME, time);
    }

    /**
     * 获取心跳时间间隔
     * @param context
     * @return
     */
    public static long getHeartConTime(Context context) {
        return AdsPreferences.getInstance(context).getLong(DEFAULT_NEXT_HEART_TIME, ConstDefine.DEFAULT_NEXT_HEART_TIME*1000L);
    }

    /**
     * 设置下次心跳时间
     * @param context
     * @param time
     */
    public static void setNextHeartConTime(Context context, long time) {
        AdsPreferences.getInstance(context).setLong(NET_HEART_TIME, time);
    }

    /**
     * 获取下次心跳时间
     * @param context
     * @return
     */
    public static long getNextNetHeartTime(Context context) {
        return AdsPreferences.getInstance(context).getLong(NET_HEART_TIME, 0L);
    }

    /**
     * 设置插屏数组
     * @param list
     */
    public static void setDspSpotList(ArrayList<Integer> list) {
        DSP_SPOT_LIST = list;
    }

    /**
     * 设置banner数组
     * @param list
     */
    public static void setDspBannerList(ArrayList<Integer> list) {
        DSP_BANNER_LIST = list;
    }

    /**
     * 设置App进入退出数组
     * @param list
     */
    public static void setDspAppList(ArrayList<Integer> list) {
        DSP_APP_LIST = list;
    }

    /**
     * 重置数据
     * @param context
     */
    public static void resetData(Context context) {
        MLog.e(TAG, "DspHelper resetData !!!!!!!!!!!!!!!!!!!!!!!!!!");
        //清除全局次数
        setDspSpotRequestNum(context, ConstDefine.DSP_GLOABL, 0);
        setDspSpotShowNum(context, ConstDefine.DSP_GLOABL, 0);
        //清单个SITE次数
        for (int i=0; i<DSP_SPOT_LIST.size(); i++){
            int channel = DSP_SPOT_LIST.get(i);
            setDspSpotRequestNum(context, channel, 0);
            setDspSpotShowNum(context, channel, 0);
        }
    }

    /**
     * 更新请求数据
     * @param context
     */
    public static void updateRequestData(Context context, int channel) {
        //更新全局次数
        setDspSpotRequestNum(context, ConstDefine.DSP_GLOABL, getDspSpotRequestNum(context, ConstDefine.DSP_GLOABL)+1);
        //更新单个SITE次数
        setDspSpotRequestNum(context, channel, getDspSpotRequestNum(context, channel)+1);
    }

    /**
     * 更新展示数据
     * @param context
     */
    public static void updateShowData(Context context, int channel) {
        //更新全局次数
        setDspSpotShowNum(context, ConstDefine.DSP_GLOABL, getDspSpotShowNum(context, ConstDefine.DSP_GLOABL)+1);
        //更新单个SITE次数
        setDspSpotShowNum(context, channel, getDspSpotShowNum(context, channel)+1);
    }

    /**
     * 展示广告
     * @param context
     * @param channel
     * @param triggerType
     */
    public static void showAds(Context context, int channel, int triggerType)  {
        showAds(context, channel, triggerType, false);
    }

    /**
     * 展示广告
     * @param context
     * @param channel
     * @param triggerType
     * @param isOutSide  是否不受次数控制   true: 不受控  false: 受控
     */
    public static void showAds(Context context, int channel, int triggerType, boolean isOutSide){
        MLog.e(TAG, "DspHelper showAds channel: " + channel + ", triggerType: " + triggerType + ", isOutSide: " + isOutSide);
        //更新下次展示的时间
        if (!isOutSide){
            setDspSpotNextTime(context, ConstDefine.DSP_GLOABL);
            setDspSpotNextTime(context, channel);
            setDspSpotLastTime(context, System.currentTimeMillis());
        }
        openActivity(context, channel, triggerType, isOutSide);
    }

    /**
     * 打开一个Activity
     * @param context
     * @param channel
     * @param triggerType
     * @param isOutSide
     */
    private static synchronized void openActivity(Context context, int channel, int triggerType, boolean isOutSide){
        if (getCurrentAdsShowFlag(context)){
            MLog.e(TAG, "openActivity already open ads! channel: " + channel + ", triggerType: " + triggerType + ", isOutSide: " + isOutSide);
            return;
        }
        setCurrentAdsShowFlag(context, true);
        //打开相应渠道的广告
        Intent intent = new Intent();
        if (channel == ConstDefine.DSP_CHANNEL_ADMOB) {
            intent.setClass(context.getApplicationContext(), AdmobActivity.class);
        } else if (channel == ConstDefine.DSP_CHANNEL_FACEBOOK){
            intent.setClass(context.getApplicationContext(), FacebookActivity.class);
        } else if (channel == ConstDefine.DSP_CHANNEL_CM){
            intent.setClass(context.getApplicationContext(), CmActivity.class);
        } else if (channel == ConstDefine.DSP_CHANNEL_ADMOB_NATIVE) {
            return;
        } else if (channel == ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE) {
            intent.setClass(context.getApplicationContext(), Facebook_Native_Activity.class);
        } else if (channel == ConstDefine.DSP_CHANNEL_CM_NATIVE) {
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AD_TRIGGER_TYPE, triggerType);
        intent.putExtra(AD_EXTRA_SITE, isOutSide);
        context.startActivity(intent);
    }

    /**
     * 设置广告位的触发类型
     * @param context
     * @param channel
     * @param type
     */
    public static void setAdTriggerType(Context context, int channel, int type){
        AdsPreferences.getInstance(context).setInt(channel, DSP_ADS_TRIGGER_TYPE, type);
    }

    /**
     * 获取广告位的触发类型
     * @param context
     * @param channel
     * @return
     */
    public static int getAdTriggerType(Context context, int channel){
        return AdsPreferences.getInstance(context).getInt(channel, DSP_ADS_TRIGGER_TYPE, 0);
    }

    /**
     * 设置广告位类型
     * @param context
     * @param channel
     * @param type  1 spot  2 banner  3 native
     */
    public static void setDspAdsType(Context context, int channel, int type){
        AdsPreferences.getInstance(context).setInt(channel, DSP_ADS_TYPE, type);
    }

    /**
     * 获取广告位类型
     * @param context
     * @param channel
     * @return
     */
    public static int getDspAdsType(Context context, int channel){
        return AdsPreferences.getInstance(context).getInt(channel, DSP_ADS_TYPE, 1);
    }

    /**
     * 获取是否允许延时弹出广告
     * @param context
     * @param channel
     * @return
     */
    public static boolean isDelayShowAdsEnable(Context context, int channel){
        return AdsPreferences.getInstance(context).getBoolean(channel, DELAY_ADS_FLAG, true);
    }

    /**
     * 设置是否允许延时弹出广告
     * @param context
     * @param channel
     * @param flag
     */
    public static void setDelayAdsFlag(Context context, int channel, boolean flag){
        MLog.i(TAG, "setDelayAdsFlag channel: " + channel + ", flag: " + flag);
        AdsPreferences.getInstance(context).setBoolean(channel, DELAY_ADS_FLAG, flag);
    }

    /**
     * 解锁是否开放
     * @param context
     * @param channel
     * @return
     */
    public static boolean isLockEnable(Context context, int channel) {
        boolean ret = false;
        int value = AdsPreferences.getInstance(context).getInt(channel, DSP_FLAG_LOCK, 1);
        if (value == 1){
            ret = true;
        }
        return ret;
    }

    /**
     * 设置解锁开关
     * @param context
     * @param channel
     * @param value
     */
    public static void setLockEnable(Context context, int channel, int value) {
        AdsPreferences.getInstance(context).setInt(channel, DSP_FLAG_LOCK, value);
    }

    /**
     * 应用进入是否开放
     * @param context
     * @param channel
     * @return
     */
    public static boolean isAppEnterEnable(Context context, int channel) {
        boolean ret = false;
        int value = AdsPreferences.getInstance(context).getInt(channel, DSP_FLAG_ENTER, 1);
        if (value == 1){
            ret = true;
        }
        return ret;
    }

    /**
     * 设置应用进入开关
     * @param context
     * @param channel
     * @param value
     */
    public static void setAppEnterEnable(Context context, int channel, int value) {
        AdsPreferences.getInstance(context).setInt(channel, DSP_FLAG_ENTER, value);
    }

    /**
     * 应用退出是否开放
     * @param context
     * @param channel
     * @return
     */
    public static boolean isAppExitEnable(Context context, int channel) {
        boolean ret = false;
        int value = AdsPreferences.getInstance(context).getInt(channel, DSP_FLAG_EXIT, 1);
        if (value == 1){
            ret = true;
        }
        return ret;
    }

    /**
     * 设置应用退出开关
     * @param context
     * @param channel
     * @param value
     */
    public static void setAppExitEnable(Context context, int channel, int value) {
        AdsPreferences.getInstance(context).setInt(channel, DSP_FLAG_EXIT, value);
    }

    /**
     * 网络变化是否开放
     * @param context
     * @param channel
     * @return
     */
    public static boolean isNetworkChangeEnable(Context context, int channel) {
        boolean ret = false;
        int value = AdsPreferences.getInstance(context).getInt(channel, DSP_FLAG_NET, 1);
        if (value == 1){
            ret = true;
        }
        return ret;
    }

    /**
     * 设置网络开关
     * @param context
     * @param channel
     * @param value
     */
    public static void setNetworkEnable(Context context, int channel, int value) {
        AdsPreferences.getInstance(context).setInt(channel, DSP_FLAG_NET, value);
    }

    /**
     * 设置最后一次插屏出现的时间
     * @param context
     * @param time
     */
    public static void setDspSpotLastTime(Context context, long time) {
        AdsPreferences.getInstance(context).setLong(DSP_SPOT_LAST_SHOW_TIME, time);
    }

    /**
     * 获取最后一次插屏出现的时间
     * @param context
     * @return
     */
    public static long getDspSpotLastTime(Context context) {
        return AdsPreferences.getInstance(context).getLong(DSP_SPOT_LAST_SHOW_TIME, 0);
    }

    /**
     * 设置插屏时间间隔
     * @param context
     * @param channel
     * @param interval
     */
    public static void setDspSpotIntervalTime(Context context, int channel, long interval) {
        AdsPreferences.getInstance(context).setLong(channel, DSP_SITE_SHOW_INTERVAL, interval);
    }

    /**
     * 设置插屏时间间隔
     * @param context
     * @param channel
     * @return
     */
    public static long getDspSpotIntervalTime(Context context, int channel) {
        long defValue = ConstDefine.GLOABL_SDK_REQUEST_INTERVAL;
        if (channel != ConstDefine.DSP_GLOABL) {
            defValue = ConstDefine.SITE_SDK_REQUEST_INTERVAL;
        }
        return AdsPreferences.getInstance(context).getLong(channel, DSP_SITE_SHOW_INTERVAL, defValue*1000L);
    }

    /**
     * 设置下一次插屏出现的时间
     * @param context
     * @param channel
     */
    public static void setDspSpotNextTime(Context context, int channel) {
        String key = new StringBuffer().append(DSP_SITE_SHOW_NEXT_TIME)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
        long interval = getDspSpotIntervalTime(context, channel);
        long curTime = System.currentTimeMillis();
        long nextTime = curTime + interval;
        AdsPreferences.getInstance(context).setLong(channel, key, nextTime);
    }

    /**
     * 获取下一次插屏出现的时间
     * @param context
     * @param channel
     * @return
     */
    public static long getDspSpotNextTime(Context context, int channel) {
        String key = new StringBuffer().append(DSP_SITE_SHOW_NEXT_TIME)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
        return AdsPreferences.getInstance(context).getLong(channel, key, 0);
    }

    /**
     * 设置插屏展示的总次数
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSpotShowTotal(Context context, int channel, int num) {
        String key = new StringBuffer().append(DSP_SITE_SHOW_TOTAL)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
        AdsPreferences.getInstance(context).setInt(channel, key, num);
    }

    /**
     * 获取插屏的展示总次数
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSpotShowTotal(Context context, int channel) {
        int defValue = ConstDefine.GLOABL_SDK_REQUEST_TOTAL_NUM;
        if (channel != ConstDefine.DSP_GLOABL){
            defValue = ConstDefine.SITE_SDK_REQUEST_TOTAL_NUM;
        }
        String key = new StringBuffer().append(DSP_SITE_SHOW_TOTAL)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
        return AdsPreferences.getInstance(context).getInt(channel, key, defValue);
    }

    /**
     * 设置插屏展示的次数
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSpotShowNum(Context context, int channel, int num) {
        String key = new StringBuffer().append(DSP_SITE_SHOW_NUM)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
//        MLog.i(TAG, "setDspSpotShowNum key " + key + " channel " + channel + "  num " + num);
        AdsPreferences.getInstance(context).setInt(channel, key, num);
    }

    /**
     * 获取插屏的展示次数
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSpotShowNum(Context context, int channel) {
        String key = new StringBuffer().append(DSP_SITE_SHOW_NUM)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
        int num = AdsPreferences.getInstance(context).getInt(channel, key, 0);
//        MLog.i(TAG, "getDspSpotShowNum key " + key + " channel " + channel + " num " + num);
        return num;
    }

    /**
     * 设置插屏请求的次数
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSpotRequestNum(Context context, int channel, int num) {
        String key = new StringBuffer().append(DSP_SITE_REQUEST_NUM)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
//        MLog.i(TAG, "setDspSpotRequestNum key " + key + " channel " + channel + "  num " + num);
        AdsPreferences.getInstance(context).setInt(channel, key, num);
    }

    /**
     * 获取插屏的请求次数
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSpotRequestNum(Context context, int channel) {
        String key = new StringBuffer().append(DSP_SITE_REQUEST_NUM)
                                        .append("_")
                                        .append(ConstDefine.AD_TYPE_SDK_SPOT)
                                        .toString().trim();
        int num = AdsPreferences.getInstance(context).getInt(channel, key, 0);
//        MLog.i(TAG, "getDspSpotRequestNum key " + key + " channel " + channel + " num " + num);
        return num;
    }

    /**
     * 获取渠道尝试次数清零周期
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSiteResetDay(Context context, int channel) {
        String key = new StringBuffer().append(SDK_SITE_RESET_NUM)
                                        .toString().trim();
        return AdsPreferences.getInstance(context).getInt(channel, key, ConstDefine.SDK_SITE_RESET_NUM);
    }

    /**
     * 设置渠道尝试次数清零周期
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSiteResetDay(Context context, int channel, int num) {
        String key = new StringBuffer().append(SDK_SITE_RESET_NUM)
                                        .toString().trim();
        AdsPreferences.getInstance(context).setInt(channel, key, num);
    }

    /**
     * 获取渠道已尝试次数
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSiteTriesNum(Context context, int channel) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_NUM)
                                        .toString().trim();
        return AdsPreferences.getInstance(context).getInt(channel, key, 0);
    }

    /**
     * 设置渠道已尝试次数
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSiteTriesNum(Context context, int channel, int num) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_NUM)
                                        .toString().trim();
        AdsPreferences.getInstance(context).setInt(channel, key, num);
    }

    /**
     * 设置渠道尝试总次数
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSiteTotalTriesNum(Context context, int channel, int num) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_TOTAL_NUM)
                                        .toString().trim();
        AdsPreferences.getInstance(context).setInt(channel, key, num);
    }

    /**
     * 获取渠道尝试总次数
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSiteTotalTriesNum(Context context, int channel) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_TOTAL_NUM)
                                        .toString().trim();
        return AdsPreferences.getInstance(context).getInt(channel, key, ConstDefine.SDK_SITE_TRIES_NUM);
    }

    /**
     * 获取渠道尝试次数是否用完标志
     * @param context
     * @param channel
     * @return
     */
    public static boolean getDspSiteTriesFlag(Context context, int channel) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_FLAG)
                                        .toString().trim();
        return AdsPreferences.getInstance(context).getBoolean(channel, key, false);
    }

    /**
     * 设置渠道尝试次数是否用完标志
     * @param context
     * @param channel
     * @param flag
     */
    public static void setDspSiteTriesFlag(Context context, int channel, boolean flag) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_FLAG)
                                        .toString().trim();
        AdsPreferences.getInstance(context).setBoolean(channel, key, flag);
    }

    /**
     * 获取渠道尝试次数达到上限的时间
     * @param context
     * @param channel
     * @return
     */
    public static long getDspSiteTriesTime(Context context, int channel) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_TIME)
                                        .toString().trim();
        return AdsPreferences.getInstance(context).getLong(channel, key, 0);
    }

    /**
     * 设置渠道尝试次数达到上限的时间
     * @param context
     * @param channel
     * @param time
     */
    public static void setDspSiteTriesTime(Context context, int channel, long time) {
        String key = new StringBuffer().append(SDK_SITE_TRIES_TIME)
                                        .toString().trim();
        AdsPreferences.getInstance(context).setLong(channel, key, time);
    }

    /**
     * 判断某个渠道的尝试次数是否达到上限
     * @param context
     * @param channel
     * @return
     */
    public static boolean isTriesVaild(Context context, int channel) {
        boolean ret = false;
        if (getDspSiteTriesFlag(context, channel)){
            ret = true;
            long time = getDspSiteTriesTime(context, channel);
            if ((System.currentTimeMillis()-time) > getDspSiteResetDay(context, channel)*3600*1000) {
                setDspSiteTriesFlag(context, channel, false);
                setDspSiteTriesNum(context, channel, 0);
                setDspSiteTriesTime(context, channel, 0);
            }
        }
        return ret;
    }

    /**
     * 获取解锁时插屏展示的渠道
     * @param context
     * @return
     */
    public static int getDspSpotLockChannel(Context context) {
        int channel = ConstDefine.DSP_GLOABL;
        //检测是否屏敝广告
        if (AdsPreferences.getInstance(context).getBoolean(AD_MASK_FLAG, ConfigDefine.AD_MASK_FLAG)){
            MLog.i(TAG, "getDspSpotLockChannel mask ad !");
            return channel;
        }
        //检测全局条件
        if (!checkDspSpotLockChannel(context, ConstDefine.DSP_GLOABL)){
            MLog.i(TAG, "getDspSpotLockChannel gloabl condition fail !");
            return channel;
        }
        //检测单个SITE条件
        for (int i=0; i<DSP_SPOT_LIST.size(); i++){
            if (checkDspSpotLockChannel(context, DSP_SPOT_LIST.get(i))){
                channel = DSP_SPOT_LIST.get(i);
                break;
            }
        }
        return channel;
    }

    /**
     * 检测解锁插屏条件
     * @param context
     * @param channel
     * @return
     */
    private static boolean checkDspSpotLockChannel(Context context, int channel) {
        boolean ret = true;
        if (isTriesVaild(context, channel)) {
            MLog.i(TAG, "checkDspSpotLockChannel tries fail !");
            return false;
        }
        //检测锁屏开关是否打开
        if (!isLockEnable(context, channel)){
            MLog.i(TAG, "checkDspSpotLockChannel lock is not enable !");
            return false;
        }
        //检测时间间隔是否满足
        long lastTime = getDspSpotNextTime(context, channel);
        if (System.currentTimeMillis() < lastTime) {
            MLog.i(TAG, "checkDspSpotLockChannel time fail !");
            return false;
        }
        //检测次数是否满足
        int show = getDspSpotShowNum(context, channel);
        int request = getDspSpotRequestNum(context, channel);
        int totalNum = getDspSpotShowTotal(context, channel);
        MLog.i(TAG, "checkDspSpotLockChannel request " + request + " show " + show + " total " + totalNum + " channel " + channel);
        if (show >= totalNum || request >= totalNum*2){
            MLog.i(TAG, "checkDspSpotLockChannel num fail !");
            return false;
        }
        return ret;
    }

    /**
     * 获取网络变化时插屏展示渠道
     * @param context
     * @return
     */
    public static int getDspSpotNetworkChannel(Context context) {
        int channel = ConstDefine.DSP_GLOABL;
        //检测是否屏敝广告
        if (AdsPreferences.getInstance(context).getBoolean(AD_MASK_FLAG, ConfigDefine.AD_MASK_FLAG)){
            MLog.i(TAG, "getDspSpotNetworkChannel mask ad !");
            return channel;
        }
        //检测全局条件
        if (!checkDspSpotNetworkChannel(context, ConstDefine.DSP_GLOABL)){
            MLog.i(TAG, "getDspSpotNetworkChannel gloabl condition fail !");
            return channel;
        }
        //检测单个SITE条件
        for (int i=0; i<DSP_SPOT_LIST.size(); i++){
            if (checkDspSpotNetworkChannel(context, DSP_SPOT_LIST.get(i))){
                channel = DSP_SPOT_LIST.get(i);
                break;
            }
        }
        return channel;
    }

    /**
     * 检测网络变化插屏条件
     * @param context
     * @param channel
     * @return
     */
    private static boolean checkDspSpotNetworkChannel(Context context, int channel) {
        boolean ret = true;
        if (isTriesVaild(context, channel)) {
            MLog.i(TAG, "checkDspSpotNetworkChannel tries fail !");
            return false;
        }
        //检测锁屏开关是否打开
        if (!isNetworkChangeEnable(context, channel)){
            MLog.i(TAG, "checkDspSpotNetworkChannel network is not enable !");
            return false;
        }
        //检测时间间隔是否满足
        long lastTime = getDspSpotNextTime(context, channel);
        if (System.currentTimeMillis() < lastTime) {
            MLog.i(TAG, "checkDspSpotNetworkChannel time fail !");
            return false;
        }
        //检测次数是否满足
        int show = getDspSpotShowNum(context, channel);
        int request = getDspSpotRequestNum(context, channel);
        int totalNum = getDspSpotShowTotal(context, channel);
        if (show >= totalNum || request >= totalNum*2){
            MLog.i(TAG, "checkDspSpotNetworkChannel num fail !");
            return false;
        }
        return ret;
    }

    /**
     * 获取应用进入时插屏展示的渠道
     * @param context
     * @return
     */
    public static int getDspSpotAppEnterChannel(Context context) {
        int channel = ConstDefine.DSP_GLOABL;
        //检测是否屏敝广告
        if (AdsPreferences.getInstance(context).getBoolean(AD_MASK_FLAG, ConfigDefine.AD_MASK_FLAG)){
            MLog.i(TAG, "getDspSpotAppEnterChannel mask ad !");
            return channel;
        }
        //检测全局条件  App进入不受全局条件控制
//        if (!checkDspSpotAppEnterChannel(context, ConstDefine.DSP_GLOABL)){
//            MLog.i(TAG, "getDspSpotAppEnterChannel gloabl condition fail !");
//            return channel;
//        }
        //检测单个SITE条件
        for (int i=0; i<DSP_APP_LIST.size(); i++){
            if (checkDspSpotAppEnterChannel(context, DSP_APP_LIST.get(i))){
                channel = DSP_APP_LIST.get(i);
                break;
            }
        }
        return channel;
    }

    /**
     * 检测应用进入时展示插屏条件
     * @param context
     * @param channel
     * @return
     */
    private static boolean checkDspSpotAppEnterChannel(Context context, int channel) {
        boolean ret = true;
        if (isTriesVaild(context, channel)) {
            MLog.i(TAG, "checkDspSpotAppEnterChannel tries fail ! " + channel);
            return false;
        }
        //检测锁屏开关是否打开
        if (!isAppEnterEnable(context, channel)){
            MLog.i(TAG, "checkDspSpotAppEnterChannel app enter is not enable ! " + channel);
            return false;
        }
        //检测时间间隔是否满足
        long lastTime = getDspSpotNextTime(context, channel);
        if (System.currentTimeMillis() < lastTime) {
            MLog.i(TAG, "checkDspSpotAppEnterChannel time fail ! " + channel + " time:" + lastTime + " - " + System.currentTimeMillis());
            return false;
        }
        //检测次数是否满足
        int show = getDspSpotShowNum(context, channel);
        int request = getDspSpotRequestNum(context, channel);
        int totalNum = getDspSpotShowTotal(context, channel);
        if (show >= totalNum || request >= totalNum*2){
            MLog.i(TAG, "checkDspSpotAppEnterChannel num fail ! " + channel);
            return false;
        }
        return ret;
    }

    /**
     * 获取应用退出时插屏展示的渠道
     * @param context
     * @return
     */
    public static int getDspSpotAppExitChannel(Context context) {
        int channel = ConstDefine.DSP_GLOABL;
        //检测是否屏敝广告
        if (AdsPreferences.getInstance(context).getBoolean(AD_MASK_FLAG, ConfigDefine.AD_MASK_FLAG)){
            MLog.i(TAG, "getDspSpotAppExitChannel mask ad !");
            return channel;
        }
        //检测全局条件  App退出不受全局条件控制
//        if (!checkDspSpotAppExitChannel(context, ConstDefine.DSP_GLOABL)){
//            MLog.i(TAG, "getDspSpotAppExitChannel gloabl condition fail !");
//            return channel;
//        }
        //检测单个SITE条件
        for (int i=0; i<DSP_APP_LIST.size(); i++){
            if (checkDspSpotAppExitChannel(context, DSP_APP_LIST.get(i))){
                channel = DSP_APP_LIST.get(i);
                break;
            }
        }
        return channel;
    }

    /**
     * 检测应用退出时展示插屏条件
     * @param context
     * @param channel
     * @return
     */
    private static boolean checkDspSpotAppExitChannel(Context context, int channel) {
        boolean ret = true;
        if (isTriesVaild(context, channel)) {
            MLog.i(TAG, "checkDspSpotAppExitChannel tries fail ! " + channel);
            return false;
        }
        //检测锁屏开关是否打开
        if (!isAppExitEnable(context, channel)){
            MLog.i(TAG, "checkDspSpotAppExitChannel app exit is not enable ! " + channel);
            return false;
        }
        //检测时间间隔是否满足
        long lastTime = getDspSpotNextTime(context, channel);
        if (System.currentTimeMillis() < lastTime) {
            MLog.i(TAG, "checkDspSpotAppExitChannel time fail ! " + channel + " time:" + lastTime + " - " + System.currentTimeMillis());
            return false;
        }
        //检测次数是否满足
        int show = getDspSpotShowNum(context, channel);
        int request = getDspSpotRequestNum(context, channel);
        int totalNum = getDspSpotShowTotal(context, channel);
        if (show >= totalNum || request >= totalNum*2){
            MLog.i(TAG, "checkDspSpotAppExitChannel num fail ! " + channel);
            return false;
        }
        return ret;
    }
}
