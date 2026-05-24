package scheduler;

public class OneTimeTask extends ScheduledTask {
    public OneTimeTask(String taskId, Runnable command, long delayMs, TaskPriority priority) {
        super(taskId, command, delayMs, priority);
    }

    @Override
    public long getNextRunTime() {
        return -1;
    }
}
