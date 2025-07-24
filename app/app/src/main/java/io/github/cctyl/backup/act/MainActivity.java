package io.github.cctyl.backup.act;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

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

import com.king.camera.scan.CameraScan;

import io.github.cctyl.backup.AppApplication;
import io.github.cctyl.backup.R;
import io.github.cctyl.backup.dao.BackupHistoryDao;
import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.entity.DirectoryItem;
import io.github.cctyl.backup.utils.GsonUtils;
import io.github.cctyl.backup.utils.JsExecUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private WebAppInterface webAppInterface;
    SharedPreferences sharedPreference;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        webviewInit();
        testScan();

    }

    public static final int REQUEST_CODE_SCAN = 0x01;


    public void testScan(){
//        startScan(FullScreenQRCodeScanActivity.class);
    }
    public void testDb(){
//        BackupHistoryDao backupHistoryDao = AppApplication.getInstance().getApplicationDatabase().backupHistoryDao();
//        BackupHistory backupHistory = new BackupHistory();
//        backupHistory.setBackupResult("成功");
//        backupHistory.setSuccess(true);
//        backupHistory.setBackUpTime(LocalDateTime.now());
//        backupHistory.setBackUpNum(1L);
//        backupHistory.setBackUpCostTime(1);
//        backupHistory.setTotalFileSize(1);
//        backupHistoryDao.insertOne(backupHistory);
//
//        Log.d("MainActivity", "testDb: 插入成功");


//        List<BackupHistory> all = backupHistoryDao.findAll();
//        Log.d("MainActivity", "testDb: "+all);


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
                edit.putString("uri", uri.toString());
                edit.apply();
                webAppInterface.initRootFileList(uri);
            }
        }
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_SCAN) {
                String result = CameraScan.parseScanResult(data);
                //TODO 解析数据，存入数据库，并将数据传递给js
                webAppInterface.toast(result);
                Log.d("TAG", "onActivityResult: result="+result);
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack(); // 如果 WebView 可以返回上一页，则返回
        } else {
            super.onBackPressed(); // 否则按默认行为退出 Activity
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
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setOffscreenPreRaster(true); // 预渲染离屏内容（API 24+）



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
        sharedPreference = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        webAppInterface = new WebAppInterface(this, sharedPreference, new JsExecUtil(webView));
        // 添加 JS 接口，"Android" 是接口名，JS 中通过这个名称调用
        webView.addJavascriptInterface(webAppInterface, "Android");



        // 加载本地文件（确保文件在 assets 目录）
//        webView.loadUrl("file:///android_asset/index.html");


        webView.loadUrl("http://192.168.43.149:8080");
    }

}