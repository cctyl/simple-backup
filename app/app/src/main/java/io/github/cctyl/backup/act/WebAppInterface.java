package io.github.cctyl.backup.act;

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

import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.cctyl.backup.AppApplication;
import io.github.cctyl.backup.dao.BackupHistoryDao;
import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.entity.DirectoryItem;
import io.github.cctyl.backup.utils.DeviceUtils;
import io.github.cctyl.backup.utils.GsonUtils;
import io.github.cctyl.backup.utils.JsExecUtil;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WebAppInterface {

    private MainActivity context;
    private SharedPreferences sharedPreference;
    private JsExecUtil jsExecUtil;
    private List<DirectoryItem> rootChild;
    private DirectoryItem root;

    private BackupHistoryDao backupHistoryDao = AppApplication.getInstance().getApplicationDatabase().backupHistoryDao();

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


    public void sendFilesToWebView(List<DirectoryItem> list) {
        JSONArray fileArray = new JSONArray();
        // 添加文件列表
        for (DirectoryItem item : list) {
            fileArray.put(GsonUtils.toJsonObject(item));
        }

        // 将数据传递给WebView
        jsExecUtil.exec("receiveFileList", fileArray,null);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @JavascriptInterface
    public void openFolderPicker() {
        // 启动目录选择器
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        Uri rootUri = Uri.parse("content://com.android.externalstorage.documents/root/primary");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri);
        context.startActivityForResult(intent, 123);
    }

    @JavascriptInterface
    public String getBackupHistory() {
        List<BackupHistory> list = backupHistoryDao.findAll();

        Log.d("WebAppInterface", "getBackupHistory: list数量="+list.size());


        return GsonUtils.toJson(list);
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
        DirectoryItem root = new DirectoryItem("根目录", uri, rootDocId, 0, 0, true, rootDocId, "/");
        this.setRoot(root);
        jsExecUtil.exec("receiveRoot", GsonUtils.toJsonObject(root),null);

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
                String relativePath = childDocId.replaceAll(rootDocId, "");

                list.add(new DirectoryItem(name, treeUri, childDocId, size, lastModified, isDirectory, rootDocId, relativePath));

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
            this.openFolderPicker();
        } else {
            Uri u = Uri.parse(uri);
            Log.d("MainActivity", "onCreate: 复用 ");

            context.runOnUiThread(() -> {
                initRootFileList(u);
            });
        }
    }

    @JavascriptInterface
    public void intoChild(String targetDirJson) {

        Log.d("WebAppInterface", "intoChild: ");
        //转换 DirectoryItem
        DirectoryItem targetDir = GsonUtils.fromJson(targetDirJson, DirectoryItem.class);
        List<DirectoryItem> childrenByDocId = getChildrenByDocId(targetDir.getTreeUri(), targetDir.getDocId(), targetDir.getRootDocId());

        context.runOnUiThread(() -> {
            // 发送新目录的内容
            sendFilesToWebView(childrenByDocId);
        });

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
    public void intoParent(String parentDirJson) {


        Log.d("WebAppInterface", "intoParent: ");
        if (parentDirJson == null) {
            context.runOnUiThread(() -> {
                // 发送新目录的内容
                sendFilesToWebView(rootChild);
            });
            return;
        }
        //转换 DirectoryItem
        DirectoryItem parentDir = GsonUtils.fromJson(parentDirJson, DirectoryItem.class);

        Log.d("WebAppInterface", "intoParent: " + parentDir.getDocId());
        List<DirectoryItem> childrenByDocId = getChildrenByDocId(parentDir.getTreeUri(), parentDir.getDocId(), parentDir.getRootDocId());

        context.runOnUiThread(() -> {
            // 发送新目录的内容
            sendFilesToWebView(childrenByDocId);
        });

    }


}
