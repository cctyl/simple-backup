package io.github.cctyl.backup.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.utils.room.LocalDateTimeConverter;

@Database(
        entities = {BackupHistory.class}, version = 1,
        exportSchema = true
)
@TypeConverters({
        LocalDateTimeConverter.class,

})
public abstract class ApplicationDatabase extends RoomDatabase {

    public abstract BackupHistoryDao backupHistoryDao();
}
