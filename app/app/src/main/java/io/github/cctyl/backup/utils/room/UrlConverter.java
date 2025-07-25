package io.github.cctyl.backup.utils.room;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

public class UrlConverter {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static String fromUri(Uri value) {
        return value == null ? null : value.toString();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static Uri toUri(String value) {
        return value == null ? null : Uri.parse(value);
    }
}
