package addons.resiliency;

import java.util.Random;

/**
 * <h1>09 - Adaptive Load Shedding (The "System Protector")</h1>
 * 
 * <b>Scenario:</b> Your 10k user system hits a bottleneck. Instead of crashing 
 * (the "Death Spiral"), it starts rejecting low-priority requests (e.g., "Analytics") 
 * to save high-priority ones (e.g., "Checkout").
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Self-Healing:</b> The system monitors its own health (CPU/Memory/Latency) 
 *    and reacts automatically to protect itself.
 * 2. <b>Priority-based Rejection:</b> Not all requests are equal. We prioritize 
 *    Revenue-generating paths.
 * 3. <b>Graceful Degradation:</b> It's better to reject some users than to crash 
 *    for all users.
 */

enum Priority { HIGH, LOW }

class SystemMonitor {
    // Simulated Health (0 to 100)
    public int getLoad() { return new Random().nextInt(100); }
}

class ApiGateway {
    private final SystemMonitor monitor = new SystemMonitor();
    private static final int LOAD_THRESHOLD = 80;

    public void handleRequest(String path, Priority priority) {
        int currentLoad = monitor.getLoad();
        System.out.println("[GATEWAY] Current System Load: " + currentLoad + "%");

        // --- [INTERVIEW_MVP] (Load Shedding Logic) ---
        if (currentLoad > LOAD_THRESHOLD) {
            if (priority == Priority.LOW) {
                System.out.println("   [SHEDDER] ⚠️ Load High! Rejecting LOW PRIORITY request: " + path);
                return; // 503 Service Unavailable
            }
        }

        // Process Normally
        System.out.println("   [GATEWAY] Processing: " + path + " (Priority: " + priority + ")");
    }
}

public class AdaptiveLoadShedderSDE2 {
    public static void main(String[] args) {
        ApiGateway gateway = new ApiGateway();

        // 1. Simulate High Load and Test Rejection
        System.out.println("--- System Under Stress Test ---");
        for (int i = 0; i < 5; i++) {
            gateway.handleRequest("/checkout", Priority.HIGH);
            gateway.handleRequest("/analytics", Priority.LOW);
            System.out.println("---");
        }
    }
}
