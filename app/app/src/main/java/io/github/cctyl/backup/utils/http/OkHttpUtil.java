package io.github.cctyl.backup.utils.http;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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


    public static final OkHttpClient INSTANCE = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();


    public static final OkHttpClient INSTANCE_UPLOAD = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();


    private static MediaType MEDIATYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * 构建器
     *
     * @return
     */
    private static Request.Builder builder(
            String path,
            String serverAddr,
            String token

    ) {
        Request.Builder builder = new Request.Builder();
        builder.url("http://" + serverAddr + path)
                .header("authorization", token)

        ;
        return builder;
    }

    /**
     * get请求
     *
     * @param url
     * @param callback
     */
    public static void get(
            String url,
            String serverAddr,
            String token,
            SimpleRestCallback callback
    ) {
        INSTANCE.newCall(
                        builder(url, serverAddr, token)
                                .get()
                                .build()
                )
                .enqueue(callback);
    }

    /**
     * post请求
     *
     * @param url
     * @param callback
     */

    public static void post(
            String url,
            String serverAddr,
            String token,
            String requestBody,
            SimpleRestCallback callback
    ) {
        INSTANCE.newCall(
                        builder(url, serverAddr, token)
                                .post(RequestBody.create(MEDIATYPE, GsonUtils.INSTANCE.toJson(requestBody)))
                                .build()
                )
                .enqueue(callback);
    }


    public static void uploadFile(ServerConfig mServerConfig,
                                  BackupFile file,
                                  boolean checkMd5,
                                  ProgressListener listener,
                                  Consumer<JsonObject> consumer


    ) {

        //从配置文件中读取
        String token = "Bearer " + mServerConfig.secret;
        String serverAddr = mServerConfig.addr;

        ProgressRequestBody progressBody = new ProgressRequestBody(file, listener);

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //务必注意字段的先后顺序
                .addFormDataPart("name", file.getName())
                .addFormDataPart("checkMd5", String.valueOf(checkMd5))
                .addFormDataPart("treeUri", file.getTreeUri().toString())
                .addFormDataPart("docId", file.getDocId())
                .addFormDataPart("relativePath", file.getRelativePath())
                .addFormDataPart("isDirectory", String.valueOf(file.isDirectory()))
                .addFormDataPart("md5", file.getMd5())
                .addFormDataPart("createTime", String.valueOf(file.getLastModified()))
                .addFormDataPart("file", file.getName(), progressBody)
                .build();


        Log.d("OkHttpUtil", "uploadFile:  "+file.getName() +"最后修改时间是："+file.getLastModified());

        Log.d("LocalBinder", "uploadFile: body= " + body);

        Request request = new Request.Builder()
                .url("http://" + serverAddr + "/api/file/upload")
                .header("authorization", token)
                .post(body)
                .build();

        try {
            String bodyString = INSTANCE_UPLOAD.newCall(request)
                    .execute().body().string();

            JsonObject jsonObject = JsonParser.parseString(bodyString).getAsJsonObject();
            consumer.accept(jsonObject);
            Log.d("LocalBinder", "uploadFile: 上传的响应=" + bodyString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static List<BackupFile> compare(List<BackupFile> list, ServerConfig mServerConfig) throws InterruptedException {
        //从配置文件中读取
        String token = "Bearer " + mServerConfig.secret;
        String serverAddr = mServerConfig.addr;
        String json = GsonUtils.INSTANCE.toJson(list);
        Request request = new Request.Builder()
                .url("http://" + serverAddr + "/api/file/compare")
                .header("authorization", token)
                .post(RequestBody.create(MEDIATYPE, json))
                .build();

        System.out.println(json);
        try {
            String bodyString = INSTANCE.newCall(request)
                    .execute().body().string();
            Log.d("LocalBinder", "compare: 上传的响应=" + bodyString);
            CompareDto compareDto = GsonUtils.fromJson(bodyString, CompareDto.class);
            if (compareDto.getStatus() == 200) {
                return compareDto.getData();
            } else {

                Log.e("OkHttpUtil", "compare: 请求错误：" + compareDto.getMessage());
                ToastUtil.toastLong("请求服务端错误：" + compareDto.getMessage());
                throw new RuntimeException("请求服务端错误");
            }

        } catch (Exception e) {
            Log.e("OkHttpUtil", "compare: " + e.getMessage(), e);
            ToastUtil.toastLong("文件上传失败！请检查配置和网络");
            throw new RuntimeException(e);
        }

    }
}