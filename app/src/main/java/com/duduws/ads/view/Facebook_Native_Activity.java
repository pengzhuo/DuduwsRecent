package com.duduws.ads.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dws.connect.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Facebook原生广告(即时请求)
 *
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/23 20:39
 */
public class Facebook_Native_Activity extends BaseActivity implements AdListener {
    private static final String TAG = "Facebook_Native_Activity";
    private static long timeDelay = 0;
    private int triggerType = -1;
    private boolean isOutSide = false;
    private int offset = 0;
    private String site = "";

    private NativeAd nativeAd;
    private AdChoicesView adChoicesView;
    private RelativeLayout adView;
    private LinearLayout nativeAdContainer;

    private static ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            triggerType = intent.getExtras().getInt(DspHelper.AD_TRIGGER_TYPE);
            isOutSide = intent.getExtras().getBoolean(DspHelper.AD_EXTRA_SITE);
        }

        if (triggerType == ConstDefine.TRIGGER_TYPE_APP_ENTER) {
            if (savedInstanceState == null) {
                savedInstanceState = new Bundle();
            }
            savedInstanceState.putBoolean("TRIGGER_TYPE_APP_ENTER", true);
        }
        super.onCreate(savedInstanceState);

        timeDelay = System.currentTimeMillis();

        offset = DspHelper.getTriggerOffSet(triggerType);

        site = DspHelper.getDspSite(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset);

        if (!TextUtils.isEmpty(site)) {
            //加载原生广告
            nativeAd = new NativeAd(this, site);
            nativeAd.setAdListener(this);
            nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);

            AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_REQUEST);
        } else {
            finish();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        MLog.e(TAG, "facebook native ad error! code: " + adError.getErrorCode() + ", message: " + adError.getErrorMessage());
        if (!isOutSide) {
            int triesNum = DspHelper.getDspSiteTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset) + 1;
            DspHelper.setDspSiteTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset, triesNum);
            int totalNum = DspHelper.getDspSiteTotalTriesNum(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset);
            if (triesNum >= totalNum) {
                DspHelper.setDspSiteTriesFlag(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset, true);
                DspHelper.setDspSiteTriesTime(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset, System.currentTimeMillis());
            }
        }
        //重置广告展示标志
        DspHelper.setCurrentAdsShowFlag(this, false);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_FAIL);
        if (triggerType == ConstDefine.TRIGGER_TYPE_APP_ENTER) {
            finish();
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        MLog.i(TAG, "facebook native ad onAdLoaded!");
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_SUCCESS);
        if (nativeAd == null || nativeAd != ad) {
            return;
        }
        //重新延时返回键1秒
//        timeDelay = System.currentTimeMillis() - (ConstDefine.BACK_KEY_DELAY_TIME-1)*1000;
        setContentView(R.layout.activity_native_ad);
        nativeAdContainer = (LinearLayout) findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        if (triggerType == ConstDefine.TRIGGER_TYPE_APP_ENTER) {
            adView = (RelativeLayout) inflater.inflate(R.layout.ad_unit, nativeAdContainer, false);
        } else if (triggerType == ConstDefine.TRIGGER_TYPE_APP_EXIT) {
            adView = (RelativeLayout) inflater.inflate(R.layout.ad_unit_ex, nativeAdContainer, false);
        } else {
            adView = (RelativeLayout) inflater.inflate(R.layout.ad_unit, nativeAdContainer, false);
        }
        nativeAdContainer.addView(adView);

        nativeAd.unregisterView();
        inflateAd(nativeAd, adView);
        if (!isOutSide) {
            DspHelper.updateRequestData(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset);
            DspHelper.updateShowData(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE + offset);
        }
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_SHOW);
    }

    @Override
    public void onAdClicked(Ad ad) {
        MLog.i(TAG, "facebook native ad onAdClicked!");
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, triggerType, ConstDefine.AD_TYPE_NATIVE_SPOT, ConstDefine.AD_RESULT_CLICK);
        finish();
    }

    private void inflateAd(NativeAd nativeAd, View adView) {
        LinearLayout adChoice = (LinearLayout) adView.findViewById(R.id.ad_choice);
        if (adChoicesView == null) {
            adChoicesView = new AdChoicesView(this, nativeAd, true);
            adChoice.addView(adChoicesView, 0);
        }
        delete = (ImageView) adView.findViewById(R.id.ad_delete);
//        delete.setVisibility(View.INVISIBLE);
//        delete.setEnabled(false);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(ConstDefine.CLOSE_BUTTON_DELAY_TIME*1000);
//                    Message msg = new Message();
//                    msg.what = 0;
//                    handler.sendMessage(msg);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
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

        RelativeLayout mask = (RelativeLayout)adView.findViewById(R.id.mask_layout);
        mask.setClickable(false);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable.
        RelativeLayout clickLayout = (RelativeLayout) adView.findViewById(R.id.clickLayout);
        RelativeLayout imgClickLayout = (RelativeLayout)adView.findViewById(R.id.click_layout);
        ViewGroup.LayoutParams pp = imgClickLayout.getLayoutParams();
        pp.height = clickLayout.getHeight()/2;
        imgClickLayout.setLayoutParams(pp);
//        nativeAd.registerViewForInteraction(clickLayout);
        List<View> viewList = new ArrayList<View>();
//        viewList.add(nativeAdMedia);
        viewList.add(imgClickLayout);
        viewList.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(clickLayout, viewList);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (System.currentTimeMillis() - timeDelay >= ConstDefine.BACK_KEY_DELAY_TIME * 1000) {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        //重置广告展示标志
        DspHelper.setCurrentAdsShowFlag(this, false);
        delayShowCmAds();
        delete = null;
        super.onDestroy();
    }

    //延时弹出CM广告
    private void delayShowCmAds() {
        if (DspHelper.isDelayShowAdsEnable(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE) &&
                (triggerType != ConstDefine.TRIGGER_TYPE_APP_ENTER && triggerType != ConstDefine.TRIGGER_TYPE_APP_EXIT)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int time = ConstDefine.DELAY_TIME_AFTER_ADS + new Random().nextInt(30) + 1;
                        MLog.i(TAG, "delay time " + time + " to show ads!");
                        Thread.sleep(time * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DspHelper.showAds(Facebook_Native_Activity.this, ConstDefine.DSP_CHANNEL_CM, ConstDefine.TRIGGER_TYPE_OTHER, true);
                }
            }).start();
        }
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (delete != null) {
                        try {
                            delete.setVisibility(View.VISIBLE);
                            delete.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
}
