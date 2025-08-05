package io.github.cctyl.backup.act;

import static android.content.Context.BIND_AUTO_CREATE;
import static io.github.cctyl.backup.act.MainActivity.REQUEST_CODE_SCAN;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.cctyl.backup.AppApplication;
import io.github.cctyl.backup.dao.BackupFileDao;
import io.github.cctyl.backup.dao.BackupHistoryDao;
import io.github.cctyl.backup.dao.SelectDirDao;
import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.entity.BackupFile;
import io.github.cctyl.backup.entity.SelectDir;
import io.github.cctyl.backup.entity.ServerConfig;
import io.github.cctyl.backup.service.BackupService;
import io.github.cctyl.backup.utils.DeviceUtils;
import io.github.cctyl.backup.utils.GsonUtils;
import io.github.cctyl.backup.utils.JsExecUtil;
import io.github.cctyl.backup.utils.ToastUtil;
import io.github.cctyl.backup.utils.http.OkHttpUtil;
import io.github.cctyl.backup.utils.http.SimpleRestCallback;
import okhttp3.Call;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WebAppInterface {

    private MainActivity context;
    private SharedPreferences sharedPreference;
    private JsExecUtil jsExecUtil;
    private List<BackupFile> rootChild;
    private BackupFile root;

    private BackupService.LocalBinder binder;

    private BackupHistoryDao backupHistoryDao = AppApplication.getInstance().getApplicationDatabase().backupHistoryDao();
    private BackupFileDao backupFileDao = AppApplication.getInstance().getApplicationDatabase().backupFileDao();
    private SelectDirDao selectDirDao = AppApplication.getInstance().getApplicationDatabase().selectDirDao();

    public MainActivity getContext() {
        return context;
    }


    public void setBinder(BackupService.LocalBinder binder) {
        this.binder = binder;
    }

    public JsExecUtil getJsExecUtil() {
        return jsExecUtil;
    }

    public void setRootChild(List<BackupFile> rootChild) {
        this.rootChild = rootChild;
    }

    public void setRoot(BackupFile root) {
        this.root = root;
    }

    public WebAppInterface(MainActivity context, SharedPreferences sharedPreference, JsExecUtil jsExecUtil) {
        this.context = context;
        this.sharedPreference = sharedPreference;
        this.jsExecUtil = jsExecUtil;
    }

    /**
     * 二维码扫码
     */
    @JavascriptInterface
    public void startScan() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(context, FullScreenQRCodeScanActivity.class);
        ActivityCompat.startActivityForResult(context, intent, REQUEST_CODE_SCAN, optionsCompat.toBundle());
    }

    /**
     * 获取当前备份状态
     * @return
     */
    @JavascriptInterface
    public int getStatus(){
        return binder.getStatus();
    }

    /**
     * 发送文件数据给js
     * @param list
     */
    public void sendFilesToWebView(List<BackupFile> list) {
        JSONArray fileArray = new JSONArray();
        // 添加文件列表
        for (BackupFile item : list) {
            fileArray.put(GsonUtils.toJsonObject(item));
        }

        // 将数据传递给WebView
        jsExecUtil.exec("receiveFileList", fileArray, null);

    }

    /**
     * 打开目录选择器
     * @param code
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openFolderPicker(int code) {

        ToastUtil.toastLong("建议选择根目录，即最外层目录");

        // 启动目录选择器
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        Uri rootUri = Uri.parse("content://com.android.externalstorage.documents/root/primary");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri);
        context.startActivityForResult(intent, code);
    }

    /**
     * 查询备份历史
     * @return
     */
    @JavascriptInterface
    public String getBackupHistory() {
        List<BackupHistory> list = backupHistoryDao.findAll();

        Log.d("WebAppInterface", "getBackupHistory: list数量=" + list.size());


        return GsonUtils.toJson(list);
    }


    /**
     * 获取状态栏高度
     * @return
     */
    @JavascriptInterface
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result / 2;
    }

    /**
     * 申请目录访问权限
     * @param refresh
     */
    @JavascriptInterface
    public void applyPermission(boolean refresh) {

        if (refresh) {

            Log.d("WebAppInterface", "applyPermission: 刷新uri");
            openFolderPicker(125);
        } else {
            String uri = sharedPreference.getString("uri", null);
            if (uri == null) {

                Log.d("MainActivity", "onCreate: 无数据，重新申请");
                // 启动目录选择器
                openFolderPicker(124);
            } else {

                toast("已授权！请继续操作");
            }
        }

    }

    /**
     * 获取已授权的目录uri
     * @return
     */
    @JavascriptInterface
    public String getDirUri() {
        return sharedPreference.getString("uri", null);

    }

    /**
     * 查询需要备份的文件夹信息
     * @return
     */
    @JavascriptInterface
    public String getSelectDir() {

        // 查询已选择的文件夹
        List<SelectDir> all = selectDirDao.findAll();

        Log.d("WebAppInterface", "getSelectDir: 查询结果：" + all);
        return GsonUtils.toJson(all);
    }

    /**
     * 更新需要备份的文件夹信息
     * @param json
     */
    @JavascriptInterface
    public void setSelectDir(String json) {

        List<SelectDir> directoryItems = GsonUtils.fromJsonArr(json, SelectDir.class);
        Log.d("WebAppInterface", "setSelectDir: " + directoryItems);

        // 数据库更新选择的文件夹
        int i = selectDirDao.deleteAll();

        Log.d("WebAppInterface", "setSelectDir: 删除" + i);
        List<Long> insert = selectDirDao.insert(directoryItems);

        Log.d("WebAppInterface", "setSelectDir: 插入结果：" + insert);

        toast("文件夹保存成功");
    }

    /**
     * 获取手机总存储大小
     * @return
     */
    @JavascriptInterface
    public long getTotalStorage() {
        return DeviceUtils.getTotalInternalStorageSize();
    }

    /**
     * 获取手机可用存储
     * @return
     */
    @JavascriptInterface
    public long getAvailableStorage() {
        return DeviceUtils.getAvailableInternalStorageSize();
    }

    /**
     * 获取手机品牌信息
     * @return
     */
    @JavascriptInterface
    public String getPhoneDetail() {

        return DeviceUtils.getPhoneDetail();
    }

    /**
     * 开始备份
     */
    @JavascriptInterface
    public void startBackup() {
        Log.d("WebAppInterface", "startBackup: ");

        ServerConfig serverConfig = getServerConfigObj();

        if (serverConfig.addr== null ||
        serverConfig.secret == null
        ){

            ToastUtil.toastLong("服务器配置为空！无法备份，请检查");
            return;
        }
        // 开始备份
        binder.start(serverConfig);

    }


    @JavascriptInterface
    public void checkConnection(String serverAddr,
                                  String token ){


        OkHttpUtil.get("/api/file/test", serverAddr, token,
                new SimpleRestCallback() {
                    @Override
                    public void getData(String json) {

                        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                        String msg = jsonObject.get("data").getAsString();

                        context.runOnUiThread(()->{
                            jsExecUtil.exec("receiveConnection",true,msg,null);
                        });

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        SimpleRestCallback.super.onFailure(call, e);


                        context.runOnUiThread(()->{
                            jsExecUtil.exec("receiveConnection",false,e.getMessage(),null);
                        });
                    }
                }
        );
    }

    /**
     * 准备启动，先更新界面状态
     */
    @JavascriptInterface
    public void prepareStart() {
        Log.d("WebAppInterface", "prepareStart: 准备备份");
        binder.setStatus(1);
    }

    /**
     * 提前结束备份
     */
    @JavascriptInterface
    public void completeBackup() {
        binder.setStatus(0);
        binder.stop();
    }



    /**
     * 继续备份
     * 恢复已暂停的备份
     */
    @JavascriptInterface
    public void resumeBackup() {
        binder.setStatus(1);
    }

    /**
     * 暂停备份·
     */
    @JavascriptInterface
    public void pauseBackup() {
        // 暂停备份
        binder.setStatus(2);
    }

    /**
     * 删除备份历史
     */
    @JavascriptInterface
    public void delBackupInfo(){
        backupHistoryDao.deleteAll();
        backupFileDao.deleteAll();
    }


    /**
     * 初始化根目录下的文件
     * 不对js暴露
     * @param uri
     */
    public void initRootFileList(Uri uri) {
        //根节点的初始化
        String rootDocId = DocumentsContract.getTreeDocumentId(uri);
        BackupFile root = new BackupFile("根目录",
                uri, rootDocId,
                0,
                0,
                true,
                rootDocId, "/",
                DocumentsContract.Document.MIME_TYPE_DIR,
                ""

        );
        this.setRoot(root);
        jsExecUtil.exec("receiveRoot", GsonUtils.toJsonObject(root), null);

        //拿到根目录下面的文件数据
        List<BackupFile> rootChild = getChildrenByDocId(uri, rootDocId, rootDocId);
        this.setRootChild(rootChild);
        sendFilesToWebView(rootChild);
    }

    /**
     * 根据uri 和 docid 获取这个目录下的文件
     * @param treeUri
     * @param parentDocId
     * @param rootDocId
     * @return
     */
    @SuppressLint("Range")
    public List<BackupFile> getChildrenByDocId(Uri treeUri, String parentDocId, String rootDocId) {
        return getChildrenByDocId(treeUri, parentDocId, rootDocId,false);
    }

    /**
     * 根据uri 和 docid 获取这个目录下的文件
     * 可以设置是否计算md5
     * @param treeUri
     * @param parentDocId
     * @param rootDocId
     * @param needMd5
     * @return
     */
    @SuppressLint("Range")
    public List<BackupFile> getChildrenByDocId(Uri treeUri, String parentDocId, String rootDocId,boolean needMd5) {

        List<BackupFile> list = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();


        // 先查询当前 docId 的 mimeType，判断是否是目录
        Cursor selfCursor = resolver.query(
                DocumentsContract.buildDocumentUriUsingTree(treeUri, parentDocId),
                new String[]{DocumentsContract.Document.COLUMN_MIME_TYPE},
                null, null, null
        );

        boolean isDir = false;
        if (selfCursor != null) {
            if (selfCursor.moveToFirst()) {
                String mimeType = selfCursor.getString(0);
                isDir = DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType);
            }
            selfCursor.close();
        }

        // 如果不是目录，直接返回空列表
        if (!isDir) {

            Log.d("MainActivity", "getChildrenByDocId:  " + parentDocId + " 不是目录");
            return list;
        }


        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocId);

        Cursor cursor = resolver.query(
                childrenUri,
                new String[]{
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE,
                        DocumentsContract.Document.COLUMN_SIZE,
                        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                },
                null, null, null
        );

        try {
            while (cursor != null && cursor.moveToNext()) {
                String childDocId = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID));
                String name = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                String mimeType = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE));
                long size = cursor.getLong(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE));
                long lastModified = cursor.getLong(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED));
                boolean isDirectory = DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType);
