package iterator;

import job.BaseJob;

public interface IJobIterator {
    boolean hasNext();
    BaseJob getNext();
}
