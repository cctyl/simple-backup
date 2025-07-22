package io.github.cctyl.backup;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import io.github.cctyl.backup.dao.ApplicationDatabase;

public class AppApplication extends Application {
    private static Context context;
    private ApplicationDatabase mApplicationDatabase;

    public static AppApplication getInstance() {
        return mApp;
    }

    private static AppApplication mApp;

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mApp = this;
        mApplicationDatabase = Room
                .databaseBuilder(this,
                        ApplicationDatabase.class,
                        "app_database" //数据库文件的名字，之前是在SQLiteOpenHelper的构造函数里面指定的
                )
                //允许迁移数据库，（如果不设置，那么数据库发生变更时，room就会默认删除原本数据库，再创建信数据库，那么原本的数据就会丢失）
                .addMigrations(

                )
                //运行在主线程中操作数据库（默认情况下room不能在主线程操作数据库，因为是耗时操作）
                .allowMainThreadQueries()
                .build()
        ;
    }


    public ApplicationDatabase getApplicationDatabase() {
        return mApplicationDatabase;
    }
}
