package idm;

import java.util.concurrent.*;

public class DownloadManager {
    private static volatile DownloadManager instance = null;

    private final PriorityBlockingQueue<DownloadJob> queue = new PriorityBlockingQueue<>();
    private final ConcurrentHashMap<String, DownloadJob> jobRegistry = new ConcurrentHashMap<>();
    private final ExecutorService chunkExecutor = Executors.newFixedThreadPool(8);
    private Thread managerThread;
    private volatile boolean isRunning = false;

    private DownloadManager() {}

    public static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    public void submitJob(DownloadJob job) {
        jobRegistry.put(job.getJobId(), job);
        queue.offer(job);
        System.out.printf("[Manager] Submitted Job %s (Priority: %d, Size: %d bytes)\n", 
            job.getJobId(), job.getPriority(), job.getTotalSize());
    }

    public void pauseJob(String jobId) {
        DownloadJob job = jobRegistry.get(jobId);
        if (job != null && job.getStatus() == DownloadStatus.DOWNLOADING) {
            job.setStatus(DownloadStatus.PAUSED);
            System.out.printf("[Manager] Sent PAUSE command to Job: %s\n", jobId);
        }
    }

    public void resumeJob(String jobId) {
        DownloadJob job = jobRegistry.get(jobId);
        if (job != null && job.getStatus() == DownloadStatus.PAUSED) {
            System.out.printf("[Manager] Resuming Job: %s\n", jobId);
            job.setStatus(DownloadStatus.DOWNLOADING);
            
            // Resubmit all chunks that are not completed yet
            for (DownloadChunk chunk : job.getChunks()) {
                if (chunk.getStatus() != DownloadStatus.COMPLETED) {
                    chunkExecutor.submit(chunk);
                }
            }
        }
    }

    public synchronized void start() {
        if (isRunning) return;
        isRunning = true;
        
        managerThread = new Thread(() -> {
            while (isRunning) {
                try {
                    DownloadJob job = queue.take(); // Blocks until a job is submitted
                    if (job.getStatus() == DownloadStatus.PAUSED) {
                        // Put back if paused before starting
                        queue.offer(job);
                        Thread.sleep(100);
                        continue;
                    }
                    
                    job.setStatus(DownloadStatus.DOWNLOADING);
                    System.out.printf("[Manager] Starting Download Job: %s\n", job.getJobId());
                    
                    // Submit each chunk to the executor
                    for (DownloadChunk chunk : job.getChunks()) {
                        chunkExecutor.submit(chunk);
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        managerThread.setName("IDM-Manager");
        managerThread.start();
        System.out.println("[Manager] Download Manager started.");
    }

    public synchronized void stop() {
        if (!isRunning) return;
        isRunning = false;
        if (managerThread != null) {
            managerThread.interrupt();
        }
        chunkExecutor.shutdown();
        try {
            if (!chunkExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                chunkExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            chunkExecutor.shutdownNow();
        }
        System.out.println("[Manager] Download Manager stopped.");
    }
}
