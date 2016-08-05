package com.duduws.ads.main;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cmcm.adsdk.BitmapListener;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.CMAdManagerFactory;
import com.cmcm.adsdk.ImageDownloadListener;
import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.FuncUtils;
import com.duduws.ads.utils.VolleyUtil;

/**
 * Created by Pengz on 16/7/20.
 */
public class AdApplication extends Application {
    private static final String TAG = "AdApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        //设置打印日志
        MLog.setLogEnable(true);

        //初始化信息
        initConfigInfo();

        //初始化SDK
        String mid = ConfigDefine.SDK_KEY_CM.substring(0,4);
        CMAdManager.applicationInit(getApplicationContext(), mid, "");
        CMAdManagerFactory.setImageDownloadListener(new MyImageLoadListener());
        //是否允许打印日志
        CMAdManager.enableLog();
    }

    public void initConfigInfo(){
        ConfigDefine.APP_VERSION 		= FuncUtils.getManifestApplicationMetaData(this, "APP_VERSION");
        ConfigDefine.APP_CHANNEL_ID		= FuncUtils.getManifestApplicationMetaData(this, "APP_CHANNEL_ID");
        ConfigDefine.APP_COOPERATION_ID	= FuncUtils.getManifestApplicationMetaData(this, "APP_COOPERATION_ID");
        ConfigDefine.APP_PRODUCT_ID		= FuncUtils.getManifestApplicationMetaData(this, "APP_PRODUCT_ID");
        ConfigDefine.APP_PROTOCOL		= FuncUtils.getManifestApplicationMetaData(this, "APP_PROTOCOL");
        //facebook
        ConfigDefine.SDK_KEY_FACEBOOK   = FuncUtils.getManifestApplicationMetaData(this, "SDK_KEY_FACEBOOK");
        //猎豹cm
        ConfigDefine.SDK_KEY_CM         = FuncUtils.getManifestApplicationMetaData(this, "SDK_KEY_CM");
        ConfigDefine.SDK_KEY_CM         = ConfigDefine.SDK_KEY_CM.substring(0, ConfigDefine.SDK_KEY_CM.length()-1);
        //admob
        ConfigDefine.SDK_KEY_ADMOB 	    = FuncUtils.getManifestApplicationMetaData(this, "SDK_KEY_ADMOB");
        MLog.e(TAG, "#### APP_VERSION = " + ConfigDefine.APP_VERSION);
        MLog.e(TAG, "#### APP_CHANNEL_ID = " + ConfigDefine.APP_CHANNEL_ID);

        MLog.e(TAG, "#### APP_COOPERATION_ID = " + ConfigDefine.APP_COOPERATION_ID);
        MLog.e(TAG, "#### APP_PRODUCT_ID = " + ConfigDefine.APP_PRODUCT_ID);

        MLog.e(TAG, "#### APP_PROTOCOL = " + ConfigDefine.APP_PROTOCOL);
        MLog.e(TAG, "#### ASSETS_PATH = " + ConstDefine.ASSETS_PATH);

        MLog.e(TAG, "#### SDK_KEY_FACEBOOK = " + ConfigDefine.SDK_KEY_FACEBOOK);
        MLog.e(TAG, "#### SDK_KEY_CM = " + ConfigDefine.SDK_KEY_CM);
        MLog.e(TAG, "#### SDK_KEY_ADMOB = " + ConfigDefine.SDK_KEY_ADMOB);
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
