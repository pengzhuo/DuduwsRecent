package com.duduws.ads.model;

import com.duduws.ads.utils.StrUtils;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/2 19:24
 */
public class AdItem implements Serializable{
    private static final String TAG = "AdItem";

    private static final String TITLENAME = StrUtils.deCrypt("title_name");
    private static final String ICONURL = StrUtils.deCrypt("icon_url");
    private static final String LANDINGURL = StrUtils.deCrypt("landing_url");

    public int index;
    public String titleName;
    public String iconUrl;
    public String landingUrl;

    public AdItem(int index, String titleName, String iconUrl, String landingUrl) {
        super();
        this.index = index;
        this.titleName = titleName;
        this.iconUrl = iconUrl;
        this.landingUrl = landingUrl;
    }

    public AdItem() {
        super();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLandingUrl() {
        return landingUrl;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }

    public AdItem(JSONObject obj) throws Exception {
        if (obj == null || obj.length() == 0) {
            return;
        }
        if (obj.has(TITLENAME)) {
            titleName = obj.getString(TITLENAME);
        }

        if (obj.has(ICONURL)) {
            iconUrl = obj.getString(ICONURL);
        }

        if (obj.has(LANDINGURL)) {
            landingUrl = obj.getString(LANDINGURL);
        }

    }
}
