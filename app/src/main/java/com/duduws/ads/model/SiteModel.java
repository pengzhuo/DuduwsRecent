package com.duduws.ads.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 广告单元模型
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/16 20:22
 */
public class SiteModel {
    private static final String TAG = "SiteModel";

    public SiteModel(String json){
        try {
            JSONObject obj = new JSONObject(json);
            setSite(obj.optString("site"));
            setAdType(obj.optInt("adtype"));
            setAppCount(obj.optInt("app_count"));
            setAppInterval(obj.optInt("app_interval"));
            setCid(obj.optString("channelid"));
            setWorkId(obj.optString("workerid"));
            setPid(obj.optString("pid"));
            setAppEnterFlag((obj.optInt("topapp_enter_action",1)==1)?true:false);
            setAppExitFlag((obj.optInt("topapp_exit_action",1)==1)?true:false);
            setLockFlag((obj.optInt("lock_action",1)==1)?true:false);
            setNetFlag((obj.optInt("net_action",1)==1)?true:false);
            setTriesNum(obj.optInt("tries_num"));
            setResetDayNum(obj.optInt("reset_day_num"));
            setTriggerType(obj.optInt("trigger_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("site", getSite());
            json.put("adtype", getAdType());
            json.put("app_count", getAppCount());
            json.put("app_interval", getAppInterval());
            json.put("channelid", getCid());
            json.put("workerid", getWorkId());
            json.put("pid", getPid());
            json.put("topapp_enter_action", isAppEnterFlag()?1:0);
            json.put("topapp_exit_action", isAppExitFlag()?1:0);
            json.put("lock_action", isLockFlag()?1:0);
            json.put("net_action", isNetFlag()?1:0);
            json.put("tries_num", getTriesNum());
            json.put("reset_day_num", getResetDayNum());
            json.put("trigger_type", getTriggerType());
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(){

    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getAppCount() {
        return appCount;
    }

    public void setAppCount(int appCount) {
        this.appCount = appCount;
    }

    public long getAppInterval() {
        return appInterval;
    }

    public void setAppInterval(long appInterval) {
        this.appInterval = appInterval;
    }

    public boolean isLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public boolean isAppEnterFlag() {
        return appEnterFlag;
    }

    public void setAppEnterFlag(boolean appEnterFlag) {
        this.appEnterFlag = appEnterFlag;
    }

    public boolean isAppExitFlag() {
        return appExitFlag;
    }

    public void setAppExitFlag(boolean appExitFlag) {
        this.appExitFlag = appExitFlag;
    }

    public boolean isNetFlag() {
        return netFlag;
    }

    public void setNetFlag(boolean netFlag) {
        this.netFlag = netFlag;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getTriesNum() {
        return triesNum;
    }

    public void setTriesNum(int triesNum) {
        this.triesNum = triesNum;
    }

    public int getResetDayNum() {
        return resetDayNum;
    }

    public void setResetDayNum(int resetDayNum) {
        this.resetDayNum = resetDayNum;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    private String site;
    private int adType;
    private int appCount;
    private long appInterval;
    private boolean lockFlag;
    private boolean appEnterFlag;
    private boolean appExitFlag;
    private boolean netFlag;
    private String workId;
    private String cid;
    private String pid;
    private int triesNum;
    private int resetDayNum;
    private int triggerType;
}
