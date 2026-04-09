package addons.concurrency;

import java.util.concurrent.*;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h1>18 - Multi-Threaded Task Scheduler (The "Uber" Special)</h1>
 * 
 * <b>Scenario:</b> Design a system that can schedule tasks to run:
 * 1. At a specific time (Delay).
 * 2. Periodically (Recurring).
 * 3. Based on Priority.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Precision:</b> Don't use a simple <code>while(true)</code> loop with 
 *    <code>Thread.sleep()</code>. Use a <code>DelayQueue</code> or 
 *    <code>PriorityQueue</code> with a <code>Condition</code> variable.
 * 2. <b>Concurrency:</b> Use a <code>ThreadPool</code> to execute tasks so 
 *    the Scheduler thread isn't blocked by long-running tasks.
 * 3. <b>Graceful Shutdown:</b> Ensure all pending tasks are handled before 
 *    killing the service.
 */

enum TaskType { ONCE, RECURRING }

class ScheduledTask implements Comparable<ScheduledTask> {
    final Runnable command;
    final long scheduledTime; // Milliseconds
    final long period;        // For recurring
    final int priority;
    final TaskType type;

    public ScheduledTask(Runnable cmd, long delay, long period, int priority, TaskType type) {
        this.command = cmd;
        this.scheduledTime = System.currentTimeMillis() + delay;
        this.period = period;
        this.priority = priority;
        this.type = type;
    }

    @Override
    public int compareTo(ScheduledTask other) {
        if (this.scheduledTime != other.scheduledTime) {
            return Long.compare(this.scheduledTime, other.scheduledTime);
        }
        return Integer.compare(other.priority, this.priority); // Higher priority first
    }
}

public class UberTaskSchedulerSDE2 {
    private final PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition newTaskAdded = lock.newCondition();
    private final ExecutorService workerPool = Executors.newFixedThreadPool(4);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public UberTaskSchedulerSDE2() {
        Thread schedulerThread = new Thread(this::runScheduler);
        schedulerThread.start();
    }

    public void schedule(Runnable command, long delay, int priority) {
        lock.lock();
        try {
            taskQueue.add(new ScheduledTask(command, delay, 0, priority, TaskType.ONCE));
            newTaskAdded.signal();
        } finally {
            lock.unlock();
        }
    }

    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, int priority) {
        lock.lock();
        try {
            taskQueue.add(new ScheduledTask(command, initialDelay, period, priority, TaskType.RECURRING));
            newTaskAdded.signal();
        } finally {
            lock.unlock();
        }
    }

    private void runScheduler() {
        while (isRunning.get()) {
            lock.lock();
            try {
                while (taskQueue.isEmpty()) {
                    newTaskAdded.await();
                }

                long now = System.currentTimeMillis();
                ScheduledTask task = taskQueue.peek();

                if (now >= task.scheduledTime) {
                    taskQueue.poll();
                    // Execute in worker pool to avoid blocking scheduler
                    workerPool.submit(task.command);

                    // If recurring, schedule the next run
                    if (task.type == TaskType.RECURRING) {
                        taskQueue.add(new ScheduledTask(task.command, task.period, task.period, task.priority, TaskType.RECURRING));
                    }
                } else {
                    // Wait until the next task is ready or a new task is added
                    newTaskAdded.await(task.scheduledTime - now, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UberTaskSchedulerSDE2 scheduler = new UberTaskSchedulerSDE2();

        System.out.println("--- Starting Uber Scheduler Demo ---");

        // 1. Immediate high priority task
        scheduler.schedule(() -> System.out.println("[TASK] Executing High Priority (Delayed 1s)"), 1000, 10);

        // 2. Low priority task
        scheduler.schedule(() -> System.out.println("[TASK] Executing Low Priority (Delayed 2s)"), 2000, 1);

        // 3. Recurring task
        scheduler.scheduleAtFixedRate(() -> System.out.println("[TASK] Recurring Heatbeat (Every 3s)"), 0, 3000, 5);

        Thread.sleep(10000); // Let it run for 10 seconds
        System.out.println("--- Demo Finished ---");
        System.exit(0);
    }
}
