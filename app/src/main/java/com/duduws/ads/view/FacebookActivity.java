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
import com.facebook.FacebookSdk;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

/**
 * Created by Pengz on 16/7/29.
 */
public class FacebookActivity extends BaseActivity implements InterstitialAdListener{
    private static final String TAG = "FacebookActivity";
    private InterstitialAd interstitialAd;
    private int triggerType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            triggerType = intent.getExtras().getInt(DspHelper.AD_TRIGGER_TYPE);
        }

        //初始化Facebook
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        loadInterstitialAd(getApplicationContext(), ConfigDefine.SDK_KEY_FACEBOOK);

        Toast.makeText(this, ConfigDefine.SDK_KEY_FACEBOOK, Toast.LENGTH_LONG).show();
    }

    private void loadInterstitialAd(Context context, String id){
        MLog.e(TAG, "#### loadInterstitialAd " + id);
        interstitialAd = new InterstitialAd(context, id);
        interstitialAd.setAdListener(this);
        interstitialAd.loadAd();
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_REQUEST);
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        MLog.i(TAG, "onInterstitialDisplayed " + ad.toString());
        DspHelper.updateShowData(this);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW);
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        MLog.i(TAG, "onInterstitialDismissed " + ad.toString());
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLOSE);
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        MLog.i(TAG, "onError " + ad.toString() + " error: " + adError.getErrorCode() + " , " + adError.getErrorMessage());
        DspHelper.updateRequestData(this);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_FAIL);
        int triesNum = DspHelper.getDspSiteTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK);
        int totalNum = DspHelper.getDspSiteTotalTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK);
        if (triesNum >= totalNum){
            DspHelper.setDspSiteTriesFlag(this, ConstDefine.DSP_CHANNEL_FACEBOOK, true);
            DspHelper.setDspSiteTriesTime(this, ConstDefine.DSP_CHANNEL_FACEBOOK, System.currentTimeMillis());
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        MLog.i(TAG, "onAdLoaded " + ad.toString());
        interstitialAd.show();
        DspHelper.updateRequestData(this);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SUCCESS);
    }

    @Override
    public void onAdClicked(Ad ad) {
        MLog.i(TAG, "onAdClicked " + ad.toString());
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK);
    }
}
