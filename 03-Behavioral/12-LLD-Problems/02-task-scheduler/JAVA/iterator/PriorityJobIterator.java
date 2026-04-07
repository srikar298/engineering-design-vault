package iterator;

import job.BaseJob;
import java.util.List;

/**
 * <h1>The Concrete Iterator</h1>
 * 
 * <p>Iterates over the List of jobs. Notice this DOES NOT sort them itself.
 * The collection is expected to be maintained in a sorted state or we could 
 * implement the sorting logic internally if needed.
 */
public class PriorityJobIterator implements IJobIterator {
    private final List<BaseJob> sortedJobs;
    private int cursor;

    public PriorityJobIterator(List<BaseJob> sortedJobs) {
        this.sortedJobs = sortedJobs;
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < sortedJobs.size();
    }

    @Override
    public BaseJob getNext() {
        if (!hasNext()) return null;
        BaseJob job = sortedJobs.get(cursor);
        cursor++;
        return job;
    }
}
