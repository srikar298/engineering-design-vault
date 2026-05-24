package mq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Partition {
    private final int id;
    private final List<Message> messages = new ArrayList<>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    public Partition(int id) {
        this.id = id;
    }

    public int getId() { return id; }

    public int append(Message message) {
        rwl.writeLock().lock();
        try {
            messages.add(message);
            return messages.size() - 1; // Return the offset of the appended message
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public List<Message> read(int offset, int maxCount) {
        rwl.readLock().lock();
        try {
            if (offset < 0 || offset >= messages.size()) {
                return Collections.emptyList();
            }
            int toIndex = Math.min(offset + maxCount, messages.size());
            return new ArrayList<>(messages.subList(offset, toIndex));
        } finally {
            rwl.readLock().unlock();
        }
    }

    public int getSize() {
        rwl.readLock().lock();
        try {
            return messages.size();
        } finally {
            rwl.readLock().unlock();
        }
    }
}
