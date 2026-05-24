package agentic;

import java.util.HashMap;
import java.util.Map;

public class GeminiProvider extends BaseLLMProvider {
    private final boolean simulateFailure;

    public GeminiProvider(LLMProvider fallbackProvider, boolean simulateFailure) {
        super("Gemini-2.5-Pro", fallbackProvider);
        this.simulateFailure = simulateFailure;
    }

    @Override
    public LLMResponse generate(String prompt) throws Exception {
        if (simulateFailure) {
            throw new Exception("Rate limit exceeded (HTTP 429) - simulated transient error");
        }

        // Simulating the agentic reasoning paths
        if (prompt.contains("Seattle") && prompt.contains("weather")) {
            if (prompt.contains("Weather in Seattle: Sunny, 72°F")) {
                return new LLMResponse("According to current reports, the weather in Seattle is Sunny and 72°F.");
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("query", "Seattle weather");
                return new LLMResponse(new ToolCall("WebSearchTool", params));
            }
        }
        
        if (prompt.contains("15") && prompt.contains("32")) {
            if (prompt.contains("480.0")) {
                return new LLMResponse("The calculated result of multiplying 15 by 32 is 480.0.");
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("op1", 15);
                params.put("op2", 32);
                params.put("operation", "multiply");
                return new LLMResponse(new ToolCall("CalculatorTool", params));
            }
        }

        return new LLMResponse("Task completed. Dynamic reasoning finished successfully.");
    }
}
