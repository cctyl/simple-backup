package io.github.cctyl.backup.act;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import io.github.cctyl.backup.R;
import io.github.cctyl.backup.entity.DirectoryItem;
import io.github.cctyl.backup.utils.JsExecUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private JsExecUtil jsExecUtil;
    private WebAppInterface webAppInterface;
    SharedPreferences sharedPreference;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webviewInit();
        jsExecUtil = new JsExecUtil(webView);
        // 加载本地文件（确保文件在 assets 目录）
        webView.loadUrl("file:///android_asset/index.html");


        sharedPreference = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁WebView
        if (webView != null) {
            webView.destroy();
            webView = null;
        }


    }

    List<DirectoryItem> rootChild;
    DirectoryItem root;

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                // 获取持久化权限
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                //2.获得SharedPreferences的编辑器
                SharedPreferences.Editor edit = sharedPreference.edit();
                edit.putString("uri",uri.toString());
                edit.apply();
                initRootFileList(uri);
            }
        }
    }

    private void initRootFileList(Uri uri) {
        String rootDocId = DocumentsContract.getTreeDocumentId(uri);
        rootChild = getChildrenByDocId(uri, rootDocId);
        root = new DirectoryItem("上级目录", uri, rootDocId, 0, 0, true);
        jsExecUtil.setData("root", DirectoryItem.toJson(root));
        sendFilesToWebView(rootChild);
    }


    // 修改 sendFilesToWebView 方法
    private void sendFilesToWebView(List<DirectoryItem> list) {
        JSONArray fileArray = new JSONArray();
        // 添加文件列表
        for (DirectoryItem item : list) {
            fileArray.put(DirectoryItem.toJson(item));
        }
        // 将数据传递给WebView
        jsExecUtil.setData("files", fileArray);

    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack(); // 如果 WebView 可以返回上一页，则返回
        } else {
            super.onBackPressed(); // 否则按默认行为退出 Activity
        }
    }


    private void printDirectoryTree(Uri treeUri) {
        String rootDocId = DocumentsContract.getTreeDocumentId(treeUri);

        Log.d("MainActivity", "printDirectoryTree: rootDocID " + rootDocId);
        printDirectoryLevel(treeUri, rootDocId, 0, 2);
    }

    private void printDirectoryLevel(Uri treeUri, String docId, int currentDepth, int maxDepth) {
        if (currentDepth > maxDepth) {
            return;
        }

        ContentResolver resolver = getContentResolver();
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId);

        Cursor cursor = resolver.query(
                childrenUri,
                new String[]{
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE
                },
                null, null, null
        );

        try {
            while (cursor != null && cursor.moveToNext()) {
                String childDocId = cursor.getString(0);

                Log.d("MainActivity", "printDirectoryLevel:childDocId:  " + childDocId);
                String name = cursor.getString(1);
                String mimeType = cursor.getString(2);

                // 打印当前项目（带缩进）
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < currentDepth; i++) {
                    prefix.append("    ");
                }
                Log.d("TREE", prefix.toString() + "├── " + name);

                // 如果是目录且未达到最大深度，递归打印子目录
                if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType) && currentDepth < maxDepth) {
                    printDirectoryLevel(treeUri, childDocId, currentDepth + 1, maxDepth);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @SuppressLint("Range")
    private List<DirectoryItem> getChildrenByDocId(Uri treeUri, String docId) {

        List<DirectoryItem> list = new ArrayList<>();
        ContentResolver resolver = getContentResolver();


        // 先查询当前 docId 的 mimeType，判断是否是目录
        Cursor selfCursor = resolver.query(
                DocumentsContract.buildDocumentUriUsingTree(treeUri, docId),
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

            Log.d("MainActivity", "getChildrenByDocId:  " + docId + " 不是目录");
            return list;
        }


        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId);

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

                list.add(new DirectoryItem(name, treeUri, childDocId, size, lastModified, isDirectory));

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
    public String getFileMD5(Uri fileUri) {
        ContentResolver resolver = getContentResolver();
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

    class WebAppInterface {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @JavascriptInterface
        public void openFolderPicker() {
            // 启动目录选择器
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            Uri rootUri = Uri.parse("content://com.android.externalstorage.documents/root/primary");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri);
            startActivityForResult(intent, 123);
        }


        @JavascriptInterface
        public void toast(String toast) {
            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void init()  {
            String uri = sharedPreference.getString("uri", null);
            if (uri == null){


                Log.d("MainActivity", "onCreate: 无数据，重新申请");
                webAppInterface.openFolderPicker();
            }else {
                Uri u = Uri.parse(uri);
                Log.d("MainActivity", "onCreate: 复用 ");

                runOnUiThread(() -> {
                    initRootFileList(u);
                });
            }
        }

        @JavascriptInterface
        public void intoChild(String targetDirJson) {

        Log.d("WebAppInterface", "intoChild: ");
            try {
                //转换 DirectoryItem
                DirectoryItem targetDir = DirectoryItem.fromJson(new JSONObject(targetDirJson));
                List<DirectoryItem> childrenByDocId = getChildrenByDocId(targetDir.getTreeUri(), targetDir.getDocId());

                runOnUiThread(() -> {
                    // 发送新目录的内容
                    sendFilesToWebView(childrenByDocId);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @JavascriptInterface
        public void getMd5(String targetDirJson) {

            Log.d("WebAppInterface", "getMd5: ");
            try {
                //转换 DirectoryItem
                DirectoryItem targetDir = DirectoryItem.fromJson(new JSONObject(targetDirJson));
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(targetDir.getTreeUri(), targetDir.getDocId());
                String fileMD5 = getFileMD5(uri);

                toast( targetDir.getName() +"的md5是："+fileMD5);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void intoParent(String parentDirJson) {

            
            Log.d("WebAppInterface", "intoParent: ");
            try {
                if (parentDirJson == null) {
                    runOnUiThread(() -> {
                        // 发送新目录的内容
                        sendFilesToWebView(rootChild);
                    });
                    return;
                }
                //转换 DirectoryItem
                DirectoryItem parentDir = DirectoryItem.fromJson(new JSONObject(parentDirJson));

                Log.d("WebAppInterface", "intoParent: " + parentDir.getDocId());
                List<DirectoryItem> childrenByDocId = getChildrenByDocId(parentDir.getTreeUri(), parentDir.getDocId());

                runOnUiThread(() -> {
                    // 发送新目录的内容
                    sendFilesToWebView(childrenByDocId);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void webviewInit() {
        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.web_view);
        // ===== 关键修复部分 =====
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // 允许文件协议访问
        settings.setAllowFileAccess(true);
        // 允许文件协议中的JS访问其他文件
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setSupportZoom(false);
        settings.setTextZoom(100);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);
        webView.clearHistory();


        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 添加 WebChromeClient 捕获 JS 错误
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("WebViewConsole",
                        consoleMessage.message() + " at " +
                                consoleMessage.sourceId() + ":" +
                                consoleMessage.lineNumber());
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("WebViewError", "Error: " + error.getDescription());
                Log.d("---->  MainActivity",
                        request.getUrl().toString()

                );
                Log.d("---->  MainActivity", String.valueOf(error.getErrorCode()));
            }

            //防止加载网页时调起系统浏览器
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 注入脚本
                //view.evaluateJavascript(
                //        "document.body.style.backgroundColor = 'red';" + // 临时设置背景色
                //                "console.log('Body dimensions:', document.body.clientWidth, document.body.clientHeight);",
                //        null
                //);
            }
        });
        webAppInterface = new WebAppInterface();
        // 添加 JS 接口，"Android" 是接口名，JS 中通过这个名称调用
        webView.addJavascriptInterface(webAppInterface, "Android");


    }

}