package io.github.cctyl.backup.utils;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import io.github.cctyl.backup.utils.gson.LocalDateTimeAdapter;
import io.github.cctyl.backup.utils.gson.UriTypeAdapter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GsonUtils {

    public static final Gson INSTANCE = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create()
            ;



    public static String toJson(Object o){

        return INSTANCE.toJson(o);
    }

    public static<T> T fromJson(String json,Class<T> clazz){

        return INSTANCE.fromJson(json,clazz);
    }

    public static <T> List<T> fromJsonArr(String json,Class<T> clazz){
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return INSTANCE.fromJson(json, type);
    }

    public static JSONObject toJsonObject(Object o){
        try {
            JSONObject jsonObject = new JSONObject(INSTANCE.toJson(o));

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
