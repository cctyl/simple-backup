package io.github.cctyl.backup.dto;

import java.time.LocalDateTime;

public class ProgressDto {

    int totalPercent;
    long alreadyUploadFileSize;
    int needUploadFileNum;
    CurrentFile currentFile = new CurrentFile();
    int speed;
    LocalDateTime startTime;
    int alreadyUploadFileNum;

    /**
     * 上传失败数量
     */
    int failNum;




    /**
     * 检查状态
     * 0 检查完毕
     * 1 本地检查
     * 2 比较服务器信息
     */
    int checkState;


    public int getFailNum() {
        return failNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public int getCheckState() {
        return checkState;
    }

    public void setCheckState(int checkState) {
        this.checkState = checkState;
    }

    public int getTotalPercent() {
        return totalPercent;
    }

    public void setTotalPercent(int totalPercent) {
        this.totalPercent = totalPercent;
    }

    public long getAlreadyUploadFileSize() {
        return alreadyUploadFileSize;
    }

    public void setAlreadyUploadFileSize(long alreadyUploadFileSize) {
        this.alreadyUploadFileSize = alreadyUploadFileSize;
    }

    public CurrentFile getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(CurrentFile currentFile) {
        this.currentFile = currentFile;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getAlreadyUploadFileNum() {
        return alreadyUploadFileNum;
    }

    public void setAlreadyUploadFileNum(int alreadyUploadFileNum) {
        this.alreadyUploadFileNum = alreadyUploadFileNum;
    }


    public int getNeedUploadFileNum() {
        return needUploadFileNum;
    }

    public void setNeedUploadFileNum(int needUploadFileNum) {
        this.needUploadFileNum = needUploadFileNum;
    }

    public static class CurrentFile {
        String mimeType;
        String name;
        String relativePath;
        int percent;


        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public void setRelativePath(String relativePath) {
            this.relativePath = relativePath;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }
    }
}
