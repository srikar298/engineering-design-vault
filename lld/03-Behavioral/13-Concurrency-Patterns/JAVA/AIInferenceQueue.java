package concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>Modern Producer-Consumer: AI Inference Queue (2025 Standard)</h1>
 * 
 * <b>Scenario:</b> An AI Gateway receiving 10,000 requests for LLM generation. 
 * Generating an AI response is slow (1-2 seconds). We must decouple the incoming 
 * requests (Producers) from the processing workers (Consumers).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Backpressure:</b> We use a bounded <code>LinkedBlockingQueue(100)</code>. 
 *    If the queue is full, producers block (wait) instead of crashing the system.
 * 2. <b>Thread Safety:</b> <code>BlockingQueue</code> handles wait/notify 
 *    internally. No manual <code>synchronized</code> blocks needed (Josh Bloch style).
 * 3. <b>Virtual Threads (Java 21):</b> Ideal for I/O bound tasks like AI API calls. 
 *    Allows spawning thousands of consumers without OS thread overhead.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Poison Pill:</b> A special signal to stop consumers gracefully.
 * - <b>Graceful Shutdown:</b> Try-with-resources ensures all workers finish.
 */

class InferenceTask {
    public final String prompt;
    public InferenceTask(String p) { this.prompt = p; }
}

public class AIInferenceQueue {

    private static final int QUEUE_CAPACITY = 100;
    private static final int WORKER_COUNT = 3;
    // Poison Pill to signal shutdown
    private static final InferenceTask SHUTDOWN_SIGNAL = new InferenceTask("STOP");

    public static void main(String[] args) {
        BlockingQueue<InferenceTask> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        AtomicInteger processedCount = new AtomicInteger(0);

        // --- [INTERVIEW_MVP] (The Orchestrator) ---
        // Using Virtual Threads for the AI Workers
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            // Start Consumers (Workers)
            for (int i = 0; i < WORKER_COUNT; i++) {
                executor.submit(() -> consumerLoop(queue, processedCount));
            }

            // Start Producer (Gateway)
            executor.submit(() -> producerLoop(queue));

            // Run for 5 seconds then shutdown
            Thread.sleep(5000);
            for (int i = 0; i < WORKER_COUNT; i++) queue.offer(SHUTDOWN_SIGNAL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n✅ Final State: " + processedCount.get() + " AI tasks processed.");
    }

    private static void producerLoop(BlockingQueue<InferenceTask> queue) {
        try {
            for (int i = 1; i <= 10; i++) {
                InferenceTask task = new InferenceTask("Prompt #" + i);
                // blocks if queue full (Backpressure)
                queue.put(task); 
                System.out.println("[Gateway] Queued: " + task.prompt);
                Thread.sleep(200); // Simulate rapid incoming requests
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static void consumerLoop(BlockingQueue<InferenceTask> queue, AtomicInteger processedCount) {
        try {
            while (true) {
                // blocks if queue empty
                InferenceTask task = queue.take(); 
                
                if (task == SHUTDOWN_SIGNAL) break;

                // [PRODUCTION_ENHANCEMENT]: AI Inference simulation
                System.out.println("   [Worker] Processing: " + task.prompt + "...");
                Thread.sleep(1000); // Slow AI call
                processedCount.incrementAndGet();
                System.out.println("   [Worker] Done: " + task.prompt);
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
