package com.duduws.ads.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/2 19:58
 */
public class AdsPreferences {
    private SharedPreferences mPref;
    private Editor mEditor;
    private static AdsPreferences mPreferences;

    private SharedPreferences mPrefGloabl;
    private Editor mEditorGloabl;

    private SharedPreferences mPrefFacebook;
    private Editor mEditorFacebook;

    private SharedPreferences mPrefAdmob;
    private Editor mEditorAdmob;

    private SharedPreferences mPrefCm;
    private Editor mEditorCm;

    private SharedPreferences mPrefFacebookNative;
    private Editor getmEditorFacebookNative;

    public static synchronized AdsPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new AdsPreferences(context);
        }
        return mPreferences;
    }

    public AdsPreferences(Context context) {
        mPref = context.getSharedPreferences("duduws_config", 0);
        mEditor = mPref.edit();
        mPrefGloabl = context.getSharedPreferences("gloabl_config", 0);
        mEditorGloabl = mPrefGloabl.edit();
        mPrefFacebook = context.getSharedPreferences("facebook_config", 0);
        mEditorFacebook = mPrefFacebook.edit();
        mPrefAdmob = context.getSharedPreferences("admob_config", 0);
        mEditorAdmob = mPrefAdmob.edit();
        mPrefCm = context.getSharedPreferences("cm_config", 0);
        mEditorCm = mPrefCm.edit();
        mPrefFacebookNative = context.getSharedPreferences("facebook_native_config", 0);
        getmEditorFacebookNative = mPrefFacebookNative.edit();
    }

    private SharedPreferences getPrefs(int channel) {
        switch (channel) {
            case -1:
                return mPrefGloabl;
            case 1:
                return mPrefFacebook;
            case 2:
                return mPrefAdmob;
            case 3:
                return mPrefCm;
            case 11:
                return mPrefFacebookNative;
            default:
                assert false;
                break;
        }
        return null;
    }

    private Editor getEditor(int channel) {
        switch (channel) {
            case -1:
                return mEditorGloabl;
            case 1:
                return mEditorFacebook;
            case 2:
                return mEditorAdmob;
            case 3:
                return mEditorCm;
            case 11:
                return getmEditorFacebookNative;
            default:
                assert false;
                break;
        }
        return null;
    }

    public void setString(String key,String value) {
        if (mEditor!=null) {
            mEditor.putString(key, value).commit();
        }
    }

    public void setString(int channel, String key, String value) {
        Editor editor = getEditor(channel);
        if (editor != null){
            editor.putString(key, value).commit();
        }
    }

    public String getString(String key,String defValue) {
        return (null!=mPref)?mPref.getString(key, defValue):"";
    }

    public String getString(int channel, String key, String defValue) {
        SharedPreferences sp = getPrefs(channel);
        return (sp == null) ? "" : sp.getString(key, defValue);
    }

    public <T> void setLong(T key, long value) {
        if (mEditor == null) {
            return;
        }
        mEditor.putLong(key.toString(), value);
        mEditor.commit();
    }

    public <T> void setLong(int channel, T key, long value){
        Editor editor = getEditor(channel);
        if (editor != null){
            editor.putLong(key.toString(), value).commit();
        }
    }

    public <T> long getLong(T key,long defValue)
    {
        if (mPref==null) {
            return 0;
        }
        return mPref.getLong((String) key, defValue);
    }

    public <T> long getLong(int channel, T key, long defValue){
        SharedPreferences sp = getPrefs(channel);
        return (sp == null) ? 0 : sp.getLong(key.toString(), defValue);
    }

    public <T> void setInt(T key, int value) {
        if (mEditor == null) {
            return;
        }
        mEditor.putInt(key.toString(), value);
        mEditor.commit();
    }

    public <T> void setInt(int channel, T key, int value){
        Editor editor = getEditor(channel);
        if (editor != null){
            editor.putInt(key.toString(), value).commit();
        }
    }

    public <T> int getInt(T key, int defValue)
    {
        if (mPref==null) {
            return 0;
        }
        return mPref.getInt((String) key, defValue);
    }

    public <T> int getInt(int channel, T key, int defValue){
        SharedPreferences sp = getPrefs(channel);
        return (sp == null) ? 0 : sp.getInt(key.toString(), defValue);
    }

    public <T> void setBoolean(T key, boolean value) {
        if (mEditor == null) {
            return;
        }
        mEditor.putBoolean(key.toString(), value);
        mEditor.commit();
    }

    public <T> void setBoolean(int channel, T key, boolean value){
        Editor editor = getEditor(channel);
        if (editor != null){
            editor.putBoolean(key.toString(), value).commit();
        }
    }

    public <T> boolean getBoolean(T key, boolean defValue)
    {
        if (mPref==null) {
            return false;
        }
        return mPref.getBoolean((String) key, defValue);
    }

    public <T> boolean getBoolean(int channel, T key, boolean defValue){
        SharedPreferences sp = getPrefs(channel);
        return (sp == null) ? false : sp.getBoolean(key.toString(), defValue);
    }
}
