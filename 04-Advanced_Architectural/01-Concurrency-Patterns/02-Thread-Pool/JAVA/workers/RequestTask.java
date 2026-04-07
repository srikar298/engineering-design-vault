package workers;

/**
 * <h1>The Task Object</h1>
 * 
 * <p>Represents a unit of work (e.g. processing an incoming HTTP request).
 */
public class RequestTask implements Runnable {
    private final String requestName;

    public RequestTask(String requestName) {
        this.requestName = requestName;
    }

    @Override
    public void run() {
        System.out.println("   ⚙️ [" + Thread.currentThread().getName() + "] Started processing: " + requestName);
        try {
            // Simulate 2 seconds of heavy processing (e.g., query DB, render HTML)
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("   ✅ [" + Thread.currentThread().getName() + "] Finished processing: " + requestName);
    }
}
