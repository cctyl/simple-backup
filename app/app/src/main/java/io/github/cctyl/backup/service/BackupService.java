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
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.cctyl.backup.AppApplication;
import io.github.cctyl.backup.R;
import io.github.cctyl.backup.act.MainActivity;
import io.github.cctyl.backup.act.WebAppInterface;
import io.github.cctyl.backup.dao.BackupFileDao;
import io.github.cctyl.backup.dao.BackupHistoryDao;
import io.github.cctyl.backup.dao.SelectDirDao;
import io.github.cctyl.backup.dto.ProgressDto;
import io.github.cctyl.backup.entity.BackupFile;
import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.entity.SelectDir;
import io.github.cctyl.backup.utils.GsonUtils;
import io.github.cctyl.backup.utils.ToastUtil;
@RequiresApi(api = Build.VERSION_CODES.O)
public class BackupService extends Service {

    private BackupFileDao backupFileDao = AppApplication.getInstance().getApplicationDatabase().backupFileDao();
    private BackupHistoryDao backupHistoryDao = AppApplication.getInstance().getApplicationDatabase().backupHistoryDao();
    private SelectDirDao selectDirDao = AppApplication.getInstance().getApplicationDatabase().selectDirDao();
    private LocalBinder binder = new LocalBinder();
    private LocalDateTime startTime = null;
    String foregroundChannelId = "foregroundChannelId";
    String foregroundChannelName = "备份进度通知";


    String completeChannelId = "completeChannelId";
    String completeChannelName = "备份完成通知";
    private Thread t = null;

    private WebAppInterface mWebAppInterface;

    private Function<String, String> mCallBack;
    /**
     * 状态，0结束，1运行中，2暂停，
     */
    private int mStatus = 0;


    public class LocalBinder extends Binder {


        public void setStatus(int status) {
            mStatus = status;
        }


        public int getStatus() {
            return mStatus;
        }


        public void receiveWebInterface(WebAppInterface webAppInterface) {

            mWebAppInterface = webAppInterface;
        }


        public void start(){
            setNotification();
            mStatus = 1;

            t = new Thread(() -> {
                Log.d("BackupService", "onCreate: 线程启动");
                startTime = LocalDateTime.now();
                BackupHistory backupHistory = null;
                BackupHistory dto = new BackupHistory();
                try {
                    progressDto.setStartTime(startTime);
                    getSystemService(NotificationManager.class).notify(1,
                            getNotification(100, "准备中", "请稍后，正在检查哪些文件需要备份...")
                    );
                    List<SelectDir> selectDirList = selectDirDao.findAll();
                    if (selectDirList == null || selectDirList.isEmpty()) {
                        Log.d("BackupService", "onCreate: 未选择任何文件夹");

                        ToastUtil.toast("错误！未选择需要备份的文件夹");
                        return;
                    }

                    backupHistory = initHistory(selectDirList);


                    //任务流程是： 先找到需要备份的，然后逐个上传，所以是局部循环，而不是整体循环

                    //1. 递归查找需要备份的文件
                    Set<Long> updatedIds = new HashSet<>();
                    List<BackupFile> initParent = getBackupFileFromSelectDir(selectDirList);
                    recursiveGetChildren(initParent, updatedIds);
                    Log.d("BackupService", "onCreate: 文件查找完成");
                    if (updatedIds.isEmpty()){


                        Log.d("LocalBinder", "start: 无需备份");
                        mWebAppInterface.receiveNotNeedBackup();
                        stopForeground(STOP_FOREGROUND_REMOVE);

                        backupHistoryDao.delete(backupHistory);
                        return;
                    }





                    //TODO 逐个上传文件
                    updateProgress(0);
                    int totalSize = updatedIds.size();
                    progressDto.setNeedUploadFileNum(totalSize);
                    progressDto.setCheckFinish(true);
                    double count = 0;
                    for (Long updatedId : updatedIds) {

                        BackupFile file  = backupFileDao.findById(updatedId);

                        //模拟上传耗时
                        Thread.sleep(200);
                        count++;

                        progressDto.setAlreadyUploadFileNum((int) count);
                        progressDto.setAlreadyUploadFileSize(
                                progressDto.getAlreadyUploadFileSize()+file.getSize()

                        );
                        progressDto.setTotalPercent((int) (count/ totalSize *100));
                        progressDto.setSpeed(ThreadLocalRandom.current().nextInt(1, 10000000));//TODO 上传速度
                        updateProgress(progressDto.getTotalPercent());
                        ProgressDto.CurrentFile currentFile = progressDto.getCurrentFile();
                        currentFile.setMimeType(file.getMimeType());
                        currentFile.setName(file.getName());
                        currentFile.setRelativePath(file.getRelativePath());
                        currentFile.setPercent(ThreadLocalRandom.current().nextInt(1, 100));//TODO 当前文件上传进度

                        mWebAppInterface.receiveProgressData( GsonUtils.toJsonObject(progressDto));

                    }

                    dto.setBackUpNum((long) count);
                    dto.setBackupResult("备份成功");
                    dto.setBackupDetail("今天也保证了数据安全！");
                    dto.setSuccess(true);
                    dto.setTotalFileSize(progressDto.getAlreadyUploadFileSize());
                    finishNotify();
                    ToastUtil.toastLong("备份完成！");
                } catch (InterruptedException e) {
                    dto.setBackUpNum((long) progressDto.getAlreadyUploadFileNum());
                    dto.setSuccess(false);
                    dto.setTotalFileSize(progressDto.getAlreadyUploadFileSize());

                    dto.setBackupResult("备份中断");
                    dto.setBackupDetail("备份过程中被取消了");
                    Log.d("BackupService", "onCreate: 线程被打断运行");
                    ToastUtil.toast("备份已取消！");
                } catch (Exception e) {
                    dto.setBackUpNum((long) progressDto.getAlreadyUploadFileNum());
                    dto.setTotalFileSize(progressDto.getAlreadyUploadFileSize());
                    dto.setSuccess(false);


                    dto.setBackupResult("备份出错");
                    dto.setBackupDetail("备份过程中出现错误");
                    Log.e("BackupService", "onCreate: 备份出错：",e);
                    ToastUtil.toastLong("备份异常！错误信息：" + e.getMessage());
                }

                updateHistory(backupHistory, dto);
                mStatus = 0;
                //通知界面备份完成
                mWebAppInterface.receiveBackupStatus(mStatus);
                Log.d("BackupService", "onCreate: 线程结束");
                stopForeground(STOP_FOREGROUND_REMOVE);
            });

            t.start();
        }


