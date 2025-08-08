package io.github.cctyl.backup.act;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.king.camera.scan.CameraScan;

import io.github.cctyl.backup.AppApplication;
import io.github.cctyl.backup.R;
import io.github.cctyl.backup.dao.BackupFileDao;
import io.github.cctyl.backup.dao.BackupHistoryDao;
import io.github.cctyl.backup.entity.BackupFile;
import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.entity.ServerConfig;
import io.github.cctyl.backup.service.BackupService;
import io.github.cctyl.backup.utils.GsonUtils;
import io.github.cctyl.backup.utils.JsExecUtil;
import io.github.cctyl.backup.utils.ToastUtil;

import java.time.LocalDateTime;
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

        //沉浸式状态栏
        makeStatusBarTransparent();
        setLightStatusBarIcons();

        //webview初始化
        webviewInit();

        //绑定服务
        bindBackupService();
    }

    private static boolean isBind = false;
    private BackupService.LocalBinder binder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBind = true;
            binder = (BackupService.LocalBinder) service;
            binder.receiveWebInterface(webAppInterface);
            webAppInterface.setBinder(binder);
            Log.d("TestServiceActivity", "onServiceConnected: 链接了");
        }
    };

    public void bindBackupService() {
        Log.d("WebAppInterface", "bindBackupService: ");
        Intent bindIntent = new Intent(this, BackupService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
    }

    public void unbindBackupService() {
        if (isBind) {
            isBind = false;
            unbindService(connection);
        } else {
            Log.d("WebAppInterface", "unbindBackupService: 未绑定，不要重复解绑");
        }
    }

    /**
     * 状态栏透明
     */
    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        // 关键：设置系统UI可见性
        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(flags);

    }

    private void setLightStatusBarIcons() {

        // Android 11+ 新API设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        }
    }

    public static final int REQUEST_CODE_SCAN = 0x01;


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁WebView
        if (webView != null) {
            webView.destroy();
            webView = null;
        }

        //解绑服务
        unbindBackupService();


    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if ((requestCode == 123 || requestCode == 124 || requestCode == 125) && resultCode == RESULT_OK) {
            if (data != null) {
                //只保存但不发送数据
                Uri uri = data.getData();


                Log.d("MainActivity", "onActivityResult: uri=" + uri);
                // 获取持久化权限
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
                String uriString = uri.toString();
                if (!uriString.replaceAll("content://com.android.externalstorage.documents/tree/primary%3A", "")
                        .isEmpty()
                ) {
                    ToastUtil.toastLong("您选择的似乎不是根目录,如果存在问题可以重新选择");
                } else {
                    ToastUtil.toast("授权成功！");
                }
                //2.获得SharedPreferences的编辑器
                SharedPreferences.Editor edit = sharedPreference.edit();
                edit.putString("uri", uri.toString());
                edit.apply();
                //通知js


                if (requestCode == 123) {
                    //仅在未授权目录，但是已经进入文件夹详情界面时调用
                    //正常情况下，不应该被调用
                    webAppInterface.initRootFileList(uri);
                } else {
                    webAppInterface.getJsExecUtil().exec("checkPermission", null);

                    //初次授权跟目录，应该直接跳转到文件夹选择界面
                    if (requestCode == 124) {
                        webAppInterface.getJsExecUtil().exec("jumpSelect", null);
                    }
                }


            }
        }


        //扫码结果
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_SCAN) {
                String result = CameraScan.parseScanResult(data);


                if (result != null && !result.isEmpty()) {

                    Log.d("TAG", "onActivityResult: result=" + result);
                    try {
                        ServerConfig serverConfig = GsonUtils.fromJson(result, ServerConfig.class);
                        webAppInterface.toast("解析成功：" + result);
                        //2.获得SharedPreferences的编辑器
                        SharedPreferences.Editor edit = sharedPreference.edit();
                        edit.putString("addr", serverConfig.addr);
                        edit.putString("secret", serverConfig.secret);
                        edit.apply();

                        //通知js获取该值
                        webAppInterface.getJsExecUtil().exec("receiveServerConfig",
                                GsonUtils.toJsonObject(serverConfig),
                                null
                        );

                    } catch (Exception e) {

                        Log.e("TAG", "onActivityResult: ", e);
                        webAppInterface.toast("解析失败！");
                    }


                } else {
                    webAppInterface.toast("解析失败！");
                }
            }

        }
    }

    @Override
    public void onBackPressed() {

        Log.d("MainActivity", "onBackPressed: 按下了返回");
        webAppInterface.getJsExecUtil().exec("onAppBackPressed", null);
//        if (webView != null && webView.canGoBack()) {
//            webView.goBack(); // 如果 WebView 可以返回上一页，则返回
//        } else {
//            super.onBackPressed(); // 否则按默认行为退出 Activity
//        }
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


//        webView.loadUrl("http://192.168.31.151:8080");
        webView.loadUrl("http://192.168.43.149:8080");
    }

}