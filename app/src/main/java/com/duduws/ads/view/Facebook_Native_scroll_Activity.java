package com.duduws.ads.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.DspHelper;
import com.dws.connect.R;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdScrollView;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdsManager;

/**
 * Facebook原生广告(预加载)
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/25 15:13
 */
public class Facebook_Native_scroll_Activity extends BaseActivity implements NativeAdsManager.Listener {
    private static final String TAG = "Facebook_Native_scroll_Activity";
    private static long timeDelay = 0;
    private int triggerType = -1;
    private boolean isOutSide = false;
    private int offset = 0;

    private NativeAd nativeAd;
    private AdChoicesView adChoicesView;
    private RelativeLayout adView;
    private LinearLayout nativeAdContainer;

    private NativeAdsManager manager;
    private NativeAdScrollView scrollView;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            triggerType = intent.getExtras().getInt(DspHelper.AD_TRIGGER_TYPE);
            isOutSide = intent.getExtras().getBoolean(DspHelper.AD_EXTRA_SITE);
        }

        timeDelay = System.currentTimeMillis();

        offset = DspHelper.getTriggerOffSet(triggerType);

        manager = new NativeAdsManager(this, ConfigDefine.SDK_KEY_FACEBOOK_NATIVE, ConstDefine.PRE_LOADING_ADS_NUM);
        manager.setListener(this);
        manager.loadAds(NativeAd.MediaCacheFlag.ALL);
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onAdsLoaded() {
        setContentView(R.layout.activity_native_ad);
        nativeAdContainer = (LinearLayout)findViewById(R.id.native_ad_container);
        if (scrollView != null){
            nativeAdContainer.removeView(scrollView);
        }
        scrollView = new NativeAdScrollView(this, manager, NativeAdView.Type.HEIGHT_400);
        nativeAdContainer.addView(scrollView);

        nativeAd = manager.nextNativeAd();
        if (nativeAd != null){
            if (triggerType == ConstDefine.TRIGGER_TYPE_APP_ENTER){
                adView = (RelativeLayout)inflater.inflate(R.layout.ad_unit, nativeAdContainer, false);
            } else if (triggerType == ConstDefine.TRIGGER_TYPE_APP_EXIT){
                adView = (RelativeLayout)inflater.inflate(R.layout.ad_unit_ex, nativeAdContainer, false);
            } else {
                adView = (RelativeLayout)inflater.inflate(R.layout.ad_unit, nativeAdContainer, false);
            }
            nativeAdContainer.addView(adView);
            nativeAd.unregisterView();
            inflateAd(nativeAd, adView);
            nativeAd.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                        Facebook_Native_scroll_Activity.this.finish();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onAdError(AdError adError) {
        MLog.e(TAG, "facebook native scroll error! " + adError.getErrorCode() + " --- " + adError.getErrorMessage());
    }

    private void inflateAd(NativeAd nativeAd, View adView) {
        // Create native UI using the ad metadata.
        LinearLayout adChoice = (LinearLayout)adView.findViewById(R.id.ad_choice);
        if (adChoicesView == null){
            adChoicesView = new AdChoicesView(this, nativeAd, true);
            adChoice.addView(adChoicesView, 0);
        }
        ImageView delete = (ImageView)adView.findViewById(R.id.ad_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Facebook_Native_scroll_Activity.this.finish();
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
