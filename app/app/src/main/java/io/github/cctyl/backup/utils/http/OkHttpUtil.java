package io.github.cctyl.backup.utils.http;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.github.cctyl.backup.dto.CompareDto;
import io.github.cctyl.backup.entity.BackupFile;
import io.github.cctyl.backup.entity.ServerConfig;
import io.github.cctyl.backup.utils.GsonUtils;
import io.github.cctyl.backup.utils.ToastUtil;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@RequiresApi(api = Build.VERSION_CODES.O)
public class OkHttpUtil {





    public static final OkHttpClient INSTANCE = new OkHttpClient();


    private static MediaType MEDIATYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * 构建器
     *
     * @param urlParam
     * @param requestParam
     * @param requestHeader
     * @return
     */
    private static Request.Builder builder(
            String urlParam,
            Map<String, ? extends Object> requestParam,
            Map<String, ? extends Object> requestHeader

    ) {
        Request.Builder builder = new Request.Builder();


        //1. url 与 query 参数
        if (requestParam != null && !requestParam.isEmpty()) {
            HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(urlParam)
                    .newBuilder();
            for (Map.Entry<String, ?> entry : requestParam.entrySet()) {
                httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
            }
            builder.url(httpUrlBuilder.build());
        } else {
            builder.url(urlParam);
        }

        //2.请求头
        if (requestHeader != null && !requestHeader.isEmpty()) {
            for (Map.Entry<String, ?> entry : requestHeader.entrySet()) {
                builder.header(entry.getKey(), entry.getValue().toString());
            }
        }


        return builder;

    }

    /**
     * get请求
     *
     * @param url
     * @param requestParam
     * @param requestHeader
     * @param callback
     */
    public static void get(
            String url,
            Map<String, ? extends Object> requestParam,
            Map<String, ? extends Object> requestHeader,
            SimpleRestCallback callback
    ) {
        INSTANCE.newCall(
                        builder(url, requestParam, requestHeader)
                                .get()
                                .build()
                )
                .enqueue(callback);
    }

    /**
     * post请求
     *
     * @param url
     * @param requestParam
     * @param requestHeader
     * @param callback
     */

    public static void post(
            String url,
            Map<String, ? extends Object> requestParam,
            Map<String, ? extends Object> requestHeader,
            String requestBody,
            SimpleRestCallback callback
    ) {
        INSTANCE.newCall(
                        builder(url, requestParam, requestHeader)
                                .post(RequestBody.create(MEDIATYPE, GsonUtils.INSTANCE.toJson(requestBody)))
                                .build()
                )
                .enqueue(callback);
    }

    public static void uploadFile(ServerConfig mServerConfig, BackupFile file, ProgressListener listener) {

        //从配置文件中读取
        String token = "Bearer " + mServerConfig.secret;
        String serverAddr = mServerConfig.addr;

        ProgressRequestBody progressBody = new ProgressRequestBody(file,listener);

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", file.getName())
                .addFormDataPart("treeUri", file.getTreeUri().toString())
                .addFormDataPart("docId", file.getDocId())
                .addFormDataPart("relativePath", file.getRelativePath())
                .addFormDataPart("isDirectory", String.valueOf(file.isDirectory()))
                .addFormDataPart("md5", file.getMd5())
                .addFormDataPart("file", file.getName(), progressBody)
                .build();


        Log.d("LocalBinder", "uploadFile: body= "+body);

        Request request = new Request.Builder()
                .url("http://"+serverAddr+"/api/file/upload")
                .header("authorization",token)
                .post(body)
                .build();

        try {
            String bodyString = INSTANCE.newCall(request)
                    .execute().body().string();
            Log.d("LocalBinder", "uploadFile: 上传的响应="+bodyString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static List<BackupFile>  compare( List<BackupFile> list,ServerConfig mServerConfig) throws InterruptedException {
        //从配置文件中读取
        String token = "Bearer " + mServerConfig.secret;
        String serverAddr = mServerConfig.addr;

        Request request = new Request.Builder()
                .url("http://"+serverAddr+"/api/file/compare")
                .header("authorization",token)
                .post(RequestBody.create(MEDIATYPE, GsonUtils.INSTANCE.toJson(list)))
                .build();

        try {
            String bodyString = INSTANCE.newCall(request)
                    .execute().body().string();
            Log.d("LocalBinder", "uploadFile: 上传的响应="+bodyString);
            CompareDto compareDto = GsonUtils.fromJson(bodyString, CompareDto.class);
            if (compareDto.getStatus()==200){
                return compareDto.getData();
            }else {

                Log.e("OkHttpUtil", "compare: 请求错误："+compareDto.getMessage());
                ToastUtil.toastLong("请求服务端错误："+compareDto.getMessage());
                throw new InterruptedException();
            }

        } catch (Exception e) {
            throw new InterruptedException();
        }

    }
}