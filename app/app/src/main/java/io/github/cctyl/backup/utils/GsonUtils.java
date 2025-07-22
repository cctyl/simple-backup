package io.github.cctyl.backup.utils;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Collection;

import io.github.cctyl.backup.utils.gson.LocalDateTimeAdapter;
import io.github.cctyl.backup.utils.gson.UriTypeAdapter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GsonUtils {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create()
            ;



    public static String toJson(Object o){

        return gson.toJson(o);
    }

    public static<T> T fromJson(String json,Class<T> clazz){
        return gson.fromJson(json,clazz);
    }

    public static JSONObject toJsonObject(Object o){
        try {
            JSONObject jsonObject = new JSONObject(gson.toJson(o));

            return jsonObject;
        } catch (JSONException e) {

            throw new RuntimeException(e);
        }
    }


    public static<T> JSONArray toJsonArray(Collection<T> col){
        JSONArray jsonArray = new JSONArray();
        for (T item : col) {
            jsonArray.put(GsonUtils.toJsonObject(item));
        }
        return jsonArray;
    }

}
