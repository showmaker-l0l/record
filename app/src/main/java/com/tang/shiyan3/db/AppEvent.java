package com.tang.shiyan3.db;

import org.litepal.crud.LitePalSupport;

public class AppEvent extends LitePalSupport{
    private int id;
    //App名称
    private String appName;
    //Activity名称
    private String activityName;
    //事件时间戳 "yyyy年MM月dd日 HH:mm:ss"
    private String eventTime;
    //事件类型
    private String eventType;
    //事件时间戳
    private long timestamp;
    //事件的日期 “yyyy年MM月dd日”
    private String eventDate;
    //记录类型
    private String dataType;

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
