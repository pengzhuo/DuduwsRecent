package com.duduws.ads.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.cmcm.adsdk.interstitial.InterstitialAdManager;
import com.duduws.ads.analytics.AnalyticsUtils;
import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.DspHelper;

/**
 * Created by Pengz on 16/7/29.
 */
public class CmActivity extends BaseActivity{
    private static final String TAG = "CmActivity";
    private static InterstitialAdManager interstitialAdManager = null;
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

        initInterstitialAds();

        finish();
//        Toast.makeText(this, ConfigDefine.SDK_KEY_CM, Toast.LENGTH_LONG).show();
    }

    private void initInterstitialAds(){
        interstitialAdManager = new InterstitialAdManager(this, ConfigDefine.SDK_KEY_CM);
        interstitialAdManager.setInterstitialCallBack(callBack);
        interstitialAdManager.loadAd();
        if (!isOutSide){
            DspHelper.updateRequestData(CmActivity.this, ConstDefine.DSP_CHANNEL_CM);
        }
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_CM, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_REQUEST);
    }

    InterstitialAdCallBack callBack = new InterstitialAdCallBack() {
        @Override
        public void onAdLoadFailed(int i) {
            MLog.i(TAG, "onAdLoadFailed " + i);
            AnalyticsUtils.onEvent(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_FAIL);
            if (!isOutSide){
                int triesNum = DspHelper.getDspSiteTriesNum(CmActivity.this, ConstDefine.DSP_CHANNEL_CM) + 1;
                DspHelper.setDspSiteTriesNum(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, triesNum);
                int totalNum = DspHelper.getDspSiteTotalTriesNum(CmActivity.this, ConstDefine.DSP_CHANNEL_CM);
                if (triesNum >= totalNum){
                    DspHelper.setDspSiteTriesFlag(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, true);
                    DspHelper.setDspSiteTriesTime(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, System.currentTimeMillis());
                }
            }
        }

        @Override
        public void onAdLoaded() {
            MLog.i(TAG, "onAdLoaded ");
            interstitialAdManager.showAd();
            AnalyticsUtils.onEvent(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SUCCESS);
        }

        @Override
        public void onAdClicked() {
            MLog.i(TAG, "onAdClicked ");
            AnalyticsUtils.onEvent(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK);
        }

        @Override
        public void onAdDisplayed() {
            MLog.i(TAG, "onAdDisplayed ");
            if (!isOutSide){
                DspHelper.updateShowData(CmActivity.this, ConstDefine.DSP_CHANNEL_CM);
            }
            AnalyticsUtils.onEvent(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW);
        }

        @Override
        public void onAdDismissed() {
            MLog.i(TAG, "onAdDismissed ");
            AnalyticsUtils.onEvent(CmActivity.this, ConstDefine.DSP_CHANNEL_CM, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLOSE);
        }
    };
}
