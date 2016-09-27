package com.duduws.ads.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.DspHelper;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2016/9/26 10:41
 */

public class AdmobRewardedVideoAdActivity extends BaseActivity implements RewardedVideoAdListener{
    private static final String TAG = "AdmobRewardedVideoAd";
    private int triggerType = -1;
    private boolean isOutSide = false;
    private int offset = 0;
    private String site = "";
    private RewardedVideoAd mRewardedVideoAd;
    private final Object mLock = new Object();
    private boolean mIsRewardedVideoLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            triggerType = intent.getExtras().getInt(DspHelper.AD_TRIGGER_TYPE);
            isOutSide = intent.getExtras().getBoolean(DspHelper.AD_EXTRA_SITE);
        }

        offset = DspHelper.getTriggerOffSet(triggerType);

        site = "ca-app-pub-2563657746943063/2967823837";//DspHelper.getDspSite(getApplicationContext(), ConstDefine.DSP_CHANNEL_ADMOB+offset);

        if (!TextUtils.isEmpty(site)){
            init();
        }else{
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(this, false);
        }

//        finish();
    }

    private void init(){
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        synchronized (mLock) {
            if (!mIsRewardedVideoLoading) {
                mIsRewardedVideoLoading = true;
                Bundle extras = new Bundle();
                extras.putBoolean("_noRefresh", true);
                AdRequest adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .addTestDevice("E11B77D3E10A92978A319C1D8D314864")
                        .build();
                mRewardedVideoAd.loadAd(site, adRequest);
            }
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        MLog.e(TAG, "onRewardedVideoAdLoaded");
        synchronized (mLock) {
            mIsRewardedVideoLoading = false;
        }
        if (mRewardedVideoAd.isLoaded()){
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        MLog.e(TAG, "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        MLog.e(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        MLog.e(TAG, "onRewardedVideoAdClosed");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        MLog.e(TAG, "onRewarded: " + rewardItem.getType() + " = " + rewardItem.getAmount());
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        MLog.e(TAG, "onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        MLog.e(TAG, "onRewardedVideoAdFailedToLoad");
        synchronized (mLock) {
            mIsRewardedVideoLoading = false;
        }
    }
}
