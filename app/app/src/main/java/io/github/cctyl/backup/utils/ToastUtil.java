package io.github.cctyl.backup.utils;

import android.content.Context;
import android.widget.Toast;


import io.github.cctyl.backup.AppApplication;

public class ToastUtil {

    public static void  toast(String desc){
        Toast.makeText(AppApplication.getContext(), desc, Toast.LENGTH_SHORT).show();
    }
    public static void  toastLong(String desc){
        Toast.makeText(AppApplication.getContext(), desc, Toast.LENGTH_LONG).show();
    }
}
