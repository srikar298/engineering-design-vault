package job;

/**
 * <h1>Command + Template Method</h1>
 * 
 * <p>Acts as the Command interface, but implements the Template Method 
 * to guarantee every Job follows the exact same lifecycle.
 */
public abstract class BaseJob {
    protected final int priority;

    public BaseJob(int priority) {
        this.priority = priority;
    }

    public int getPriority() { return priority; }

    /**
     * The Template Method (Command's 'execute')
     */
    public final void execute() {
        System.out.println("\n[Job Engine] Starting Job: " + this.getClass().getSimpleName() + " (Priority " + priority + ")");
        if (preFlightCheck()) {
            runTask();
            cleanup();
        } else {
            System.out.println("   ❌ PreFlight Check Failed! Aborting Job.");
        }
    }

    // --- Template Steps ---

    protected boolean preFlightCheck() {
        System.out.println("   -> Standard Action: Verifying system resources...");
        return true; 
    }

    protected abstract void runTask(); // Must be implemented

    protected void cleanup() {
        System.out.println("   -> Standard Action: Freeing memory buffers.");
    }
}
