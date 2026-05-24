package scheduler;

public class PeriodicTask extends ScheduledTask {
    private final long intervalMs;

    public PeriodicTask(String taskId, Runnable command, long delayMs, long intervalMs, TaskPriority priority) {
        super(taskId, command, delayMs, priority);
        this.intervalMs = intervalMs;
    }

    @Override
    public long getNextRunTime() {
        return System.currentTimeMillis() + intervalMs;
    }
}
