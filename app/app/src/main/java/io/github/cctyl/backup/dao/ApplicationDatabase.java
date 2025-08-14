package io.github.cctyl.backup.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import io.github.cctyl.backup.entity.BackupFile;
import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.entity.SelectDir;
import io.github.cctyl.backup.utils.room.LocalDateTimeConverter;
import io.github.cctyl.backup.utils.room.UrlConverter;

@Database(
        entities = {

                BackupHistory.class,
                SelectDir.class,
                BackupFile.class

        }, version = 3,
        exportSchema = true
)
@TypeConverters({
        LocalDateTimeConverter.class,
        UrlConverter.class
})
public abstract class ApplicationDatabase extends RoomDatabase {

    public abstract BackupHistoryDao backupHistoryDao();

    public abstract SelectDirDao selectDirDao();

    public abstract BackupFileDao backupFileDao();
}
