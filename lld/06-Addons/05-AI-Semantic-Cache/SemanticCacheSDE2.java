package addons.ai;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>05 - AI Semantic Cache (The "Cost-Efficient" Proxy)</h1>
 * 
 * <b>Scenario:</b> LLM calls are expensive and slow. "How's the weather?" and 
 * "What's the weather like?" are semantically identical. This Proxy intercepts 
 * calls and returns a cached response if a "similar" query is found.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Fuzzy Matching:</b> In a real system, we'd use Vector Embeddings (Cosine Similarity). 
 *    Here, we simulate this with a normalized string comparison.
 * 2. <b>Latency vs. Accuracy:</b> A higher similarity threshold is safer but 
 *    results in more cache misses (higher cost).
 * 3. <b>TTL & Invalidation:</b> AI responses can become stale (e.g., weather updates).
 */

interface LLMService {
    String query(String prompt);
}

class OpenAIService implements LLMService {
    @Override
    public String query(String prompt) {
        System.out.println("   [API] Calling OpenAI (Cost: $0.02, Latency: 1.5s)...");
        return "Deep Response to: " + prompt;
    }
}

class SemanticCacheProxy implements LLMService {
    private final LLMService realService = new OpenAIService();
    private final Map<String, String> cache = new HashMap<>();
    private static final double SIMILARITY_THRESHOLD = 0.85;

    @Override
    public String query(String prompt) {
        String normalized = prompt.toLowerCase().trim();

        // 1. Check for Semantic Match
        for (String cachedPrompt : cache.keySet()) {
            if (calculateSimilarity(normalized, cachedPrompt) >= SIMILARITY_THRESHOLD) {
                System.out.println("[CACHE] Semantic Hit! Found match for: '" + cachedPrompt + "'");
                return cache.get(cachedPrompt);
            }
        }

        // 2. Cache Miss -> Call Real LLM
        String response = realService.query(prompt);
        cache.put(normalized, response);
        return response;
    }

    /**
     * Simulation of Cosine Similarity/Jaccard Similarity.
     * In Production: Use Vector DB (Pinecone, Milvus) and Embeddings.
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        // Simple overlap logic for demo purposes
        long matches = s1.chars().filter(ch -> s2.indexOf(ch) != -1).count();
        return (double) matches / Math.max(s1.length(), s2.length());
    }
}

public class SemanticCacheSDE2 {
    public static void main(String[] args) {
        LLMService ai = new SemanticCacheProxy();

        System.out.println("User 1: " + ai.query("What is the weather in London?"));
        
        // User 2 asks a semantically similar question
        System.out.println("User 2: " + ai.query("Weather in London?")); // Cache Hit
        
        System.out.println("User 3: " + ai.query("Explain Quantum Physics.")); // Cache Miss
    }
}
