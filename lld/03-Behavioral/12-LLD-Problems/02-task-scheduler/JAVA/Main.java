import engine.Scheduler;
import iterator.JobQueue;
import job.BackupJob;
import job.ReportJob;

/**
 * <h1>Combined Behavioral Patterns Demonstration</h1>
 * 
 * <p>Demonstrates:
 * 1. Command (Jobs as Objects)
 * 2. Iterator (Queue traversal)
 * 3. Template Method (Standardized Job Execution Pipeline)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Task Scheduler: Command + Template + Iterator  ");
        System.out.println("==================================================\n");

        JobQueue queue = new JobQueue();

        // Adding jobs out of order
        queue.addJob(new ReportJob(1));      // Low Priority
        queue.addJob(new BackupJob(10));     // High Priority
        queue.addJob(new ReportJob(5));      // Medium Priority

        Scheduler engine = new Scheduler();
        engine.runAllPendingJobs(queue);
    }
}
