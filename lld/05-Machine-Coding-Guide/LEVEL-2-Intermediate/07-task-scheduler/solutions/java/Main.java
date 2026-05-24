import scheduler.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("⏰ Enterprise High-Performance Task Scheduler Demo ⏰");
        System.out.println("=================================================\n");

        // 1. Get Scheduler instance & start
        TaskScheduler scheduler = TaskScheduler.getInstance();
        scheduler.start();

        // 2. Schedule various tasks
        
        // Task A: One-time run, 1000ms delay, MEDIUM priority
        scheduler.schedule(new OneTimeTask("TASK-A", () -> {
            System.out.println("-> Hello from TASK-A (One-time, Medium Priority)");
        }, 1000, TaskPriority.MEDIUM));

        // Task B: One-time run, 500ms delay, HIGH priority
        scheduler.schedule(new OneTimeTask("TASK-B", () -> {
            System.out.println("-> Hello from TASK-B (One-time, High Priority)");
        }, 500, TaskPriority.HIGH));

        // Task C: Periodic run, 200ms delay, repeating every 800ms, LOW priority
        scheduler.schedule(new PeriodicTask("TASK-C", () -> {
            System.out.println("-> Hello from TASK-C (Periodic, Low Priority)");
        }, 200, 800, TaskPriority.LOW));

        // Task D: One-time run, 2000ms delay, HIGH priority (We will cancel this before it runs)
        ScheduledTask taskD = new OneTimeTask("TASK-D", () -> {
            System.out.println("-> Hello from TASK-D (This should NOT print!)");
        }, 2000, TaskPriority.HIGH);
        scheduler.schedule(taskD);

        // Allow some tasks to start running
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cancel Task D
        scheduler.cancel("TASK-D");

        // Let the simulation run for 3 seconds
        try {
            System.out.println("\n--- Let the scheduler run for 3 seconds ---");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. Stop the scheduler
        System.out.println();
        scheduler.stop();
        System.exit(0);
    }
}
