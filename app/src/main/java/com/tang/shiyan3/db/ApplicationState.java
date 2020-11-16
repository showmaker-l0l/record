package com.tang.shiyan3.db;


import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class ApplicationState extends LitePalSupport implements Serializable {

    private int id;
    //App名称
    private String appName;
    //第一次启动时间
    private String firstRuntime;
    //最后启动时间
    private String lastRuntime;
    //总运行时间
    private int totalRuntime;
    //位置信息
    private String location;
    //states日期  “yyyy年MM月dd日”
    private String recordDate;
    //记录时间
    private long recordTime;
    //记录类型
    private String dataType;
    //前台次数
    private int foreCount;
    //后台次数
    private int backCount;
    //交互次数
    private  int interactionCount;

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }
    public int getForeCount() {
        return foreCount;
    }

    public void setForeCount(int foreCount) {
        this.foreCount = foreCount;
    }

    public int getBackCount() {
        return backCount;
    }

    public void setBackCount(int backCount) {
        this.backCount = backCount;
    }

    public int getInteractionCount() {
        return interactionCount;
    }

    public void setInteractionCount(int interactionCount) {
        this.interactionCount = interactionCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getFirstRuntime() {
        return firstRuntime;
    }

    public void setFirstRuntime(String firstRuntime) {
        this.firstRuntime = firstRuntime;
    }

    public String getLastRuntime() {
        return lastRuntime;
    }

    public void setLastRuntime(String lastRuntime) {
        this.lastRuntime = lastRuntime;
    }

    public int getTotalRuntime() {
        return totalRuntime;
    }

    public void setTotalRuntime(int totalRuntime) {
        this.totalRuntime = totalRuntime;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }


}
