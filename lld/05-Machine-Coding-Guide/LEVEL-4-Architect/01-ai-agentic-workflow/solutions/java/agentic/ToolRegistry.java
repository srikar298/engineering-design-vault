package agentic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToolRegistry {
    private final Map<String, ToolCommand> tools;

    public ToolRegistry() {
        this.tools = new ConcurrentHashMap<>();
    }

    public void registerTool(String name, ToolCommand tool) {
        tools.put(name, tool);
    }

    public String executeTool(String name, Map<String, Object> parameters) {
        ToolCommand tool = tools.get(name);
        if (tool == null) {
            return "Error: Tool '" + name + "' not found in registry.";
        }
        return tool.execute(parameters);
    }
}
