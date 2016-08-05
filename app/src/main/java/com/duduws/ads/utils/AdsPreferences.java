package com.duduws.ads.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/2 19:58
 */
public class AdsPreferences {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private static AdsPreferences mPreferences;

    public static synchronized AdsPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new AdsPreferences(context);
        }
        return mPreferences;
    }

    public AdsPreferences(Context context) {
        mPref = context.getSharedPreferences("duduws_config", 0);
        mEditor = mPref.edit();
    }

    public void setString(String key,String value) {
        if (mEditor!=null) {
            mEditor.putString(key, value).commit();
        }
    }

    public String getString(String key,String defValue) {
        return (null!=mPref)?mPref.getString(key, defValue):"";
    }

    public <T> void setLong(T key, long value) {
        if (mEditor == null) {
            return;
        }
        mEditor.putLong(key.toString(), value);
        mEditor.commit();
    }

    public <T> long getLong(T key,long defValue)
    {
        if (mPref==null) {
            return 0;
        }
        return mPref.getLong((String) key, defValue);
    }

    public <T> void setInt(T key, int value) {
        if (mEditor == null) {
            return;
        }
        mEditor.putInt(key.toString(), value);
        mEditor.commit();
    }

    public <T> int getInt(T key, int defValue)
    {
        if (mPref==null) {
            return 0;
        }
        return mPref.getInt((String) key, defValue);
    }

    public <T> void setBoolean(T key, boolean value) {
        if (mEditor == null) {
            return;
        }
        mEditor.putBoolean(key.toString(), value);
        mEditor.commit();
    }

    public <T> boolean getBoolean(T key, boolean defValue)
    {
        if (mPref==null) {
            return false;
        }
        return mPref.getBoolean((String) key, defValue);
    }
}
