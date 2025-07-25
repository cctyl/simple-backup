package io.github.cctyl.backup.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.github.cctyl.backup.entity.BackupHistory;
import io.github.cctyl.backup.entity.SelectDir;

@Dao
public interface SelectDirDao {


    @Insert
    Long insertOne(SelectDir selectDir);

    @Insert
    List<Long> insert(List<SelectDir>  selectDirs);

    @Query(" delete from select_dir ")
    int deleteAll();

    @Query(" select * from select_dir order by id desc ")
    List<SelectDir> findAll();
}
