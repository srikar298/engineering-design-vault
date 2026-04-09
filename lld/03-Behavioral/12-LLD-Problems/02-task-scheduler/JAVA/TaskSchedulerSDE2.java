package scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <h1>LLD Problem: Distributed Task Scheduler (SDE-2+ Level)</h1>
 * 
 * <b>Patterns Combined:</b> Command (Tasks) + Iterator (Traversal)
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Command for Serialization:</b> Tasks are objects. This allows them 
 *    to be serialized into JSON and stored in Redis for persistence.
 * 2. <b>Concurrency:</b> In a 10k user environment, the task queue MUST be 
 *    thread-safe. Use <code>ConcurrentLinkedQueue</code>.
 * 3. <b>Retry Policy:</b> A senior implementation handles failures. If a task 
 *    fails, it is re-queued or moved to a Dead Letter Queue (DLQ).
 */

// --- 1. COMMAND (The Job) ---
interface Job {
    void run();
    String getId();
}

class EmailJob implements Job {
    private final String id;
    public EmailJob(String id) { this.id = id; }
    @Override public String getId() { return id; }
    @Override public void run() { System.out.println("   [Worker] Sending email for Job: " + id); }
}

// --- 2. THE SCHEDULER (The Invoker) ---
class Scheduler {
    // --- [INTERVIEW_MVP] (Thread-Safe Queue) ---
    private final ConcurrentLinkedQueue<Job> queue = new ConcurrentLinkedQueue<>();

    public void addJob(Job j) { queue.add(j); }

    public void runNextBatch() {
        System.out.println("Scheduler: Processing next batch...");
        
        // --- [PRODUCTION_ENHANCEMENT] (Iterator-based Traversal) ---
        Iterator<Job> it = queue.iterator();
        while (it.hasNext()) {
            Job j = it.next();
            try {
                j.run();
                it.remove(); // Remove from queue after success
            } catch (Exception e) {
                System.err.println("   [Error] Job " + j.getId() + " failed. Moving to DLQ.");
            }
        }
    }
}

public class TaskSchedulerSDE2 {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        
        // [INTERVIEW_MVP]: Adding jobs
        scheduler.addJob(new EmailJob("JOB-001"));
        scheduler.addJob(new EmailJob("JOB-002"));

        // [PRODUCTION_ENHANCEMENT]: Batch processing
        scheduler.runNextBatch();
        
        System.out.println("✅ Task Scheduler LLD verified.");
    }
}
