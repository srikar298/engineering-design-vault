package agentic;

import java.util.Map;

public interface ToolCommand {
    String execute(Map<String, Object> parameters);
}
