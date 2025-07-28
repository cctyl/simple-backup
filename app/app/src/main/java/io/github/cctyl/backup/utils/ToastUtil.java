package io.github.cctyl.backup.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import io.github.cctyl.backup.AppApplication;

public class ToastUtil {

    public static void  toast(String desc){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppApplication.getContext(), desc, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static void  toastLong(String desc){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppApplication.getContext(), desc, Toast.LENGTH_LONG).show();
            }
        });
    }
}
