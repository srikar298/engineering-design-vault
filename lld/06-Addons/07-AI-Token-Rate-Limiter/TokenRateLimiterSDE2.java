package addons.ai;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * <h1>07 - AI Token-based Rate Limiter (The "TPM" Guard)</h1>
 * 
 * <b>Scenario:</b> LLM Providers (OpenAI, Anthropic) limit you based on 
 * <b>Tokens Per Minute (TPM)</b>, not just Requests. A 10,000 token prompt 
 * costs more than a 10 token prompt.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Sliding Window Log:</b> We track exact timestamps and token counts 
 *    for each request. This is more accurate than a Fixed Window.
 * 2. <b>Token Estimation:</b> A common rule of thumb is 1 token = 4 characters. 
 *    A Senior implementation decouples the estimator from the limiter.
 * 3. <b>Concurrency:</b> Uses <code>ConcurrentLinkedQueue</code> to allow 
 *    multiple threads (user requests) to check the limit simultaneously.
 * 
 * <b>Edge Cases:</b>
 * - <b>Burst Handling:</b> Allows rapid small requests but blocks single massive prompts.
 * - <b>Old Log Cleanup:</b> Automatically removes logs older than 60 seconds.
 */

class TokenRequestLog {
    long timestamp;
    int tokens;
    TokenRequestLog(long ts, int t) { this.timestamp = ts; this.tokens = t; }
}

class TokenRateLimiter {
    private final int tpmLimit;
    private final ConcurrentLinkedQueue<TokenRequestLog> requestLogs = new ConcurrentLinkedQueue<>();

    public TokenRateLimiter(int limit) { this.tpmLimit = limit; }

    /**
     * Estimates token count (e.g., 4 characters = 1 token).
     */
    public int estimateTokens(String text) {
        return (int) Math.ceil(text.length() / 4.0);
    }

    public synchronized boolean allowRequest(String prompt) {
        long now = System.currentTimeMillis();
        int tokensRequested = estimateTokens(prompt);

        // 1. Cleanup: Remove logs older than 1 minute
        long windowStart = now - TimeUnit.MINUTES.toMillis(1);
        while (!requestLogs.isEmpty() && requestLogs.peek().timestamp < windowStart) {
            requestLogs.poll();
        }

        // 2. Sum current tokens in the window
        int currentTokens = requestLogs.stream().mapToInt(log -> log.tokens).sum();

        // 3. Check Limit
        if (currentTokens + tokensRequested > tpmLimit) {
            System.out.println("   [LIMITER] BLOCKED: Reached TPM Limit (" + currentTokens + "/" + tpmLimit + ")");
            return false;
        }

        // 4. Accept & Log
        requestLogs.add(new TokenRequestLog(now, tokensRequested));
        System.out.println("   [LIMITER] ALLOWED: Using " + tokensRequested + " tokens.");
        return true;
    }
}

public class TokenRateLimiterSDE2 {
    public static void main(String[] args) {
        TokenRateLimiter limiter = new TokenRateLimiter(100); // 100 TPM Limit

        String smallPrompt = "What is 2+2?"; // ~4 tokens
        String hugePrompt = "Explain all of human history in 10000 words..."; // ~2500 tokens

        System.out.println("Request 1 (Small): " + limiter.allowRequest(smallPrompt));
        System.out.println("Request 2 (Huge): " + limiter.allowRequest(hugePrompt)); // Fails
        
        // Simulate rapid small requests
        for (int i = 0; i < 20; i++) {
            System.out.print("R" + i + ": ");
            limiter.allowRequest("Quick question #" + i);
        }
    }
}
