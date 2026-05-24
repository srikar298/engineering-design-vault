import idm.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("📥 Enterprise Internet Download Manager (IDM) Demo 📥");
        System.out.println("=================================================\n");

        // 1. Get Manager and start
        DownloadManager manager = DownloadManager.getInstance();
        manager.start();

        // 2. Setup Progress Observer
        DownloadObserver observer = (jobId, downloaded, total, percentage, status) -> {
            System.out.printf("[Observer] Job: %s | Downloaded: %d/%d bytes (%.1f%%) | Status: %s\n", 
                jobId, downloaded, total, percentage, status);
        };

        // 3. Create and register Jobs
        // Job-1: Large file, priority 5
        DownloadJob job1 = new DownloadJob("JOB-1", "http://example.com/large_video.mp4", 10000, 4, 5);
        job1.addObserver(observer);

        // Job-2: Critical file, priority 10 (Higher priority)
        DownloadJob job2 = new DownloadJob("JOB-2", "http://example.com/system_update.bin", 6000, 3, 10);
        job2.addObserver(observer);

        // Submit jobs
        // We will submit them in order. Since manager takes from Priority Queue,
        // it will prioritize the job with higher priority score (JOB-2).
        manager.submitJob(job1);
        manager.submitJob(job2);

        // Let them start downloading
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 4. Test Pause & Resume on JOB-2 (which runs first due to priority)
        System.out.println("\n--- ⏸️ Pausing JOB-2 ---");
        manager.pauseJob("JOB-2");

        try {
            Thread.sleep(1000); // Sleep while JOB-2 is paused (JOB-1 continues if running)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- ▶️ Resuming JOB-2 ---");
        manager.resumeJob("JOB-2");

        // Let simulation run for 5 seconds to finish
        try {
            System.out.println("\n--- Waiting for downloads to complete ---");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 5. Cleanup
        System.out.println();
        manager.stop();
        System.exit(0);
    }
}
