package com.duduws.ads.net;

import android.text.TextUtils;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.utils.Base64;
import com.duduws.ads.utils.XXTea;

import org.json.JSONObject;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/7/30 11:34
 */
public class NetManager {
    private static final String TAG = "NetManager";

    /**
     * 请求服务器
     */
    public static void startRequest(){
        JSONObject jsonObject = NetHelper.getRequestInfo();
        String str = new String(Base64.encode(XXTea.encrypt(jsonObject.toString().getBytes(), ConstDefine.XXTEA_KEY.getBytes())));
        String response = NetHelper.sendPost(ConstDefine.SERVER_URL, str);
        if (!TextUtils.isEmpty(response)){
            try {
                response = new String(XXTea.decrypt(Base64.decode(response.toCharArray()), ConstDefine.XXTEA_KEY.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向服务器发送心跳
     */
    public static void startHeart(){

    }
}
