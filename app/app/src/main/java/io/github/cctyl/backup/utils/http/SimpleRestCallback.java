package io.github.cctyl.backup.utils.http;

import android.util.Log;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
public interface SimpleRestCallback extends Callback {
    String TAG = "SimpleRestCallback";
    @Override
    default void onResponse(Call call, Response response) throws IOException {
        getData(response.body().string());
    }
    @Override
    default void onFailure(Call call, IOException e) {
        Log.e(TAG, "error: ", e);
    }
    void getData(String data);
}