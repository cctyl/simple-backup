package io.github.cctyl.backup.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.github.cctyl.backup.entity.BackupFile;
import io.github.cctyl.backup.entity.SelectDir;

@Dao
public interface BackupFileDao {


    @Insert
    Long insertOne(BackupFile selectDir);

    @Insert
    List<Long> insert(List<BackupFile>  selectDirs);

    @Query(" delete from backup_file ")
    int deleteAll();

    @Query(" delete from backup_file where id in (:ids) ")
    int deleteByIdIn( Collection<Long> ids);

    @Query(" delete from backup_file where id = :id ")
    int deleteById(long id);

    @Query(" select * from backup_file order by id desc ")
    List<BackupFile> findAll();

    @Query(" select * from backup_file where relative_path = :relativePath limit 1 ")
    BackupFile findByRelativePath(String relativePath);

    @Update
    int updateOne(BackupFile user);

    @Query(" select * from backup_file where id = :id ")
    BackupFile findById(Long id);

    @Query(" select * from backup_file where id in (:updatedIds) ")
    List<BackupFile> findByIdIn(Collection<Long> updatedIds);
}
