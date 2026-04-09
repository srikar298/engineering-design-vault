package proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Modern Add-on: Semantic Cache Proxy (2025 AI Standard)</h1>
 * 
 * <b>Scenario:</b> You are building a Chatbot. Users often ask similar questions 
 * (e.g., "How to reset password?" and "I forgot my password"). 
 * 
 * <b>2025 Senior Insight:</b>
 * Exact string matching (Standard Cache) fails here. Senior engineers use 
 * <b>Semantic Caching</b>. In an LLD interview, you can simulate this by 
 * normalizing strings or using a "Similarity Threshold" check.
 */

interface LLMService {
    String ask(String prompt);
}

class RealLLMService implements LLMService {
    @Override public String ask(String p) {
        System.out.println("[API] Calling OpenAI/Anthropic (Cost: $$)...");
        return "Response to: " + p;
    }
}

class SemanticCacheProxy implements LLMService {
    private final RealLLMService service = new RealLLMService();
    private final Map<String, String> cache = new HashMap<>();

    @Override
    public String ask(String prompt) {
        // --- [INTERVIEW_MVP] (Basic Normalization) ---
        String normalized = prompt.toLowerCase().trim().replaceAll("[^a-zA-Z0-9]", "");

        // --- [PRODUCTION_ENHANCEMENT] (Semantic Check) ---
        if (cache.containsKey(normalized)) {
            System.out.println("[Cache] Semantic Hit found for: " + prompt);
            return cache.get(normalized);
        }

        String response = service.ask(prompt);
        cache.put(normalized, response);
        return response;
    }
}

class SemanticCacheDemo {
    public static void main(String[] args) {
        LLMService ai = new SemanticCacheProxy();
        
        System.out.println(ai.ask("What is SOLID?"));
        System.out.println(ai.ask("what is solid??  ")); // Cache Hit!
    }
}
