package engine;

import iterator.IJobIterator;
import iterator.JobQueue;
import job.BaseJob;

/**
 * <h1>The Scheduler Engine (The Invoker)</h1>
 * 
 * <p>Utilizes an Iterator to pull jobs from a Queue, and then executes them 
 * via the Command Pattern mechanism.
 */
public class Scheduler {
    
    public void runAllPendingJobs(JobQueue queue) {
        System.out.println("\n--- ⚙️ Scheduler Booting Up... ---");
        
        IJobIterator iterator = queue.createIterator();
        
        while (iterator.hasNext()) {
            BaseJob activeJob = iterator.getNext();
            
            // Invokes the Template Method on the Command!
            activeJob.execute(); 
        }

        System.out.println("\n--- 🛑 Scheduler Finished: Queue is Empty ---");
    }
}
