package io.github.cctyl.backup.utils.http;

import android.content.ContentResolver;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.github.cctyl.backup.AppApplication;
import io.github.cctyl.backup.entity.BackupFile;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private final BackupFile file;
    private final ProgressListener listener;

    public ProgressRequestBody(BackupFile file, ProgressListener listener) {
        this.file = file;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("multipart/form-data");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long total = file.getSize();
        long uploaded = 0;
        long lastUpdateTime = System.currentTimeMillis();
        long lastUploadSize = 0; // 新增：记录上次更新时的上传量
        byte[] buffer = new byte[8192];
        ContentResolver resolver = AppApplication.getContext().getContentResolver();

        try (InputStream is = resolver.openInputStream(
                DocumentsContract.buildDocumentUriUsingTree(file.getTreeUri(), file.getDocId())
        )) {
            int read;
            while ((read = is.read(buffer)) != -1) {
                Log.d("ProgressRequestBody", "writeTo: start write " +read);
                try {
                    sink.write(buffer, 0, read);
                } catch (IOException e) {
                    Log.e("ProgressRequestBody", "writeTo: ",e);
                    throw new RuntimeException(e);
                }
                uploaded += read;
                long currentTime = System.currentTimeMillis();
                long timeDelta = currentTime - lastUpdateTime;

                // 每100ms或上传8KB更新一次
                if (timeDelta >= 100 || uploaded == total) {
                    // 计算时间段内的总上传量和总时间
                    long deltaUpload = uploaded - lastUploadSize;
                    long speed = (long) (deltaUpload * 1000.0 / timeDelta); // 单位：B/s
                    listener.onProgressUpdate(uploaded, total, speed, timeDelta);
                    lastUpdateTime = currentTime;
                    lastUploadSize = uploaded; // 更新上次上传量
                }

                Log.d("ProgressRequestBody", "writeTo: "+read);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }catch (Exception e){
            Log.e("ProgressRequestBody", "writeTo: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


}

