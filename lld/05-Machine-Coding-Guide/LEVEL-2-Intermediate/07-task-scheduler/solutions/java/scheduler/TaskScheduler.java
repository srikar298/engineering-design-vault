package scheduler;

import java.util.concurrent.*;

public class TaskScheduler {
    private static volatile TaskScheduler instance = null;

    private final PriorityBlockingQueue<ScheduledTask> taskQueue = new PriorityBlockingQueue<>();
    private final ConcurrentHashMap<String, ScheduledTask> taskRegistry = new ConcurrentHashMap<>();
    private final ExecutorService workerPool = Executors.newFixedThreadPool(4);
    private Thread dispatcherThread;
    private volatile boolean isRunning = false;

    private TaskScheduler() {}

    public static TaskScheduler getInstance() {
        if (instance == null) {
            synchronized (TaskScheduler.class) {
                if (instance == null) {
                    instance = new TaskScheduler();
                }
            }
        }
        return instance;
    }

    public void schedule(ScheduledTask task) {
        taskRegistry.put(task.getTaskId(), task);
        taskQueue.offer(task);
        System.out.printf("[Scheduler] Scheduled Task: %s (Runs in %d ms, Priority: %s)\n", 
            task.getTaskId(), task.getNextExecutionTime() - System.currentTimeMillis(), task.getPriority());
        
        // Interrupt dispatcher if this task is now the earliest task at the head of the queue
        if (dispatcherThread != null && taskQueue.peek() == task) {
            dispatcherThread.interrupt();
        }
    }

    public void cancel(String taskId) {
        ScheduledTask task = taskRegistry.get(taskId);
        if (task != null) {
            task.cancel();
            System.out.printf("[Scheduler] Cancelled Task: %s\n", taskId);
        } else {
            System.out.printf("[Scheduler] Task ID not found: %s\n", taskId);
        }
    }

    public synchronized void start() {
        if (isRunning) return;
        isRunning = true;
        dispatcherThread = new Thread(() -> {
            while (isRunning) {
                try {
                    ScheduledTask task = taskQueue.take(); // Blocks until a task is available
                    if (task.isCancelled()) {
                        taskRegistry.remove(task.getTaskId());
                        continue;
                    }

                    long delay = task.getNextExecutionTime() - System.currentTimeMillis();
                    if (delay > 0) {
                        // Put it back because it's not time yet, then sleep
                        taskQueue.offer(task);
                        Thread.sleep(delay);
                    } else {
                        // Time to execute! Offload to worker pool
                        workerPool.submit(() -> {
                            try {
                                if (task.isCancelled()) {
                                    taskRegistry.remove(task.getTaskId());
                                    return;
                                }
                                System.out.printf("[Worker] Executing: %s at %d (Priority: %s)\n", 
                                    task.getTaskId(), System.currentTimeMillis() % 100000, task.getPriority());
                                task.getCommand().run();
                                
                                // Reschedule if periodic
                                long nextRun = task.getNextRunTime();
                                if (nextRun != -1 && !task.isCancelled()) {
                                    task.updateNextExecutionTime(nextRun);
                                    taskQueue.offer(task);
                                } else {
                                    taskRegistry.remove(task.getTaskId());
                                }
                            } catch (Exception e) {
                                System.err.printf("[Worker] Error executing task %s: %s\n", task.getTaskId(), e.getMessage());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    // Woken up by new task schedule or shutdown
                }
            }
        });
        dispatcherThread.setName("Task-Dispatcher");
        dispatcherThread.start();
        System.out.println("[Scheduler] Scheduler started.");
    }

    public synchronized void stop() {
        if (!isRunning) return;
        isRunning = false;
        if (dispatcherThread != null) {
            dispatcherThread.interrupt();
        }
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(2, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
        }
        System.out.println("[Scheduler] Scheduler stopped.");
    }
}
