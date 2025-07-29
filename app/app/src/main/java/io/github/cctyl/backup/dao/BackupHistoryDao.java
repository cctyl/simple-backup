package io.github.cctyl.backup.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.cctyl.backup.entity.BackupFile;
import io.github.cctyl.backup.entity.BackupHistory;

@Dao
public interface BackupHistoryDao {

    @Query(" delete from backup_history ")
    int deleteAll();

    @Delete
    int delete(BackupHistory backupHistory);

    @Update
    int updateOne(BackupHistory user);

    @Insert
    Long insertOne(BackupHistory back);

    @Insert
    List<Long> insert(List<BackupHistory> back);

    @Query(" select * from backup_history order by backup_time desc limit 20 offset 0 ")
    List<BackupHistory> findAll();
}
