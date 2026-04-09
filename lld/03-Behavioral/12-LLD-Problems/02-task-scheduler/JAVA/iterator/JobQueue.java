package iterator;

import job.BaseJob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <h1>The Iterable Collection</h1>
 * 
 * <p>Holds the actual jobs. Automatically sorts them by priority 
 * so the Iterator hands them out in the correct queue order.
 */
public class JobQueue {
    private final List<BaseJob> queue = new ArrayList<>();

    public void addJob(BaseJob job) {
        queue.add(job);
        // Extremely simple sort: Highest priority First
        queue.sort(Comparator.comparingInt(BaseJob::getPriority).reversed());
        System.out.println("   [Queue] Added " + job.getClass().getSimpleName() + " with priority " + job.getPriority());
    }

    // Factory Method for the Iterator
    public IJobIterator createIterator() {
        return new PriorityJobIterator(queue);
    }
}
