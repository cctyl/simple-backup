package io.github.cctyl.backup.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.function.Function;

import io.github.cctyl.backup.R;
import io.github.cctyl.backup.act.TestServiceActivity;

public class BackupService extends Service {

    private LocalBinder binder = new LocalBinder();

    private Thread t = null;

    private Function<String,String> mCallBack;
    /**
     * 状态，0结束，1运行中，2暂停，
     */
    private int status ;

    public class LocalBinder extends Binder {


        public void receiveCallback(Function<String,String> callBack ){
            mCallBack = callBack;
        }
    }

    public BackupService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BackupService", "onBind: ");
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BackupService", "onCreate: ");
        setNotification();
        status = 1;

        t = new Thread(() -> {

            while (true){

               if (status==1){

                   try {
                       task();
                       mCallBack.apply("运行一次");
                   } catch (InterruptedException e) {

                       Log.d("BackupService", "onCreate: 睡眠被中断，结束线程运行");
                       break;
                   }

               }else if (status==2){


                   Log.d("BackupService", "onCreate: 暂停中");

                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       Log.d("BackupService", "onCreate: 睡眠被中断，结束线程运行");
                       break;
                   }
               }else {

                   Log.d("BackupService", "onCreate: 结束线程");
                   break;
               }


            }




        });

        t.start();

    }


    public void setNotification() {
        // 创建通知渠道（Android 8.0+ 必需）
        String channelId = "my_channel_id";
        String channelName = "My Foreground Service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, TestServiceActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // 创建带进度条的通知
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)  // 必须设置，且不能为0
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // 设置优先级
                .setProgress(100, 0, false)  // 添加进度条：最大值100，当前进度0，不确定模式false
                .build();

        startForeground(1, notification);
    }

    // 更新进度条
    private void updateNotificationProgress(int progress) {
        String channelId = "my_channel_id"; // 必须与创建时相同

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setSmallIcon(R.mipmap.ic_launcher) // 必须设置且有效
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setProgress(100, progress, false) // 更新进度
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, notification); // 必须使用与startForeground相同的ID
    }


    public void finishNottify(){
        String channelId = "my_channel_id"; // 必须与创建时相同

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("备份完成")
                .setSmallIcon(R.mipmap.ic_launcher) // 必须设置且有效
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), notification); // 必须使用与startForeground相同的ID
    }
    private int progress = 95;
    public void task() throws InterruptedException {


        if (progress>=100){

            status = 0;
            stopForeground(STOP_FOREGROUND_REMOVE);
            finishNottify();
            stopSelf();
            return;
        }

        Log.d("BackupService", "task: 模拟任务运行...");
        Thread.sleep(1000);

        updateNotificationProgress(++progress);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("BackupService", "onStartCommand: ");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("BackupService", "onDestroy: ");
        t.interrupt();
        super.onDestroy();
    }
}