package io.github.cctyl.backup.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.github.cctyl.backup.entity.BackupHistory;

@Dao
public interface BackupHistoryDao {


    @Insert
    Long insertOne(BackupHistory back);

    @Query(" select * from backup_history order by backup_time desc limit 20 offset 0 ")
    List<BackupHistory> findAll();
}
