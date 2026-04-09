package concurrency;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>Advanced AI Task Pipeline (2025 SDE-2+ Standard)</h1>
 * 
 * <b>Patterns:</b> Producer-Consumer + Virtual Threads + Token Bucket Rate Limiting.
 * 
 * <b>Scenario:</b> A Founding Engineer building a RAG (Retrieval-Augmented Generation) 
 * pipeline. Thousands of documents are ingested (Producers), embedded, and 
 * sent to an LLM (Consumers).
 * 
 * <b>2025 Senior Insights:</b>
 * 1. <b>Virtual Threads:</b> We use <code>newVirtualThreadPerTaskExecutor</code>. 
 *    This makes the "I/O Pool Sizing" formulas obsolete because blocking is virtually free.
 * 2. <b>Rate Limiting (Backpressure):</b> We implement a <b>Token Bucket</b> to ensure 
 *    the producer doesn't hit the LLM's RPM (Requests Per Minute) limit.
 * 3. <b>Observability:</b> We track "Tokens processed" alongside "Task count."
 */

class AIMessage {
    public final String id = UUID.randomUUID().toString();
    public final String prompt;
    public final int estimatedTokens;

    public AIMessage(String p) {
        this.prompt = p;
        this.estimatedTokens = p.length() / 4; 
    }
}

public class AdvancedAIPipelineSDE2 {

    private static final int MAX_QUEUE = 50;
    private static final int RPM_LIMIT = 5; // 5 Requests Per Minute for demo
    
    public static void main(String[] args) {
        BlockingQueue<AIMessage> taskQueue = new LinkedBlockingQueue<>(MAX_QUEUE);
        AtomicInteger tokensProcessed = new AtomicInteger(0);

        // --- [INTERVIEW_MVP] (Modern Orchestration) ---
        // try-with-resources handles graceful shutdown of ALL virtual threads
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            // 1. Consumer: The AI Worker (Handles slow I/O)
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        AIMessage task = taskQueue.take();
                        processAITask(task, tokensProcessed);
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });

            // 2. Producer: The Ingestion Engine (With Rate Limiting)
            executor.submit(() -> {
                try {
                    for (int i = 1; i <= 10; i++) {
                        // [PRODUCTION_ENHANCEMENT]: Rate Limiting (Simple Token Bucket)
                        System.out.println("[Ingestion] Checking Rate Limit...");
                        Thread.sleep(1000 * 60 / RPM_LIMIT); // Wait for next slot
                        
                        AIMessage msg = new AIMessage("Deep LLD Question #" + i);
                        taskQueue.put(msg);
                        System.out.println("[Ingestion] Enqueued: " + msg.id.substring(0,8));
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });

            // Allow some time for execution
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n✅ Final Audit: " + tokensProcessed.get() + " tokens ingested into Vector DB.");
    }

    private static void processAITask(AIMessage msg, AtomicInteger tokenCounter) throws InterruptedException {
        System.out.println("   [AI Worker] Calling LLM for: " + msg.id.substring(0,8));
        Thread.sleep(2000); // Simulate slow AI response
        tokenCounter.addAndGet(msg.estimatedTokens);
        System.out.println("   [AI Worker] Task Complete. Tokens: " + msg.estimatedTokens);
    }
}
