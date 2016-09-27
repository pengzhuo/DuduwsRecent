package com.duduws.ads.model;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/2 17:03
 */
public class PackageElement implements Serializable{
    private PackageInfo mPackageInfo;
    private String mPackageName;
    private Drawable mIcon;
    private String mLabel;
    private boolean mIsNative;
    private String mUrl;
    private String mLandingUrl;
    private int index;

    public PackageElement(){}

    public PackageElement(String mPackageName, String label){
        this.mPackageName = mPackageName.trim();
        this.mLabel = label.trim();
    }

    public Object getCompareField() {
        return mLabel;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public PackageInfo getmPackageInfo() {
        return mPackageInfo;
    }

    public void setmPackageInfo(PackageInfo mPackageInfo) {
        this.mPackageInfo = mPackageInfo;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public Drawable getmIcon() {
        return mIcon;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public boolean ismIsNative() {
        return mIsNative;
    }

    public void setmIsNative(boolean mIsNative) {
        this.mIsNative = mIsNative;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmLandingUrl() {
        return mLandingUrl;
    }

    public void setmLandingUrl(String mLandingUrl) {
        this.mLandingUrl = mLandingUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
