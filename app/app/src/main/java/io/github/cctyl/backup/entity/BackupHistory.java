package io.github.cctyl.backup.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "backup_history")
public class BackupHistory {



    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * 备份结果
     */
    @ColumnInfo(name = "backup_result")
    private String backupResult;


    @ColumnInfo(name = "backup_detail")
    private String backupDetail;

    /**
     * 是否成功
     */
    private Boolean success;


    /**
     * 备份时间，开始时间
     */
    @ColumnInfo(name = "backup_time")
    private LocalDateTime backUpTime;


    /**
     * 备份成功的文件数
     */
    @ColumnInfo(name = "backup_num")
    private Long backUpNum;

    /**
     * 备份花费的时间，秒
     */
    @ColumnInfo(name = "back_up_cost_time")
    private Integer backUpCostTime;


    /**
     * 总文件大小,MB
     */
    @ColumnInfo(name = "total_file_size")
    private Integer totalFileSize;


    /**
     * 备份的路径，逗号隔开
     */
    @ColumnInfo(name = "back_up_path_arr")
    private String backUpPathArr;

    @ColumnInfo(name = "task_finish")
    private Boolean taskFinish;


    public Boolean getTaskFinish() {
        return taskFinish;
    }

    public void setTaskFinish(Boolean taskFinish) {
        this.taskFinish = taskFinish;
    }

    public BackupHistory() {
    }


    public String getBackupDetail() {
        return backupDetail;
    }

    public void setBackupDetail(String backupDetail) {
        this.backupDetail = backupDetail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackupResult() {
        return backupResult;
    }

    public void setBackupResult(String backupResult) {
        this.backupResult = backupResult;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LocalDateTime getBackUpTime() {
        return backUpTime;
    }

    public void setBackUpTime(LocalDateTime backUpTime) {
        this.backUpTime = backUpTime;
    }

    public Long getBackUpNum() {
        return backUpNum;
    }

    public void setBackUpNum(Long backUpNum) {
        this.backUpNum = backUpNum;
    }

    public Integer getBackUpCostTime() {
        return backUpCostTime;
    }

    public void setBackUpCostTime(Integer backUpCostTime) {
        this.backUpCostTime = backUpCostTime;
    }

    public Integer getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(Integer totalFileSize) {
        this.totalFileSize = totalFileSize;
    }


    public String getBackUpPathArr() {
        return backUpPathArr;
    }

    public void setBackUpPathArr(String backUpPathArr) {
        this.backUpPathArr = backUpPathArr;
    }
}
