package io.github.cctyl.backup.utils;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class GsonUtils {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriTypeAdapter())
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


}
