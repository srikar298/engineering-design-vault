package agentic;

import java.util.Map;

public class ToolCall {
    private final String toolName;
    private final Map<String, Object> parameters;

    public ToolCall(String toolName, Map<String, Object> parameters) {
        this.toolName = toolName;
        this.parameters = parameters;
    }

    public String getToolName() {
        return toolName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
