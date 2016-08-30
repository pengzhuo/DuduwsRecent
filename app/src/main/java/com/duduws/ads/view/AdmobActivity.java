package com.duduws.ads.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.duduws.ads.analytics.AnalyticsUtils;
import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.DspHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Pengz on 16/7/29.
 */
public class AdmobActivity extends BaseActivity{
    private static final String TAG = "AdmobActivity";
    private static InterstitialAd interstitialAd;
    private int triggerType = -1;
    private boolean isOutSide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            triggerType = intent.getExtras().getInt(DspHelper.AD_TRIGGER_TYPE);
            isOutSide = intent.getExtras().getBoolean(DspHelper.AD_EXTRA_SITE);
        }

        initInterstitialAd();
        finish();

//        Toast.makeText(this, ConfigDefine.SDK_KEY_ADMOB, Toast.LENGTH_LONG).show();
    }

    private void initInterstitialAd(){
        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId(ConfigDefine.SDK_KEY_ADMOB);
        interstitialAd.setAdListener(listener);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        if (!isOutSide){
            DspHelper.updateRequestData(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB);
        }
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_REQUEST);
    }

    AdListener listener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            MLog.i(TAG, "onAdClosed ");
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLOSE);
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(AdmobActivity.this, false);
            finish();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            MLog.i(TAG, "onAdFailedToLoad " + errorCode);
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_FAIL);
            if (!isOutSide){
                int triesNum = DspHelper.getDspSiteTriesNum(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB) + 1;
                DspHelper.setDspSiteTriesNum(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triesNum);
                int totalNum = DspHelper.getDspSiteTotalTriesNum(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB);
                if (triesNum >= totalNum){
                    DspHelper.setDspSiteTriesFlag(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, true);
                    DspHelper.setDspSiteTriesTime(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, System.currentTimeMillis());
                }
            }
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(AdmobActivity.this, false);
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            MLog.i(TAG, "onAdOpened ");
            if (!isOutSide){
                DspHelper.updateShowData(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB);
            }
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW);
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            MLog.i(TAG, "onAdLeftApplication ");
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK);
            finish();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            MLog.i(TAG, "onAdLoaded ");
            interstitialAd.show();
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SUCCESS);
        }
    };
}
