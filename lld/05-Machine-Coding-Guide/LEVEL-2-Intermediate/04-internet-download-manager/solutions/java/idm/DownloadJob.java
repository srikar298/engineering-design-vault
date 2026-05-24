package idm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadJob implements Comparable<DownloadJob> {
    private final String jobId;
    private final String fileUrl;
    private final long totalSize;
    private final int priority; // Higher means more urgent (SDE-2 Queue requirement)
    
    private volatile DownloadStatus status = DownloadStatus.IDLE;
    private final AtomicLong totalDownloaded = new AtomicLong(0);
    private final List<DownloadChunk> chunks = new ArrayList<>();
    private final List<DownloadObserver> observers = new CopyOnWriteArrayList<>();

    public DownloadJob(String jobId, String fileUrl, long totalSize, int numParts, int priority) {
        this.jobId = jobId;
        this.fileUrl = fileUrl;
        this.totalSize = totalSize;
        this.priority = priority;
        initializeChunks(numParts);
    }

    private void initializeChunks(int numParts) {
        long chunkSize = totalSize / numParts;
        for (int i = 0; i < numParts; i++) {
            long start = i * chunkSize;
            long end = (i == numParts - 1) ? totalSize - 1 : (start + chunkSize) - 1;
            chunks.add(new DownloadChunk(jobId + "-P" + i, start, end, this));
        }
    }

    public String getJobId() { return jobId; }
    public String getFileUrl() { return fileUrl; }
    public long getTotalSize() { return totalSize; }
    public int getPriority() { return priority; }
    public DownloadStatus getStatus() { return status; }
    public List<DownloadChunk> getChunks() { return chunks; }
    public long getTotalDownloaded() { return totalDownloaded.get(); }

    public void addObserver(DownloadObserver obs) {
        observers.add(obs);
    }

    public void removeObserver(DownloadObserver obs) {
        observers.remove(obs);
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
        notifyObservers();
    }

    public void addProgress(long bytes) {
        totalDownloaded.addAndGet(bytes);
        notifyObservers();
    }

    public synchronized void onChunkCompleted() {
        boolean allDone = true;
        for (DownloadChunk chunk : chunks) {
            if (chunk.getStatus() != DownloadStatus.COMPLETED) {
                allDone = false;
                break;
            }
        }
        if (allDone) {
            this.status = DownloadStatus.COMPLETED;
            System.out.printf("[Job-%s] All chunks finished. Assembling/merging file...\n", jobId);
            notifyObservers();
        }
    }

    private void notifyObservers() {
        double percentage = (double) totalDownloaded.get() / totalSize * 100.0;
        if (percentage > 100.0) percentage = 100.0;
        for (DownloadObserver obs : observers) {
            obs.onProgress(jobId, totalDownloaded.get(), totalSize, percentage, status);
        }
    }

    @Override
    public int compareTo(DownloadJob other) {
        // High priority first. If same, compare ID
        if (this.priority != other.priority) {
            return Integer.compare(other.priority, this.priority);
        }
        return this.jobId.compareTo(other.jobId);
    }
}
