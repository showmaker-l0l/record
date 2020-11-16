package com.tang.shiyan3.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.tang.shiyan3.LoginActivity;
import com.tang.shiyan3.MainActivity;
import com.tang.shiyan3.R;
import com.tang.shiyan3.util.Utility;

import java.util.concurrent.ExecutionException;

public class AutoCollectService extends Service {

    private static String CHANNAL_ID = "1";

    public AutoCollectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //开启异步任务
        //Toast.makeText(this,"更新data",Toast.LENGTH_SHORT).show();
        DataUpdateTask dataUpdateTask = new DataUpdateTask(this);
        dataUpdateTask.execute();

        //前台显示通知
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = getNotification("AutoCollectService",man);
        startForeground(1,notification);
        //定时收集
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int timeLag = 1000 * 60 * 10; //10分钟
        long triggerAtTime = SystemClock.elapsedRealtime() + timeLag;
        Intent i = new Intent(this,AutoCollectService.class);
        PendingIntent  pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification(String title, NotificationManager manager) {
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationChannel mChannel = new NotificationChannel(CHANNAL_ID,"myChannel",NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(mChannel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNAL_ID);
        builder.setSmallIcon(R.drawable.awei);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.awei));
        builder.setContentText("应用正在后台收集信息...");
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        return builder.build();
    }

}