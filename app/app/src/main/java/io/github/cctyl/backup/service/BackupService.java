package io.github.cctyl.backup.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.function.Function;

import io.github.cctyl.backup.R;
import io.github.cctyl.backup.act.MainActivity;
import io.github.cctyl.backup.act.TestServiceActivity;

public class BackupService extends Service {
    public static boolean isBinder = false;

    private LocalBinder binder = new LocalBinder();
    String foregroundChannelId = "foregroundChannelId";
    String foregroundChannelName = "备份进度通知";


    String completeChannelId = "completeChannelId";
    String completeChannelName = "备份完成通知";
    private Thread t = null;




    private Function<String,String> mCallBack;
    /**
     * 状态，0结束，1运行中，2暂停，
     */
    private int mStatus = 0;

    public class LocalBinder extends Binder {


        public void setStatus(int status){
            mStatus = status;
        }


        public int getStatus(){
            return mStatus;
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
        mStatus = 1;
        isBinder = true;

        t = new Thread(() -> {

            while (true){

               if (mStatus ==1){
                   try {
                       task();
                   } catch (InterruptedException e) {
                       Log.d("BackupService", "onCreate: 睡眠被中断，结束线程运行");
                       break;
                   }
               }else if (mStatus ==2){
                   Log.d("BackupService", "onCreate: 暂停中");
                   updateStopStateProgress();
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       Log.d("BackupService", "onCreate: 睡眠被中断，结束线程运行");
                       break;
                   }
               } else {

                   Log.d("BackupService", "onCreate: 结束线程");
                   break;
               }
            }

            stopForeground(STOP_FOREGROUND_REMOVE);
            finishNotify();
            stopSelf();
        });

        t.start();

    }

    /**
     * 初始化设置通知
     */
    public void setNotification() {
        // 创建通知渠道（Android 8.0+ 必需）
        startForeground(1, getNotification(0,"备份中","正在准备中。。。"));
    }

    /**
     * 创建通知信息
     * @param progress
     * @param title
     * @param contentText
     * @return
     */
    private Notification getNotification(int progress, String title ,String contentText) {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager.getNotificationChannel(foregroundChannelId) == null){
                NotificationChannel channel = new NotificationChannel(
                        foregroundChannelId,
                        foregroundChannelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // 创建带进度条的通知
        // 必须设置，且不能为0
        // 设置优先级
        // 添加进度条：最大值100，当前进度0，不确定模式false
        return new NotificationCompat.Builder(this, foregroundChannelId)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSubText("简单备份")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)  // 必须设置，且不能为0
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // 设置优先级
                .setProgress(100, progress, false)  // 添加进度条：最大值100，当前进度0，不确定模式false
                .build();
    }

    /**
     * 结束时的通知
     */
    public void finishNotify(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager.getNotificationChannel(completeChannelId) == null){
                NotificationChannel channel = new NotificationChannel(
                        completeChannelId,
                        completeChannelName,
                        NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, completeChannelId)
                .setContentTitle("备份完成")
                .setSmallIcon(R.mipmap.ic_launcher) // 必须设置且有效
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), notification); // 必须使用与startForeground相同的ID
    }


    public void updateStopStateProgress(){
        getSystemService(NotificationManager.class).notify(1, getNotification(progress,"已暂停","点击继续备份"));
    }


    public void updateProgress(int progress){
        getSystemService(NotificationManager.class).notify(1, getNotification(progress,"备份中", progress+"%"));
    }

    private int progress = 60;


    /**
     * 主逻辑，
     * 必须支持随时打断，打断后可恢复
     * @throws InterruptedException
     */
    public void task() throws InterruptedException {

        //TODO 必须支持随时打断，打断后可恢复

        if (progress>=100){
            mStatus = 0;
            return;
        }

        Log.d("BackupService", "task: 模拟任务运行...");
        Thread.sleep(1000);

        updateProgress(++progress);
    }

    /**
     * 随时随地打断程序
     * @throws InterruptedException
     */
    public void anytimeInterrupted() throws InterruptedException {

        if(mStatus!=1){
            throw new InterruptedException();
        }

    }


    @Override
    public void onDestroy() {
        Log.d("BackupService", "onDestroy: ");
        t.interrupt();
        isBinder = false;
        super.onDestroy();
    }
}