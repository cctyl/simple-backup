package io.github.cctyl.backup.act;

import static io.github.cctyl.backup.act.MainActivity.REQUEST_CODE_SCAN;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.gson.JsonObject;

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
import io.github.cctyl.backup.entity.DirectoryItem;
import io.github.cctyl.backup.entity.SelectDir;
import io.github.cctyl.backup.entity.ServerConfig;
import io.github.cctyl.backup.utils.DeviceUtils;
import io.github.cctyl.backup.utils.GsonUtils;
import io.github.cctyl.backup.utils.JsExecUtil;
import io.github.cctyl.backup.utils.ToastUtil;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WebAppInterface {

    private MainActivity context;
    private SharedPreferences sharedPreference;
    private JsExecUtil jsExecUtil;
    private List<DirectoryItem> rootChild;
    private DirectoryItem root;

    private BackupHistoryDao backupHistoryDao = AppApplication.getInstance().getApplicationDatabase().backupHistoryDao();
    private SelectDirDao selectDirDao = AppApplication.getInstance().getApplicationDatabase().selectDirDao();


    public JsExecUtil getJsExecUtil() {
        return jsExecUtil;
    }

    public void setRootChild(List<DirectoryItem> rootChild) {
        this.rootChild = rootChild;
    }

    public void setRoot(DirectoryItem root) {
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


    public void sendFilesToWebView(List<DirectoryItem> list) {
        JSONArray fileArray = new JSONArray();
        // 添加文件列表
        for (DirectoryItem item : list) {
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
    public void addTestHistory() {


        List<BackupHistory> testData = new ArrayList<>();

        // 2025-07-25 晚间备份 - 成功
        BackupHistory history1 = new BackupHistory();
        history1.setId(11);
        history1.setBackupResult("备份完成");
        history1.setBackupDetail("成功备份了今日新增的156张照片和12个文档");
        history1.setSuccess(true);
        history1.setBackUpTime(LocalDateTime.of(2025, 7, 25, 22, 30, 15));
        history1.setBackUpNum(168L);
        history1.setBackUpCostTime(285);
        history1.setTotalFileSize(842);
        history1.setBackUpPathArr("/storage/emulated/0/DCIM/Camera,/storage/emulated/0/Documents");
        testData.add(history1);

        // 2025-07-24 晚间备份 - 成功
        BackupHistory history2 = new BackupHistory();
        history2.setId(12);
        history2.setBackupResult("备份完成");
        history2.setBackupDetail("成功备份了工作项目文件");
        history2.setSuccess(true);
        history2.setBackUpTime(LocalDateTime.of(2025, 7, 24, 23, 15, 30));
        history2.setBackUpNum(78L);
        history2.setBackUpCostTime(122);
        history2.setTotalFileSize(1520);
        history2.setBackUpPathArr("/storage/emulated/0/WorkProjects");
        testData.add(history2);

        // 2025-07-23 晚间备份 - 失败
        BackupHistory history3 = new BackupHistory();
        history3.setId(13);
        history3.setBackupResult("备份失败");
        history3.setBackupDetail("网络连接中断，未能完成云同步");
        history3.setSuccess(false);
        history3.setBackUpTime(LocalDateTime.of(2025, 7, 23, 21, 45, 5));
        history3.setBackUpNum(0L);
        history3.setBackUpCostTime(35);
        history3.setTotalFileSize(0);
        history3.setBackUpPathArr("/storage/emulated/0/CloudSync");
        testData.add(history3);

        // 2025-07-22 晚间备份 - 成功
        BackupHistory history4 = new BackupHistory();
        history4.setId(14);
        history4.setBackupResult("备份完成");
        history4.setBackupDetail("成功备份了手机数据");
        history4.setSuccess(true);
        history4.setBackUpTime(LocalDateTime.of(2025, 7, 22, 20, 5, 42));
        history4.setBackUpNum(25L);
        history4.setBackUpCostTime(480);
        history4.setTotalFileSize(3650);
        history4.setBackUpPathArr("/data/data/com.android.contacts,/data/data/com.android.sms");
        testData.add(history4);

        // 2025-07-21 晚间备份 - 成功
        BackupHistory history5 = new BackupHistory();
        history5.setId(15);
        history5.setBackupResult("备份完成");
        history5.setBackupDetail("成功备份了媒体文件");
        history5.setSuccess(true);
        history5.setBackUpTime(LocalDateTime.of(2025, 7, 21, 23, 52, 18));
        history5.setBackUpNum(892L);
        history5.setBackUpCostTime(650);
        history5.setTotalFileSize(4280);
        history5.setBackUpPathArr("/storage/emulated/0/Music,/storage/emulated/0/Movies");
        testData.add(history5);

        // 2025-07-20 晚间备份 - 失败
        BackupHistory history6 = new BackupHistory();
        history6.setId(16);
        history6.setBackupResult("备份失败");
        history6.setBackupDetail("存储空间不足，仅备份了部分文件");
        history6.setSuccess(false);
        history6.setBackUpTime(LocalDateTime.of(2025, 7, 20, 22, 18, 33));
        history6.setBackUpNum(423L);
        history6.setBackUpCostTime(120);
        history6.setTotalFileSize(2100);
        history6.setBackUpPathArr("/storage/emulated/0/Downloads,/storage/emulated/0/DCIM/Screenshots");
        testData.add(history6);

        // 2025-07-19 晚间备份 - 成功
        BackupHistory history7 = new BackupHistory();
        history7.setId(17);
        history7.setBackupResult("备份完成");
        history7.setBackupDetail("成功备份了重要文档和表格");
        history7.setSuccess(true);
        history7.setBackUpTime(LocalDateTime.of(2025, 7, 19, 19, 35, 7));
        history7.setBackUpNum(36L);
        history7.setBackUpCostTime(75);
        history7.setTotalFileSize(265);
        history7.setBackUpPathArr("/storage/emulated/0/Documents/Work,/storage/emulated/0/Sheets");
        testData.add(history7);

        // 2025-07-18 晚间备份 - 成功
        BackupHistory history8 = new BackupHistory();
        history8.setId(18);
        history8.setBackupResult("备份完成");
        history8.setBackupDetail("成功备份了所有选定数据");
        history8.setSuccess(true);
        history8.setBackUpTime(LocalDateTime.of(2025, 7, 18, 21, 22, 50));
        history8.setBackUpNum(1542L);
        history8.setBackUpCostTime(520);
        history8.setTotalFileSize(3890);
        history8.setBackUpPathArr("/storage/emulated/0/DCIM,/storage/emulated/0/Pictures,/storage/emulated/0/Documents");
        testData.add(history8);

        // 2025-07-17 晚间备份 - 失败
        BackupHistory history9 = new BackupHistory();
        history9.setId(19);
        history9.setBackupResult("备份失败");
        history9.setBackupDetail("用户取消了备份操作");
        history9.setSuccess(false);
        history9.setBackUpTime(LocalDateTime.of(2025, 7, 17, 20, 10, 25));
        history9.setBackUpNum(0L);
        history9.setBackUpCostTime(0);
        history9.setTotalFileSize(0);
        history9.setBackUpPathArr("/storage/emulated/0/AllFiles");
        testData.add(history9);

        // 2025-07-16 晚间备份 - 成功
        BackupHistory history10 = new BackupHistory();
        history10.setId(20);
        history10.setBackupResult("备份完成");
        history10.setBackupDetail("成功备份了系统设置和应用数据");
        history10.setSuccess(true);
        history10.setBackUpTime(LocalDateTime.of(2025, 7, 16, 22, 45, 11));
        history10.setBackUpNum(18L);
        history10.setBackUpCostTime(320);
        history10.setTotalFileSize(125);
        history10.setBackUpPathArr("/system/settings,/data/app");
        testData.add(history10);


        backupHistoryDao.insert(testData);

    }

    @JavascriptInterface
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result/2;
    }

    @JavascriptInterface
    public void applyPermission(boolean refresh){
        
        if (refresh){

            Log.d("WebAppInterface", "applyPermission: 刷新uri");
            openFolderPicker(124);
        }else {
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
    public String getDirUri(){
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

        toast("保存成功");
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

    public void initRootFileList(Uri uri) {
        //根节点的初始化
        String rootDocId = DocumentsContract.getTreeDocumentId(uri);
        DirectoryItem root = new DirectoryItem("根目录",
                uri, rootDocId, 0, 0, true, rootDocId, "/", DocumentsContract.Document.MIME_TYPE_DIR);
        this.setRoot(root);
        jsExecUtil.exec("receiveRoot", GsonUtils.toJsonObject(root), null);

        //拿到根目录下面的文件数据
        List<DirectoryItem> rootChild = getChildrenByDocId(uri, rootDocId, rootDocId);
        this.setRootChild(rootChild);
        sendFilesToWebView(rootChild);
    }

    @SuppressLint("Range")
    public List<DirectoryItem> getChildrenByDocId(Uri treeUri, String parentDocId, String rootDocId) {

        List<DirectoryItem> list = new ArrayList<>();
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

                list.add(new DirectoryItem(name, treeUri, childDocId, size, lastModified, isDirectory, rootDocId, relativePath, mimeType));

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        Collections.sort(list, new Comparator<DirectoryItem>() {
            @Override
            public int compare(DirectoryItem o1, DirectoryItem o2) {

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
        DirectoryItem targetDir = GsonUtils.fromJson(targetDirJson, DirectoryItem.class);
        List<DirectoryItem> childrenByDocId = getChildrenByDocId(targetDir.getTreeUri(), targetDir.getDocId(), targetDir.getRootDocId());


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
        DirectoryItem targetDir = GsonUtils.fromJson(targetDirJson, DirectoryItem.class);
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
        DirectoryItem parentDir = GsonUtils.fromJson(parentDirJson, DirectoryItem.class);

        Log.d("WebAppInterface", "intoParent: " + parentDir.getDocId());
        List<DirectoryItem> childrenByDocId = getChildrenByDocId(parentDir.getTreeUri(), parentDir.getDocId(), parentDir.getRootDocId());


        return GsonUtils.toJson(childrenByDocId);

    }


}
