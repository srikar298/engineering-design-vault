package idm;

public interface DownloadObserver {
    void onProgress(String jobId, long downloaded, long total, double percentage, DownloadStatus status);
}
