package com.duduws.ads.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.utils.DspHelper;
import com.dws.connect.R;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/9/10 17:31
 */
public class LoadingActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_imitate_radar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    DspHelper.setCurrentAdsShowFlag(LoadingActivity.this, true);
                    //打开相应渠道的广告
                    int channel = intent.getIntExtra("channel", ConstDefine.DSP_GLOABL);
                    int triggerType = intent.getIntExtra("triggerType", -1);
                    boolean isOutSide = intent.getBooleanExtra("isOutSide", false);

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

                    LoadingActivity.this.startActivity(intent);
                    LoadingActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