        public void stop(){

            mStatus = 0;
            if (t!=null){
                t.interrupt();
                stopForeground(STOP_FOREGROUND_REMOVE);
                cancelNotify();
            }

        }
    }

    public BackupService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BackupService", "onBind: ");
        return binder;
    }


    private ProgressDto progressDto = new ProgressDto();

    /**
     *  更新准备中的界面信息
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUiCheckData(BackupFile file, int needUploadFileNum) {
        /*


            totalPercent: 30,
            alreadyUploadFileSize: 72,
            currentFile: {
                mimeType: 'text/html',
                name: 'IMG_20230715_184523.jpg',
                relativePath: '内部存储/DCIM/Camera',
                percent: 90,
            },
            speed: 20481,//上传速度
            startTime: '2025-07-26T18:45:23Z', //ISO 8601 格式的时间字符串 示例："2023-07-15T18:45:23Z"
            alreadyUploadFileNum: 1,

         */
        int progress = 0;



        progressDto.setTotalPercent(progress);
        progressDto.setAlreadyUploadFileSize(0L);
        progressDto.setSpeed(0);

        progressDto.setAlreadyUploadFileNum(0);
        progressDto.setCheckFinish(false);
        progressDto.setNeedUploadFileNum(needUploadFileNum);
        ProgressDto.CurrentFile currentFile = progressDto.getCurrentFile();
        currentFile.setMimeType(file.getMimeType());
        currentFile.setName(file.getName());
        currentFile.setRelativePath(file.getRelativePath());
        currentFile.setPercent(100);

        mWebAppInterface.getContext().runOnUiThread(() -> {
            mWebAppInterface.getJsExecUtil().exec("receiveProgressData",
                    GsonUtils.toJsonObject(progressDto),
                    null
            );
        });

    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUiStopData() {


        progressDto.setSpeed(0);
        progressDto.setCheckFinish(true);

        mWebAppInterface.receiveProgressData( GsonUtils.toJsonObject(progressDto));


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void recursiveGetChildren(List<BackupFile> parentReal, Set<Long> updatedIds) throws InterruptedException {

        for (BackupFile real : parentReal) {

            // 根据relativePath 查找数据库，先比较最后修改时间,不同，则再比较md5
            BackupFile db = backupFileDao.findByRelativePath(real.getRelativePath());

            if (db == null) {
                long id = backupFileDao.insertOne(real);
                real.setId(id);
                updatedIds.add(id);
            } else {
                if (db.getLastModified() != real.getLastModified()) {
                    if (real.isDirectory() || db.isDirectory()) {
                        //两者其一是目录，并且修改时间不相同，有必要比较看文件类型是否发生了变化
                        if (db.isDirectory() && real.isDirectory()) {
                            //都是目录，不需要比较md5
                        } else {
                            //db不是目录了，发生了文件类型变化
                            real.setId(db.getId());
                            backupFileDao.updateOne(real);
                            updatedIds.add(db.getId());
                        }

                    } else {
                        //不是目录，计算md5
                        String md5 = getMd5(real);
                        real.setMd5(md5);
                        if (!db.getMd5().equals(real.getMd5())) {
                            real.setId(db.getId());
                            backupFileDao.updateOne(real);
                            updatedIds.add(db.getId());
                        } else {
                            db.setLastModified(real.getLastModified());
                            backupFileDao.updateOne(db);
                        }
                    }
                }
            }


            // 查找文件时，需要给前端一个友好提示，也就是需要更新界面
            updateUiCheckData(real,updatedIds.size());

            if (real.isDirectory()) {
                // 查出real的子级，继续递归
                List<BackupFile> childrenReal = mWebAppInterface.getChildrenByDocId(real.getTreeUri(), real.getDocId(), real.getRootDocId());
                recursiveGetChildren(childrenReal, updatedIds);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BackupService", "onCreate: ");


    }

    @NonNull
    private static List<BackupFile> getBackupFileFromSelectDir(List<SelectDir> selectDirList) {
        List<BackupFile> initParent = selectDirList.stream().map(selectDir -> {

            // 真的拿到实际数据
            BackupFile backupFile = new BackupFile();
            backupFile.setName(selectDir.getName());
            backupFile.setTreeUri(selectDir.getTreeUri());
            backupFile.setDocId(selectDir.getDocId());
            backupFile.setRootDocId(selectDir.getRootDocId());
            backupFile.setRelativePath(selectDir.getRelativePath());
            backupFile.setDirectory(true);
            backupFile.setMimeType(DocumentsContract.Document.MIME_TYPE_DIR);
            //目录不需要md5
            backupFile.setMd5("");
            return backupFile;
        }).collect(Collectors.toList());
        return initParent;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getMd5(BackupFile file) {
        return mWebAppInterface.getFileMD5(DocumentsContract.buildDocumentUriUsingTree(file.getTreeUri(), file.getDocId()));
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateHistory(BackupHistory backupHistory, BackupHistory dto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = backupHistory.getBackUpTime();

        //任务耗时
        long seconds = Duration.between(startTime, now).getSeconds();
        backupHistory.setBackUpCostTime((int) seconds);


        backupHistory.setBackupResult(dto.getBackupResult());
        backupHistory.setBackupDetail(dto.getBackupDetail());
        backupHistory.setSuccess(dto.getSuccess());
        backupHistory.setBackUpNum(dto.getBackUpNum());
        backupHistory.setTotalFileSize(dto.getTotalFileSize());


        backupHistoryDao.updateOne(backupHistory);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private BackupHistory initHistory(List<SelectDir> selectDirList) {
        String pathArr = selectDirList.stream().map(SelectDir::getRelativePath).collect(Collectors.joining(","));
        BackupHistory backupHistory = new BackupHistory();
        backupHistory.setBackUpTime(startTime);
        backupHistory.setBackUpPathArr(pathArr);
        Long id = backupHistoryDao.insertOne(backupHistory);
        backupHistory.setId(id);

        return backupHistory;

    }

    /**
     * 初始化设置通知
     */
    public void setNotification() {
        // 创建通知渠道（Android 8.0+ 必需）
        startForeground(1, getNotification(0, "备份中", "正在准备中。。。"));
    }

    /**
     * 创建通知信息
     *
     * @param progress
     * @param title
     * @param contentText
     * @return
     */
    private Notification getNotification(int progress, String title, String contentText) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager.getNotificationChannel(foregroundChannelId) == null) {
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, foregroundChannelId)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSubText("简单备份")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)  // 必须设置，且不能为0
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);  // 设置优先级
        if (progress >= 0) {

            builder.setProgress(100, progress, false);  // 添加进度条：最大值100，当前进度0，不确定模式false
        }

        return builder.build();
    }

    /**
     * 结束时的通知
     */
    public void finishNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager.getNotificationChannel(completeChannelId) == null) {
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


    public void cancelNotify(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager.getNotificationChannel(completeChannelId) == null) {
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
                .setContentTitle("备份被取消")
                .setSmallIcon(R.mipmap.ic_launcher) // 必须设置且有效
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), notification); // 必须使用与startForeground相同的ID

    }

    public void updateStopStateProgress() {
        getSystemService(NotificationManager.class).notify(1, getNotification(progressDto.getTotalPercent(), "已暂停", "点击继续备份"));
    }


    public void updateProgress(int progress) {
        getSystemService(NotificationManager.class).notify(1, getNotification(progress, "备份中", progress + "%"));
    }




    /**
     * 随时随地打断程序
     * anytimeInterrupted 的简写
     *
     * @throws InterruptedException
     */
    public void i() throws InterruptedException {
        anytimeInterrupted();
    }

    /**
     * 随时随地打断程序
     *
     * @throws InterruptedException
     */

    public void anytimeInterrupted() throws InterruptedException {

        if (mStatus == 1) {
            return;
        } else if (mStatus == 2) {
            Log.d("BackupService", "onCreate: 暂停中");
            updateStopStateProgress();
            updateUiStopData();
            try {
                while (mStatus == 2) {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Log.d("BackupService", "onCreate: 睡眠被中断，结束线程运行");
                throw e;
            }

            Log.d("BackupService", "anytimeInterrupted: 结束暂停，继续备份");

        } else {

            Log.d("BackupService", "onCreate: 结束线程,mStatus=" + mStatus);
            throw new InterruptedException();
        }
    }


    @Override
    public void onDestroy() {
        Log.d("BackupService", "onDestroy: ");
        t.interrupt();
        super.onDestroy();
    }
}