package task;

import java.util.concurrent.Callable;

/**
 * <h1>RequestTask — upgraded from Runnable to Callable</h1>
 *
 * <p><strong>Why Callable instead of Runnable?</strong>
 * <ul>
 *   <li>{@code Runnable.run()} returns {@code void} — you can never get
 *       the result of the computation back.</li>
 *   <li>{@code Runnable.run()} cannot throw checked exceptions — any
 *       {@code RuntimeException} is silently swallowed by the pool,
 *       producing invisible failures with no log, no alert, no retry.</li>
 *   <li>{@code Callable<T>} solves both: it returns a typed result
 *       AND propagates exceptions to the caller via {@code Future.get()}.</li>
 * </ul>
 *
 * <p>This is the production-standard approach in Spring Boot's
 * {@code @Async} + {@code CompletableFuture} and Java's
 * {@code ExecutorService.submit(Callable)} APIs.
 */
public class RequestTask implements Callable<String> {

    private final String requestName;
    private final long   processingTimeMs;

    public RequestTask(String requestName, long processingTimeMs) {
        this.requestName      = requestName;
        this.processingTimeMs = processingTimeMs;
    }

    @Override
    public String call() throws Exception {
        System.out.println("   ⚙️  [" + Thread.currentThread().getName()
                           + "] Processing: " + requestName);

        // Simulate real work (DB query, template rendering, etc.)
        Thread.sleep(processingTimeMs);

        String result = "HTTP 200 OK — " + requestName + " completed in " + processingTimeMs + "ms";

        System.out.println("   ✅ [" + Thread.currentThread().getName()
                           + "] Done: " + requestName);
        return result;
    }
}
