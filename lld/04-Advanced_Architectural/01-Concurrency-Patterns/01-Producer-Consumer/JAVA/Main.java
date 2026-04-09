import model.Message;
import pool.ThreadPoolFactory;
import queue.DeadLetterQueue;
import queue.MessageQueue;
import workers.CpuBoundTask;
import workers.IdempotentConsumer;
import workers.IoBoundTask;
import workers.Producer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Producer-Consumer v2 — Production-Grade Demo</h1>
 *
 * <p>Three scenarios are run in sequence:
 * <ol>
 *   <li>Idempotent consumption with DLQ routing</li>
 *   <li>CPU-bound thread pool (N_cores + 1 threads)</li>
 *   <li>I/O-bound thread pool (N_cores × (1 + wait/cpu) threads)</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        printBanner();

        // ================================================================
        // SCENARIO 1: Idempotent Consumers + DLQ
        // ================================================================
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println(" SCENARIO 1: Idempotent Consumers + Dead-Letter Queue");
        System.out.println("═══════════════════════════════════════════════════\n");

        DeadLetterQueue dlq = new DeadLetterQueue();
        MessageQueue sharedQueue = new MessageQueue(5, dlq);

        // Shared idempotency store — simulates a Redis SETNX cache
        Set<String> processedIds = Collections.newSetFromMap(new ConcurrentHashMap<>());

        ExecutorService consumerPool = ThreadPoolFactory.ioBoundPool(200, 50);
        consumerPool.execute(new IdempotentConsumer(1, sharedQueue, processedIds));
        consumerPool.execute(new IdempotentConsumer(2, sharedQueue, processedIds));

        ExecutorService producerPool = ThreadPoolFactory.cpuBoundPool();
        producerPool.execute(new Producer(1, sharedQueue, 4));

        // Simulate duplicate delivery: re-enqueue an already-processed message
        Thread.sleep(300);
        Message alreadySeen = sharedQueue.consume(); // pull one out
        System.out.println("\n   🔁 [Test] Simulating re-delivery of: " + alreadySeen);
        sharedQueue.produce(alreadySeen);             // put it back (duplicate!)

        producerPool.shutdown();
        producerPool.awaitTermination(5, TimeUnit.SECONDS);
        Thread.sleep(1000); // let consumers finish

        consumerPool.shutdownNow();
        consumerPool.awaitTermination(2, TimeUnit.SECONDS);

        if (!dlq.isEmpty()) {
            dlq.printReport();
        } else {
            System.out.println("\n✅ DLQ is empty — all messages processed successfully.");
        }

        // ================================================================
        // SCENARIO 2: CPU-Bound Pool
        // ================================================================
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println(" SCENARIO 2: CPU-Bound Thread Pool");
        System.out.println("═══════════════════════════════════════════════════\n");

        MessageQueue cpuQueue = new MessageQueue(10, new DeadLetterQueue());
        ExecutorService cpuPool = ThreadPoolFactory.cpuBoundPool();

        cpuPool.execute(new CpuBoundTask(1, cpuQueue));
        cpuPool.execute(new CpuBoundTask(2, cpuQueue));

        // Produce 4 messages for CPU workers
        for (int i = 1; i <= 4; i++) {
            cpuQueue.produce(new Message("UserPassword-" + i));
        }

        Thread.sleep(1500); // let workers crunch
        cpuPool.shutdownNow();
        cpuPool.awaitTermination(2, TimeUnit.SECONDS);

        // ================================================================
        // SCENARIO 3: I/O-Bound Pool
        // ================================================================
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println(" SCENARIO 3: I/O-Bound Thread Pool");
        System.out.println("═══════════════════════════════════════════════════\n");

        // waitTimeMs=200, serviceTimeMs=50  →  N_cores × (1 + 200/50) = N×5 threads
        MessageQueue ioQueue = new MessageQueue(10, new DeadLetterQueue());
        ExecutorService ioPool = ThreadPoolFactory.ioBoundPool(200, 50);

        for (int i = 1; i <= 3; i++) {
            ioPool.execute(new IoBoundTask(i, ioQueue));
        }

        for (int i = 1; i <= 6; i++) {
            ioQueue.produce(new Message("UserRecord-" + i));
        }

        Thread.sleep(1500);
        ioPool.shutdownNow();
        ioPool.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("\n✅ All scenarios complete.");
    }

    private static void printBanner() {
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("   Producer-Consumer v2: Production-Grade Patterns    ");
        System.out.println("   ✓ Idempotency  ✓ DLQ  ✓ CPU vs I/O Sizing        ");
        System.out.println("══════════════════════════════════════════════════════\n");
    }
}
