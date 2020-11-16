package com.tang.shiyan3.util;

import android.Manifest;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.tang.shiyan3.db.AppEvent;
import com.tang.shiyan3.db.AppInfo;
import com.tang.shiyan3.db.ApplicationState;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utility {

    private static final String TAG = "Utility";

    //检查权限是否已获取，未获取就跳转到设置页面
    public static boolean checkAppUsagePermission(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        // try to get app usage state in last 1 min
        List<UsageStats> stats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 60 * 1000, currentTime);
        if (stats.size() == 0) {
            Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.i(TAG, "Start usage access settings activity fail!");
            }
        }
        return true;
    }

    //获取当前位置
    public static String getLocation(Context context) {
        String locationService = Context.LOCATION_SERVICE;// 获取的是位置服务
        LocationManager manager = (LocationManager) context.getSystemService(locationService);
        String networkProvider = LocationManager.NETWORK_PROVIDER;
        //检查权限
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location location = manager.getLastKnownLocation(networkProvider);
        String address = "";
        if (location != null){
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> result = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!result.isEmpty()){
                    try {
                        address = result.get(0).getAddressLine(0) + result.get(0).getAddressLine(1);
                    } catch (Exception e){
                        address = result.get(0).getAddressLine(0);
                    }
                }
                address = address.replace("null","");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return address;
    }

    //传入long型时间，返回格式化日期
    public static String longToDate(long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sdf.format(date);
    }

    //传入yyyy年MM月dd日String时间，转为long型毫秒值
    public static long stringDateToLong(String date)  {
        Calendar c = Calendar. getInstance();
        try {
            c.setTime( new SimpleDateFormat("yyyy年MM月dd日" ).parse(date ));
        }catch (ParseException e){
            e.printStackTrace();
        }
        return c.getTimeInMillis();
    }

    //获取两个时间之间的所有天数
    public static List<String> getDaysBetweenTwoDays(String startDay,String endDay) throws ParseException {
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");//格式化为年月
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(startDay));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), min.get(Calendar.DATE),0,0,0);
        max.setTime(sdf.parse(endDay));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), max.get(Calendar.DATE),23,59,59);
        Calendar curr = min;
        while (curr.before(max)) {
            list.add(sdf.format(curr.getTime()));
            curr.add(Calendar.DATE, 1);
        }
        return list;
    }

    //将drawable转为byte[]
    public static byte[] drawableToByte(Drawable icon) {
        Bitmap bitmap = Bitmap.createBitmap(
                icon.getIntrinsicWidth(),
                icon.getIntrinsicHeight(),
                icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