//                String relativePath = childDocId.replaceAll(rootDocId, "");
                String relativePath = childDocId.replaceAll("primary:", "");
                String md5 = "";
                if (needMd5){
                    md5 = getFileMD5(DocumentsContract.buildDocumentUriUsingTree(treeUri, childDocId));
                }
                list.add(new BackupFile(name, treeUri, childDocId, size, lastModified, isDirectory, rootDocId, relativePath, mimeType,md5));

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        Collections.sort(list, new Comparator<BackupFile>() {
            @Override
            public int compare(BackupFile o1, BackupFile o2) {

                //对list排序，如果isDirectory=true，排在前面，其次再按照name排序
                if (o1.isDirectory() && !o2.isDirectory()) {
                    return -1;
                }
                if (!o1.isDirectory() && o2.isDirectory()) {
                    return 1;
                }
                return o1.getName().compareTo(o2.getName());

            }
        });

        return list;
    }

    /**
     *
     * 通知
     * @param toast
     */
    @JavascriptInterface
    public void toast(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转到b站
     * @param bvid
     */
    @JavascriptInterface
    public void toBilibili(String bvid) {
        Uri appUri = Uri.parse("bilibili://video/" + bvid);
        Intent intent = new Intent(Intent.ACTION_VIEW, appUri);

        // 检查是否有能处理该Intent的APP（即B站是否安装）
        if (intent.resolveActivity(context. getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            // 未安装B站时，跳转到网页版
            Uri webUri = Uri.parse("https://www.bilibili.com/video/" + bvid);
            context.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }

    @JavascriptInterface
    public void toBilibiliUserProfile(String uid) {
        Uri appUri = Uri.parse("bilibili://space/" + uid);
        Intent intent = new Intent(Intent.ACTION_VIEW, appUri);

        // 检查B站是否安装
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            // 未安装时跳转网页版
            Uri webUri = Uri.parse("https://space.bilibili.com/" + uid);
            context. startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }

    @JavascriptInterface
    public void openInBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        // 检查是否有浏览器能处理该Intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context. startActivity(intent);
        } else {
            ToastUtil.toastLong("未找到浏览器应用");
        }
    }


    /**
     * 获取根目录下的文件信息,对js暴露
     * 先获取uri，没有uri则申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @JavascriptInterface
    public void initRootFile() {
        String uri = sharedPreference.getString("uri", null);
        if (uri == null) {

            Log.d("MainActivity", "onCreate: 无数据，重新申请");
            this.openFolderPicker(123);
        } else {
            Uri u = Uri.parse(uri);
            Log.d("MainActivity", "onCreate: 复用 ");

            context.runOnUiThread(() -> {
                initRootFileList(u);
            });
        }
    }

    /**
     * 获取服务器配置信息json
     * @return
     */
    @JavascriptInterface
    public String getServerConfig() {
        return GsonUtils.toJson(getServerConfigObj());
    }




    public ServerConfig getServerConfigObj() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.addr = sharedPreference.getString("addr", null);
        serverConfig.secret = sharedPreference.getString("secret", null);
        serverConfig.checkMd5 = sharedPreference.getBoolean("checkMd5", false);
        serverConfig.forceBackup = sharedPreference.getBoolean("forceBackup", false);

        return serverConfig;
    }





    /**
     * 设置服务器配置
     * @param json
     */
    @JavascriptInterface
    public void setServerConfig(String json) {
        ServerConfig serverConfig = GsonUtils.fromJson(json, ServerConfig.class);
        sharedPreference.edit()
                .putString("addr", serverConfig.addr)
                .putString("secret", serverConfig.secret)
                .putBoolean("checkMd5",serverConfig.checkMd5)
                .putBoolean("forceBackup",serverConfig.forceBackup)
                .apply();

    }

    @JavascriptInterface
    public boolean getIsFirst() {
        return sharedPreference.getBoolean("isFirst", true);
    }

    @JavascriptInterface
    public void setFirst() {
        sharedPreference.edit()
                .putBoolean("isFirst", false)
                .apply();
    }


    /**
     * 进入子目录
     * @param targetDirJson
     * @return
     */
    @JavascriptInterface
    public String intoChild(String targetDirJson) {

        Log.d("WebAppInterface", "intoChild: ");
        //转换 DirectoryItem
        BackupFile targetDir = GsonUtils.fromJson(targetDirJson, BackupFile.class);
        List<BackupFile> childrenByDocId = getChildrenByDocId(targetDir.getTreeUri(), targetDir.getDocId(), targetDir.getRootDocId());


        return GsonUtils.toJson(childrenByDocId);

    }


    /**
     * 计算文件md5，不对外暴露
     * @param fileUri
     * @return
     */
    public String getFileMD5(Uri fileUri) {
        ContentResolver resolver = context.getContentResolver();
        try (InputStream is = resolver.openInputStream(fileUri)) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = is.read(byteArray)) != -1) {
                md5.update(byteArray, 0, bytesCount);
            }
            byte[] bytes = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算某个文件的md5
     * @param targetDirJson
     */
    @JavascriptInterface
    public void getMd5(String targetDirJson) {

        Log.d("WebAppInterface", "getMd5: ");
        //转换 DirectoryItem
        BackupFile targetDir = GsonUtils.fromJson(targetDirJson, BackupFile.class);
        Uri uri = DocumentsContract.buildDocumentUriUsingTree(targetDir.getTreeUri(), targetDir.getDocId());
        String fileMD5 = getFileMD5(uri);

        toast(targetDir.getName() + "的md5是：" + fileMD5);


    }

    /**
     * 进入指定父级，如果穿空表示进入root
     * @param parentDirJson
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @JavascriptInterface
    public String intoParent(String parentDirJson) {


        Log.d("WebAppInterface", "intoParent: ");
        if (parentDirJson == null) {

            return GsonUtils.toJson(rootChild);
        }
        //转换 DirectoryItem
        BackupFile parentDir = GsonUtils.fromJson(parentDirJson, BackupFile.class);

        Log.d("WebAppInterface", "intoParent: " + parentDir.getDocId());
        List<BackupFile> childrenByDocId = getChildrenByDocId(parentDir.getTreeUri(), parentDir.getDocId(), parentDir.getRootDocId());


        return GsonUtils.toJson(childrenByDocId);

    }

    /**
     * 通知js 备份状态更新
     * @param mStatus
     */
    public void receiveBackupStatus(int mStatus) {

        Log.d("WebAppInterface", "receiveBackupStatus: 更新界面备份状态 ");
        context.runOnUiThread(() -> {
            jsExecUtil.exec("receiveBackupStatus",
                    mStatus,
                    null
            );
        });
    }

    /**
     * 通知js ,备份出错
     * @param msg
     */
    public void receiveUploadError(String msg) {

        Log.d("WebAppInterface", "receiveUploadError: 通知备份错误 ");
        context.runOnUiThread(() -> {
            jsExecUtil.exec("receiveUploadError",
                    msg,
                    null
            );
        });
    }

    /**
     * 通知js，备份界面信息更新
     * @param jsonObject
     */
    public void receiveProgressData(JSONObject jsonObject) {

        context.runOnUiThread(() -> {
            jsExecUtil.exec("receiveProgressData",
                    jsonObject,
                    null
            );
        });
    }


    /**
     * 通知js无需备份
     */
    public void receiveNotNeedBackup() {
        context.runOnUiThread(() -> {
            jsExecUtil.exec("receiveNotNeedBackup",
                    null
            );
        });
    }

    /**
     * 通知js已经是最新，无需备份
     */
    public void receiveServerAlreadyLatest() {
        context.runOnUiThread(() -> {
            jsExecUtil.exec("receiveServerAlreadyLatest",
                    null
            );
        });
    }
}
