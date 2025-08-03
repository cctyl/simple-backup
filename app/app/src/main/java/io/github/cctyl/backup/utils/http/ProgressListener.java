package io.github.cctyl.backup.utils.http;

public interface ProgressListener {
    void onProgressUpdate(long uploaded, long total, long speed, long timeDelta) throws InterruptedException;
}