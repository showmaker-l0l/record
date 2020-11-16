package com.tang.shiyan3.util;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tang.shiyan3.db.AppEvent;
import com.tang.shiyan3.db.AppInfo;
import com.tang.shiyan3.db.ApplicationState;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetData {

    //获取事件的时间间隔
    public static final int GET_DAILY = 1;
    public static final int GET_WEEKLY = 2;
    public static final int GET_INPUT = 3;
    public static final int GET_ROUTINE = 4;
    //事件的数据按时间间隔分类
    public static final String DAILY_DATA = "Daily Data";
    public static final String WEEKLY_DATA = "Weekly Data";
    public static final String SEARCH_DATA = "Search Data";
    public static final String ROUTINE_DATA = "Routine Data";
    //事件的类型
    public static final String MOVE_TO_BACKGROUND = "Move To Background";
    public static final String MOVE_TO_FOREGROUND = "Move To Foreground";
    public static final String CONFIGURATION_CHANGE = "Configuration Change";
    public static final String USER_INTERACTION = "User Interaction";



    //获取UsageEvents，并存入数据库，方便统计
    public static void getUsageEvents(Context context, int getType) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context
                .getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager packageManager = context.getPackageManager();
        //当前时间
        long currentTime = System.currentTimeMillis();
        String date = Utility.longToDate(currentTime);
        String dataType = "";
        UsageEvents queryEvents = null;
        long startTime;

        switch (getType){
            //获取10min的events
            case GET_ROUTINE:
                dataType = ROUTINE_DATA;
                startTime = currentTime - 1000*60*10;
                queryEvents = usageStatsManager.queryEvents(startTime, currentTime);//获得events迭代器
                break;
            //获取当天的events
            case GET_DAILY:
                dataType = DAILY_DATA;
                startTime = Utility.stringDateToLong(date.split(" ")[0]);
                LitePal.deleteAll(AppEvent.class,"dataType = ? and eventDate = ?",dataType, date.split(" ")[0]);
                queryEvents = usageStatsManager.queryEvents(startTime, currentTime);
                break;
            //获取10天的events
            //events最多获取10天的数据，用于第一次app启动的初始化，将之前的数据写入手机数据库
            case GET_WEEKLY:
                dataType = DAILY_DATA;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_YEAR, -10);
                startTime = calendar.getTimeInMillis();//开始时间
                //LitePal.deleteAll(AppEvent.class, "dataType = ?", dataType);
                queryEvents = usageStatsManager.queryEvents(startTime, currentTime);
                break;
            default:
                break;
        }

        while (queryEvents.hasNextEvent()){
            UsageEvents.Event eventAux = new UsageEvents.Event();
            queryEvents.getNextEvent(eventAux);
            try {
                AppEvent appEvent = new AppEvent();
                String eventType;
                //判断events类型
                if (eventAux.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    eventType = MOVE_TO_FOREGROUND;
                } else if (eventAux.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    eventType = MOVE_TO_BACKGROUND;
                } else if (eventAux.getEventType() == UsageEvents.Event.USER_INTERACTION) {
                    eventType = USER_INTERACTION;
                } else {
                    continue;//类型为空则跳过循环
                }
                //获取event的appname
                ApplicationInfo applicationInfo = packageManager
                        .getApplicationInfo(eventAux.getPackageName(), packageManager.GET_META_DATA);
                String appName = packageManager.getApplicationLabel(applicationInfo) + "";
                String activityName = eventAux.getClassName();//获取event的activity
                long timestamp = eventAux.getTimeStamp();//获取event的时间戳
                String eventTime = Utility.longToDate(timestamp);

                appEvent.setAppName(appName);
                appEvent.setActivityName(activityName);
                appEvent.setEventType(eventType);
                appEvent.setDataType(dataType);
                appEvent.setEventTime(eventTime);
                appEvent.setTimestamp(timestamp);
                appEvent.setEventDate(eventTime.split(" ")[0]);
                appEvent.save();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //获取应用活动记录,并存入数据库
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void getUsagestates(Context context, int getType) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context
                .getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager packageManager = context.getPackageManager();

        //当前时间
        long currentTime = System.currentTimeMillis();
        String date = Utility.longToDate(currentTime);
        List<UsageStats> usageStatsList = new ArrayList<>();
        String dataType = "";
        long startTime = 0;

        switch (getType){
            //Routine级的
            case GET_ROUTINE:
                dataType = ROUTINE_DATA;
                startTime = currentTime - 1000 * 60 * 11;
                //获取十分钟的应用活动记录
                usageStatsList = usageStatsManager
                        .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, currentTime);
                break;
            //获取当天应用记录,Daily级的记录一天保存一个
            case GET_DAILY:
                //开始时间,当天0点
                startTime = Utility.stringDateToLong(date.split(" ")[0]);
                dataType = DAILY_DATA;
                //先删除当天之前的记录
                LitePal.deleteAll(ApplicationState.class, "dataType = ? and recordDate = ?", dataType, date.split(" ")[0]);
                usageStatsList = usageStatsManager
                        .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime,currentTime);
                break;
//            case GET_STATES_WEEKLY://获取7天内的应用活动记录
//                dataType = WEEKLY_DATA;
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(new Date());
//                calendar.add(Calendar.DAY_OF_YEAR, -7);
//                startTime = calendar.getTimeInMillis();//开始时间
//                //先删除之前的记录
//                LitePal.deleteAll(ApplicationState.class, "dataType = ?", dataType);
//                usageStatsList = usageStatsManager
//                        .queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, currentTime);
//                break;
            default:
                break;
        }

        if (usageStatsList != null) {
            for (UsageStats usageStats : usageStatsList) {
                try {
                    ApplicationState app = new ApplicationState();
                    ApplicationInfo applicationInfo = packageManager
                            .getApplicationInfo(usageStats.getPackageName(), packageManager.GET_META_DATA);
                    String appName = packageManager.getApplicationLabel(applicationInfo) + "";
                    long totalRuntime = usageStats.getTotalTimeInForeground() / (1000 * 60) ;//单位：min
                    //仅记录活动时间1min以上的app
                    if (totalRuntime > 0) {
                        app.setAppName(appName);
                        List<AppEvent> appEvents = LitePal.
                                where("appName = ? and dataType = ? and eventType = ? and timestamp > ?", appName, dataType, MOVE_TO_FOREGROUND, startTime+"").
                                order("timestamp desc").
                                find(AppEvent.class);
                        if (appEvents.size() == 0){
                            continue;
                        }
                        app.setFirstRuntime(appEvents.get(appEvents.size() - 1).getEventTime());
                        app.setLastRuntime(appEvents.get(0).getEventTime());
                        app.setForeCount(appEvents.size());
                        app.setBackCount(LitePal.
                                where("appName = ? and dataType = ? and eventType = ? and timestamp > ?", appName, dataType, MOVE_TO_BACKGROUND, startTime+"").
                                count(AppEvent.class));
                        app.setInteractionCount(LitePal.
                                where("appName = ? and dataType = ? and eventType = ? and timestamp > ?", appName, dataType, USER_INTERACTION, startTime+"").
                                count(AppEvent.class));
                        app.setTotalRuntime(Integer.parseInt(totalRuntime + "")); //按min存储
                        app.setRecordDate(appEvents.get(0).getEventDate());
                        app.setRecordTime(System.currentTimeMillis());
                        app.setLocation(Utility.getLocation(context));
                        app.setDataType(dataType);
                        app.save();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取应用信息存入数据库
    public static void getAppInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> infoList = packageManager
                .getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        PackageInfo packageInfo;

        if (infoList.size() != 0) {
            for (ApplicationInfo info : infoList) {
                String name = info.loadLabel(packageManager).toString();
                List<AppInfo> list = LitePal
                        .where("name = ?", name)
                        .find(AppInfo.class);
                if (list.size() != 0) {
                    continue;
                }
                AppInfo appInfo = new AppInfo();
                Drawable icon = info.loadIcon(packageManager);
                byte[] appIcon = Utility.drawableToByte(icon);
                appInfo.setIcon(appIcon);
                appInfo.setPackageName(info.packageName);
                appInfo.setName(name);
                String dir = info.sourceDir;
                appInfo.setDir(dir);
                float size = new File(dir).length() * 1f / 1024 / 1024;//单位MB
                size = Float.parseFloat(decimalFormat.format(size));//保留两位小数
                appInfo.setSize(size);
                appInfo.setSystemApp((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM);
                try {
                    packageInfo = packageManager.getPackageInfo(info.packageName, PackageManager.GET_PERMISSIONS);
                    StringBuilder builder = new StringBuilder();
                    String[] permissions = packageInfo.requestedPermissions;
                    if (permissions != null && permissions.length > 0) {
                        for (int i = 0; i < permissions.length; i++) {
                            if (i < permissions.length - 1) {
                                builder.append(permissions[i]).append(",\n");
                            } else {
                                builder.append(permissions[i]);
                            }
                        }
                    }
                    appInfo.setPermission(builder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                appInfo.save();
            }
        }
    }

    //初始化app数据
    public static void initEventsAndStates(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context
                .getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager packageManager = context.getPackageManager();
        String dataType = DAILY_DATA;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -10);
        long startTime = calendar.getTimeInMillis();//开始时间
        long endTime = System.currentTimeMillis();
        getUsageEvents(context, GET_WEEKLY);

        try {
            List<String> days = Utility.getDaysBetweenTwoDays(Utility.longToDate(startTime).split(" ")[0],
                    Utility.longToDate(endTime).split(" ")[0]);
            for (String day : days){
                if (LitePal.select("recordDate").
                        where("recordDate = ?", day).
                        findFirst(ApplicationState.class) != null){
                    continue;
                }
                long start = Utility.stringDateToLong(day);
                long end = start + (1000*60*60*24);
                List<UsageStats> usageStatsList = usageStatsManager
                        .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start,end);
                if (usageStatsList != null) {
                    for (UsageStats usageStats : usageStatsList) {
                        try {
                            ApplicationState app = new ApplicationState();
                            ApplicationInfo applicationInfo = packageManager
                                    .getApplicationInfo(usageStats.getPackageName(), packageManager.GET_META_DATA);
                            String appName = packageManager.getApplicationLabel(applicationInfo) + "";
                            long totalRuntime = usageStats.getTotalTimeInForeground() / (1000 * 60) ;//单位：min
                            //仅记录活动时间1min以上的app
                            if (totalRuntime > 0) {
                                app.setAppName(appName);
                                List<AppEvent> appEvents = LitePal.
                                        where("appName = ? and dataType = ? and eventType = ? and eventDate = ?",
                                                appName, DAILY_DATA, MOVE_TO_FOREGROUND, day).
                                        order("timestamp desc").
                                        find(AppEvent.class);
                                if (appEvents.size() == 0){
                                    continue;
                                }
                                app.setFirstRuntime(appEvents.get(appEvents.size() - 1).getEventTime());
                                app.setLastRuntime(appEvents.get(0).getEventTime());
                                app.setForeCount(appEvents.size());
                                app.setBackCount(LitePal.
                                        where("appName = ? and dataType = ? and eventType = ? and eventDate = ?",
                                                appName, DAILY_DATA, MOVE_TO_BACKGROUND, day).
                                        count(AppEvent.class));
                                app.setInteractionCount(LitePal.
                                        where("appName = ? and dataType = ? and eventType = ? and eventDate = ?",
                                                appName, DAILY_DATA, USER_INTERACTION, day).
                                        count(AppEvent.class));
                                app.setTotalRuntime(Integer.parseInt(totalRuntime + "")); //按min存储
                                app.setRecordDate(appEvents.get(0).getEventDate());
                                app.setRecordTime(System.currentTimeMillis());
                                app.setLocation(Utility.getLocation(context));
                                app.setDataType(dataType);
                                app.save();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getUsagestatesBefore(Context context, int getType, long startTime, long endTime) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context
                .getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager packageManager = context.getPackageManager();
        //当前时间
        long currentTime = System.currentTimeMillis();
        String date = Utility.longToDate(currentTime);
        List<UsageStats> usageStatsList = new ArrayList<>();
        String dataType = "";

        switch (getType) {
            case GET_INPUT://获取设定时间的应用活动记录
                //先删除之前的记录
                dataType = SEARCH_DATA;
                LitePal.deleteAll(ApplicationState.class, "dataType = ?", dataType);
                usageStatsList = usageStatsManager
                        .queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
                break;
            default:
                break;
        }

        if (usageStatsList != null) {
            for (UsageStats usageStats : usageStatsList) {
                try {
                    ApplicationState app = new ApplicationState();
                    ApplicationInfo applicationInfo = packageManager
                            .getApplicationInfo(usageStats.getPackageName(), packageManager.GET_META_DATA);
                    String appName = packageManager.getApplicationLabel(applicationInfo) + "";
                    //仅记录活动时间1min以上的app
                    if (usageStats.getTotalTimeInForeground() / (60 * 1000) > 0) {
                        app.setAppName(appName);
                        app.setFirstRuntime(Utility.longToDate(usageStats.getFirstTimeStamp()));
                        app.setLastRuntime(Utility.longToDate(usageStats.getLastTimeStamp()));
                        app.setTotalRuntime(Integer.parseInt(usageStats.getTotalTimeInForeground() / (60 * 1000) + ""));
                        app.setRecordDate(date.split(" ")[0]);
                        app.setDataType(dataType);
                        app.setLocation(Utility.getLocation(context));
                        app.save();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
