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

import org.json.JSONArray;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.cctyl.backup.AppApplication;
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

@RequiresApi(api = Build.VERSION_CODES.O)
public class WebAppInterface {

    private MainActivity context;
    private SharedPreferences sharedPreference;
    private JsExecUtil jsExecUtil;
    private List<BackupFile> rootChild;
    private BackupFile root;

    private BackupHistoryDao backupHistoryDao = AppApplication.getInstance().getApplicationDatabase().backupHistoryDao();
    private SelectDirDao selectDirDao = AppApplication.getInstance().getApplicationDatabase().selectDirDao();

    public MainActivity getContext() {
        return context;
    }

    private static boolean isBind = false;

    public void bindBackupService() {
        Log.d("WebAppInterface", "bindBackupService: ");
        Intent bindIntent = new Intent(context, BackupService.class);
        context.bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
    }

    public void unbindBackupService() {
        if (isBind) {
            isBind = false;
            context.unbindService(connection);
        } else {
            Log.d("WebAppInterface", "unbindBackupService: 未绑定，不要重复解绑");
        }
    }

    private BackupService.LocalBinder binder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBind = true;
            binder = (BackupService.LocalBinder) service;
            binder.receiveWebInterface(WebAppInterface.this);
            Log.d("TestServiceActivity", "onServiceConnected: 链接了");
        }
    };

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

    @JavascriptInterface
    public void startScan() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(context, FullScreenQRCodeScanActivity.class);
        ActivityCompat.startActivityForResult(context, intent, REQUEST_CODE_SCAN, optionsCompat.toBundle());
    }


    @JavascriptInterface
    public int getStatus(){

        if (binder==null ){

            if ( BackupService.isBinder){
                Log.d("WebAppInterface", "getStatus: 重新绑定");
                bindBackupService();
                return binder.getStatus();
            }else {
                return 0;
            }
        }else {
            return binder.getStatus();
        }

    }

    public void sendFilesToWebView(List<BackupFile> list) {
        JSONArray fileArray = new JSONArray();
        // 添加文件列表
        for (BackupFile item : list) {
            fileArray.put(GsonUtils.toJsonObject(item));
        }

        // 将数据传递给WebView
        jsExecUtil.exec("receiveFileList", fileArray, null);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @JavascriptInterface
    public void openFolderPicker(int code) {

        ToastUtil.toastLong("建议选择根目录，即最外层目录");

        // 启动目录选择器
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        Uri rootUri = Uri.parse("content://com.android.externalstorage.documents/root/primary");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri);
        context.startActivityForResult(intent, code);
    }

    @JavascriptInterface
    public String getBackupHistory() {
        List<BackupHistory> list = backupHistoryDao.findAll();

        Log.d("WebAppInterface", "getBackupHistory: list数量=" + list.size());


        return GsonUtils.toJson(list);
    }




    @JavascriptInterface
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result / 2;
    }

    @JavascriptInterface
    public void applyPermission(boolean refresh) {

        if (refresh) {

            Log.d("WebAppInterface", "applyPermission: 刷新uri");
            openFolderPicker(124);
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


    @JavascriptInterface
    public String getDirUri() {
        return sharedPreference.getString("uri", null);

    }

    @JavascriptInterface
    public String getSelectDir() {

        // 查询已选择的文件夹
        List<SelectDir> all = selectDirDao.findAll();

        Log.d("WebAppInterface", "getSelectDir: 查询结果：" + all);
        return GsonUtils.toJson(all);
    }

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

    @JavascriptInterface
    public long getTotalStorage() {
        return DeviceUtils.getTotalInternalStorageSize();
    }

    @JavascriptInterface
    public long getAvailableStorage() {
        return DeviceUtils.getAvailableInternalStorageSize();
    }

    @JavascriptInterface
    public String getPhoneDetail() {

        return DeviceUtils.getPhoneDetail();
    }


    @JavascriptInterface
    public void startBackup() {
        // 开始备份
        bindBackupService();
    }

    @JavascriptInterface
    public void completeBackup() {
        //  提前结束备份

        binder.setStatus(0);
        unbindBackupService();

    }

    @JavascriptInterface
    public void resumeBackup() {
        // 继续备份
        binder.setStatus(1);
    }

    @JavascriptInterface
    public void pauseBackup() {
        // 暂停备份
        binder.setStatus(2);
    }

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
    @SuppressLint("Range")
    public List<BackupFile> getChildrenByDocId(Uri treeUri, String parentDocId, String rootDocId) {
        return getChildrenByDocId(treeUri, parentDocId, rootDocId,false);
    }
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

    @JavascriptInterface
    public void toast(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @JavascriptInterface
    public void init() {
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


    @JavascriptInterface
    public String getServerConfig() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.addr = sharedPreference.getString("addr", null);
        serverConfig.secret = sharedPreference.getString("secret", null);

        return GsonUtils.toJson(serverConfig);
    }

    @JavascriptInterface
    public void setServerConfig(String json) {
        ServerConfig serverConfig = GsonUtils.fromJson(json, ServerConfig.class);
        sharedPreference.edit()
                .putString("addr", serverConfig.addr)
                .putString("secret", serverConfig.secret)
                .apply();

    }

    @JavascriptInterface
    public String intoChild(String targetDirJson) {

        Log.d("WebAppInterface", "intoChild: ");
        //转换 DirectoryItem
        BackupFile targetDir = GsonUtils.fromJson(targetDirJson, BackupFile.class);
        List<BackupFile> childrenByDocId = getChildrenByDocId(targetDir.getTreeUri(), targetDir.getDocId(), targetDir.getRootDocId());


        return GsonUtils.toJson(childrenByDocId);

    }

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


    @JavascriptInterface
    public void getMd5(String targetDirJson) {

        Log.d("WebAppInterface", "getMd5: ");
        //转换 DirectoryItem
        BackupFile targetDir = GsonUtils.fromJson(targetDirJson, BackupFile.class);
        Uri uri = DocumentsContract.buildDocumentUriUsingTree(targetDir.getTreeUri(), targetDir.getDocId());
        String fileMD5 = getFileMD5(uri);

        toast(targetDir.getName() + "的md5是：" + fileMD5);


    }

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


}
