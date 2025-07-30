package io.github.cctyl.backup.dto;

import java.util.List;

import io.github.cctyl.backup.entity.BackupFile;

public class CompareDto {

    private int status;
    private String message;


    private List<BackupFile> data;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BackupFile> getData() {
        return data;
    }

    public void setData(List<BackupFile> data) {
        this.data = data;
    }
}
