package agentic;

import java.util.HashMap;
import java.util.Map;

public class OpenAIProvider extends BaseLLMProvider {
    public OpenAIProvider(LLMProvider fallbackProvider) {
        super("GPT-4o", fallbackProvider);
    }

    @Override
    public LLMResponse generate(String prompt) throws Exception {
        // Backup provider logic
        if (prompt.contains("Seattle") && prompt.contains("weather")) {
            if (prompt.contains("Weather in Seattle: Sunny, 72°F")) {
                return new LLMResponse("GPT-4o successfully resolved: Weather in Seattle is Sunny, 72°F.");
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("query", "Seattle weather");
                return new LLMResponse(new ToolCall("WebSearchTool", params));
            }
        }
        
        if (prompt.contains("15") && prompt.contains("32")) {
            if (prompt.contains("480.0")) {
                return new LLMResponse("GPT-4o calculated result: The product of 15 and 32 is 480.0.");
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("op1", 15);
                params.put("op2", 32);
                params.put("operation", "multiply");
                return new LLMResponse(new ToolCall("CalculatorTool", params));
            }
        }

        return new LLMResponse("GPT-4o: Fallback executed successfully, final output returned.");
    }
}
