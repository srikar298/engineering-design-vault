package scheduler;

public abstract class ScheduledTask implements Comparable<ScheduledTask> {
    private final String taskId;
    private final Runnable command;
    private final TaskPriority priority;
    protected volatile long nextExecutionTime;
    private volatile boolean isCancelled = false;

    protected ScheduledTask(String taskId, Runnable command, long delayMs, TaskPriority priority) {
        this.taskId = taskId;
        this.command = command;
        this.nextExecutionTime = System.currentTimeMillis() + delayMs;
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public Runnable getCommand() {
        return command;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    public abstract long getNextRunTime();

    public void updateNextExecutionTime(long time) {
        this.nextExecutionTime = time;
    }

    @Override
    public int compareTo(ScheduledTask other) {
        if (this.nextExecutionTime != other.nextExecutionTime) {
            return Long.compare(this.nextExecutionTime, other.nextExecutionTime);
        }
        return Integer.compare(this.priority.ordinal(), other.priority.ordinal());
    }
}
