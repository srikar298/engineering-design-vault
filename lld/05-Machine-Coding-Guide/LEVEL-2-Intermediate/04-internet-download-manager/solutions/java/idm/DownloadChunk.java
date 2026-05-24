package idm;

import java.util.concurrent.atomic.AtomicLong;

public class DownloadChunk implements Runnable {
    private final String chunkId;
    private final long startByte;
    private final long endByte;
    private final AtomicLong downloadedBytes = new AtomicLong(0);
    private final DownloadJob parentJob;
    private volatile DownloadStatus status = DownloadStatus.IDLE;

    public DownloadChunk(String chunkId, long startByte, long endByte, DownloadJob parentJob) {
        this.chunkId = chunkId;
        this.startByte = startByte;
        this.endByte = endByte;
        this.parentJob = parentJob;
    }

    public String getChunkId() {
        return chunkId;
    }

    public long getStartByte() {
        return startByte;
    }

    public long getEndByte() {
        return endByte;
    }

    public long getDownloadedBytes() {
        return downloadedBytes.get();
    }

    public long getChunkSize() {
        return (endByte - startByte) + 1;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    @Override
    public void run() {
        if (status == DownloadStatus.COMPLETED) {
            return;
        }

        status = DownloadStatus.DOWNLOADING;
        long totalChunkSize = getChunkSize();
        long currentProgress = downloadedBytes.get();
        long step = totalChunkSize / 10; // Download in 10 increments
        if (step <= 0) step = 1;

        System.out.printf("[Chunk-%s] Starting download range: %d-%d (Size: %d bytes)\n", 
            chunkId, startByte, endByte, totalChunkSize);

        try {
            while (currentProgress < totalChunkSize) {
                // Check pause signal
                if (parentJob.getStatus() == DownloadStatus.PAUSED) {
                    status = DownloadStatus.PAUSED;
                    System.out.printf("[Chunk-%s] Paused at %d bytes.\n", chunkId, currentProgress);
                    return;
                }

                // Simulate network fetch delay
                Thread.sleep(150);

                long nextIncrement = Math.min(step, totalChunkSize - currentProgress);
                downloadedBytes.addAndGet(nextIncrement);
                parentJob.addProgress(nextIncrement);
                currentProgress = downloadedBytes.get();
            }

            status = DownloadStatus.COMPLETED;
            System.out.printf("[Chunk-%s] Completed download.\n", chunkId);
            parentJob.onChunkCompleted();

        } catch (InterruptedException e) {
            status = DownloadStatus.FAILED;
            System.err.printf("[Chunk-%s] Download interrupted: %s\n", chunkId, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
