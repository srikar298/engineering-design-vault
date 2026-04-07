package fmlogger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <h1>02 - Modern Factory: The "AI Model Provider" (2025 Edition)</h1>
 * 
 * <b>Scenario:</b> You are building an AI Gateway. Users can switch between 
 * OpenAI, Anthropic, and Local Llama. You need a thread-safe, 
 * OCP-compliant way to instantiate these providers.
 * 
 * <b>2025 Senior Insights:</b>
 * 1. <b>Concurrency:</b> Uses <code>ConcurrentHashMap</code> to handle 10k+ 
 *    concurrent model requests without lock contention.
 * 2. <b>AI Twist:</b> The Factory handles the complexity of "Model Versions" 
 *    and "Context Windows" behind a clean interface.
 * 3. <b>Vibe Coding Tip:</b> In an interview, ask the AI to generate the 
 *    boilerplate providers, but you MANUALLY write the Registry and 
 *    Thread-safety logic to show expertise.
 */

interface LLMProvider {
    String generate(String prompt);
}

class OpenAIProvider implements LLMProvider {
    public String generate(String p) { return "OpenAI response to: " + p; }
}

class AnthropicProvider implements LLMProvider {
    public String generate(String p) { return "Anthropic response to: " + p; }
}

public class AIModelFactory {

    // --- [INTERVIEW_MVP] (Thread-Safe Registry) ---
    private static final Map<String, Supplier<LLMProvider>> REGISTRY = new ConcurrentHashMap<>();

    static {
        REGISTRY.put("GPT-4", OpenAIProvider::new);
        REGISTRY.put("CLAUDE-3", AnthropicProvider::new);
    }

    /**
     * [PRODUCTION_ENHANCEMENT]: Returns Optional to handle missing models 
     * and uses Supplier for Lazy Instantiation (prevents API key initialization overhead).
     */
    public static Optional<LLMProvider> getModel(String modelName) {
        return Optional.ofNullable(REGISTRY.get(modelName.toUpperCase()))
                       .map(Supplier::get);
    }

    public static void main(String[] args) {
        // Mocking a 2025 API Gateway call
        LLMProvider ai = getModel("GPT-4").orElseThrow();
        System.out.println(ai.generate("Design a vector DB."));
    }
}
