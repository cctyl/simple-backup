package com.example.myapplication.act;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.utils.JsExecUtil;
import com.example.myapplication.utils.JsResultParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private JsExecUtil jsExecUtil;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webviewInit();
        jsExecUtil = new JsExecUtil(webView);
        // 加载本地文件（确保文件在 assets 目录）
        webView.loadUrl("file:///android_asset/index2.html");
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
            }

            //防止加载网页时调起系统浏览器
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        // 添加 JS 接口，"Android" 是接口名，JS 中通过这个名称调用
        webView.addJavascriptInterface(new WebAppInterface(), "Android");


    }


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

                // 处理选择的文件/目录
                Log.d("onActivityResult: ", uri.toString());
                sendFilesToWebView(uri);
            }
        }
    }

    public String getUserFriendlyPath(Context context, Uri treeUri) {
        String baseName = "External Storage";

        // Android 10+ 获取卷名称
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            StorageVolume volume = sm.getStorageVolume(treeUri);
            if (volume != null) baseName = volume.getDescription(context);
        }

        // 解析路径段（示例：primary:Android/data → Android/data）
        String documentId = DocumentsContract.getTreeDocumentId(treeUri);
        String subPath = documentId.contains(":")
                ? documentId.split(":")[1]
                : documentId;

        return baseName + File.separator + subPath;
    }

    // 在获取文件列表后，将数据转换为JSON格式传递给WebView
    private void sendFilesToWebView(Uri folderUri) {
        DocumentFile file = DocumentFile.fromTreeUri(this, folderUri);
        JSONArray fileArray = new JSONArray();

        JSONObject fileObj = new JSONObject();
        try {
            fileObj.put("name", getUserFriendlyPath(this, folderUri));
            fileObj.put("uri", file.getUri().toString());
            fileObj.put("type", file.getType());
            fileObj.put("size", file.length());
            fileObj.put("lastModified", file.lastModified());
            fileArray.put(fileObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 将数据传递给WebView
        String jsonData = fileArray.toString();
        Log.d("sendFilesToWebView", jsonData);
        jsExecUtil.setData("files",fileArray);


    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack(); // 如果 WebView 可以返回上一页，则返回
        } else {
            super.onBackPressed(); // 否则按默认行为退出 Activity
        }
    }


    class WebAppInterface {
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
        public void userClick(String jsonStr) throws JSONException {
            Log.d("userClick", "userClick: " + jsonStr);
            JSONArray jsonArray = new JSONArray(jsonStr);
        }

    }
}