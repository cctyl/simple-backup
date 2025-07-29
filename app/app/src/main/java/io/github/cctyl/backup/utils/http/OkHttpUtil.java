package io.github.cctyl.backup.utils.http;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.util.Map;

import io.github.cctyl.backup.utils.GsonUtils;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
@RequiresApi(api = Build.VERSION_CODES.O)
public class OkHttpUtil {

    private static OkHttpClient client = new OkHttpClient();


    private static MediaType MEDIATYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * 构建器
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
        if (requestParam != null && requestParam.size() > 0) {
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
        if (requestHeader != null && requestHeader.size() > 0) {
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
        client.newCall(
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
        client.newCall(
                        builder(url, requestParam, requestHeader)
                                .post(RequestBody.create(MEDIATYPE, GsonUtils.INSTANCE.toJson(requestBody)))
                                .build()
                )
                .enqueue(callback);
    }



}