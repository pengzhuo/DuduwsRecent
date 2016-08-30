package com.duduws.ads.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duduws.ads.analytics.AnalyticsUtils;
import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.DspHelper;
import com.duduws.recent.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

/**
 * Facebook原生广告(即时请求)
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/23 20:39
 */
public class Facebook_Native_Activity extends BaseActivity implements AdListener{
    private static final String TAG = "Facebook_Native_Activity";
    private static long timeDelay = 0;
    private int triggerType = -1;
    private boolean isOutSide = false;

    private NativeAd nativeAd;
    private AdChoicesView adChoicesView;
    private RelativeLayout adView;
    private LinearLayout nativeAdContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            triggerType = intent.getExtras().getInt(DspHelper.AD_TRIGGER_TYPE);
            isOutSide = intent.getExtras().getBoolean(DspHelper.AD_EXTRA_SITE);
        }

        timeDelay = System.currentTimeMillis();

        //加载原生广告
        nativeAd = new NativeAd(this, ConfigDefine.SDK_KEY_FACEBOOK_NATIVE);
        nativeAd.setAdListener(this);
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);

        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_REQUEST);
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        MLog.e(TAG, "facebook native ad error! code: " + adError.getErrorCode() + ", message: " + adError.getErrorMessage());
        if (!isOutSide){
            int triesNum = DspHelper.getDspSiteTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE) + 1;
            DspHelper.setDspSiteTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triesNum);
            int totalNum = DspHelper.getDspSiteTotalTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE);
            if (triesNum >= totalNum){
                DspHelper.setDspSiteTriesFlag(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, true);
                DspHelper.setDspSiteTriesTime(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, System.currentTimeMillis());
            }
        }
        //重置广告展示标志
        DspHelper.setCurrentAdsShowFlag(this, false);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_FAIL);
    }

    @Override
    public void onAdLoaded(Ad ad) {
        MLog.i(TAG, "facebook native ad onAdLoaded!");
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_SUCCESS);
        if (nativeAd == null || nativeAd != ad){
            return;
        }
        //重新延时返回键1秒
        timeDelay = System.currentTimeMillis() + (ConstDefine.BACK_KEY_DELAY_TIME-1)*1000;
        setContentView(R.layout.activity_native_ad);
        nativeAdContainer = (LinearLayout)findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        if (triggerType == ConstDefine.TRIGGER_TYPE_APP_ENTER){
            adView = (RelativeLayout)inflater.inflate(R.layout.ad_unit, nativeAdContainer, false);
        } else if (triggerType == ConstDefine.TRIGGER_TYPE_APP_EXIT){
            adView = (RelativeLayout)inflater.inflate(R.layout.ad_unit_ex, nativeAdContainer, false);
        }
        nativeAdContainer.addView(adView);

        nativeAd.unregisterView();
        inflateAd(nativeAd, adView);
        if (!isOutSide){
            DspHelper.updateRequestData(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE);
            DspHelper.updateShowData(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE);
        }
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_SHOW);
    }

    @Override
    public void onAdClicked(Ad ad) {
        MLog.i(TAG, "facebook native ad onAdClicked!");
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_CLICK);
    }

    private void inflateAd(NativeAd nativeAd, View adView){
        LinearLayout adChoice = (LinearLayout)adView.findViewById(R.id.ad_choice);
        if (adChoicesView == null){
            adChoicesView = new AdChoicesView(this, nativeAd, true);
            adChoice.addView(adChoicesView, 0);
        }
        ImageView delete = (ImageView)adView.findViewById(R.id.ad_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置广告展示标志
                DspHelper.setCurrentAdsShowFlag(Facebook_Native_Activity.this, false);
                AnalyticsUtils.onEvent(Facebook_Native_Activity.this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_CLOSE);
                Facebook_Native_Activity.this.finish();
            }
        });
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
        nativeAdMedia.setAutoplay(AdSettings.isVideoAutoplay());
        nativeAdMedia.setAutoplayOnMobile(AdSettings.isVideoAutoplayOnMobile());
        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(View.VISIBLE);
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        nativeAdMedia.setNativeAd(nativeAd);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable.
        RelativeLayout clickLayout = (RelativeLayout)adView.findViewById(R.id.clickLayout);
        nativeAd.registerViewForInteraction(clickLayout);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (System.currentTimeMillis() - timeDelay >= ConstDefine.BACK_KEY_DELAY_TIME*1000){
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //重置广告展示标志
        DspHelper.setCurrentAdsShowFlag(this, false);
    }
}
