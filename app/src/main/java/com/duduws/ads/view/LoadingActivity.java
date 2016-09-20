package com.duduws.ads.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.DspHelper;
import com.dws.connect.R;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/9/10 17:31
 */
public class LoadingActivity extends Activity{
    private static final String TAG = "LoadingActivity";
    int channel = ConstDefine.DSP_GLOABL;
    int triggerType = 0;
    boolean isOutSide = false;
    Intent intent = null;
    ImageView img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        DspHelper.setCurrentAdsShowFlag(LoadingActivity.this, true);
        //打开相应渠道的广告
        channel = intent.getIntExtra("channel", ConstDefine.DSP_GLOABL);
        triggerType = intent.getIntExtra("triggerType", -1);
        isOutSide = intent.getBooleanExtra("isOutSide", false);

        if (triggerType == ConstDefine.TRIGGER_TYPE_APP_ENTER){
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }else{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.loading);
            img = (ImageView)findViewById(R.id.infoOperating);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.tip);
            LinearInterpolator lin = new LinearInterpolator();
            animation.setInterpolator(lin);
            img.startAnimation(animation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MLog.e(TAG, e.getMessage());
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        if (img != null){
            img.clearAnimation();
            img = null;
        }
        super.onDestroy();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    {
                        if (channel == ConstDefine.DSP_CHANNEL_ADMOB) {
                            intent.setClass(LoadingActivity.this, AdmobActivity.class);
                        } else if (channel == ConstDefine.DSP_CHANNEL_FACEBOOK){
                            intent.setClass(LoadingActivity.this, Facebook_Native_Activity.class);
                        } else if (channel == ConstDefine.DSP_CHANNEL_CM){
                            intent.setClass(LoadingActivity.this, CmActivity.class);
                        } else if (channel == ConstDefine.DSP_CHANNEL_ADMOB_NATIVE) {
                            return;
                        } else if (channel == ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE) {
                            intent.setClass(LoadingActivity.this, Facebook_Native_Activity.class);
                        } else if (channel == ConstDefine.DSP_CHANNEL_CM_NATIVE) {
                            return;
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(DspHelper.AD_TRIGGER_TYPE, triggerType);
                        intent.putExtra(DspHelper.AD_EXTRA_SITE, isOutSide);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };
}
