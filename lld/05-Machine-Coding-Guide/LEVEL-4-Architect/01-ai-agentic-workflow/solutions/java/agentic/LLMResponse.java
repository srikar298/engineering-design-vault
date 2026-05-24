package agentic;

public class LLMResponse {
    private final String textResponse;
    private final ToolCall toolCall;
    private final boolean hasToolCall;

    public LLMResponse(String textResponse) {
        this.textResponse = textResponse;
        this.toolCall = null;
        this.hasToolCall = false;
    }

    public LLMResponse(ToolCall toolCall) {
        this.textResponse = null;
        this.toolCall = toolCall;
        this.hasToolCall = true;
    }

    public String getTextResponse() {
        return textResponse;
    }

    public ToolCall getToolCall() {
        return toolCall;
    }

    public boolean hasToolCall() {
        return hasToolCall;
    }
}
