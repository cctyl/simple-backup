package com.example.myapplication.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LocalHttpServer extends NanoHTTPD {

    private final Context context;
    private final Map<String, String> mimeTypes = new HashMap<>();

    public LocalHttpServer(int port, Context context) {
        super(port);
        this.context = context;

        // 初始化MIME类型映射
        mimeTypes.put("html", "text/html");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("ico", "image/x-icon");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("woff", "font/woff");
        mimeTypes.put("woff2", "font/woff2");
        mimeTypes.put("ttf", "font/ttf");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        // 处理根路径请求
        if ("/".equals(uri)) {
            uri = "/index.html";
        }

        // 去掉开头的斜杠
        String assetPath = uri.startsWith("/") ? uri.substring(1) : uri;

        try {
            // 获取AssetManager实例
            AssetManager assets = context.getAssets();

            // 检查文件是否存在
            try {
                InputStream test = assets.open(assetPath);
                test.close();
            } catch (IOException e) {
                return notFoundResponse("File not found: " + assetPath);
            }

            // 获取文件扩展名以确定MIME类型
            String extension = assetPath.contains(".") ?
                    assetPath.substring(assetPath.lastIndexOf('.') + 1) : "";
            String mimeType = mimeTypes.getOrDefault(extension.toLowerCase(), "text/plain");

            // 处理特殊文件类型
            if ("ttf".equalsIgnoreCase(extension) || "woff".equalsIgnoreCase(extension)) {
                return newFixedLengthResponse(
                        Response.Status.OK,
                        mimeType,
                        assets.open(assetPath),
                        assets.open(assetPath).available()
                );
            }

            // 返回文件内容
            return newChunkedResponse(Response.Status.OK, mimeType, assets.open(assetPath));

        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain",
                    "Internal Server Error: " + e.getMessage());
        }
    }

    private Response notFoundResponse(String message) {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html",
                "<html><body><h1>404 Not Found</h1><p>" + message + "</p></body></html>");
    }
}