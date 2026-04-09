package addons.ai;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>06 - AI Agent Tool-Use Registry (The "Secure" Commander)</h1>
 * 
 * <b>Scenario:</b> An AI Agent needs to execute local functions (e.g., send an email, 
 * check a database). The Agent cannot "just call" your code; it must use a 
 * Registry to find and execute "Tools" safely.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Command Pattern:</b> Each tool is an isolated command. This decouples 
 *    the Orchestrator from the actual business logic.
 * 2. <b>Input Validation:</b> LLMs can provide invalid or malicious arguments. 
 *    We MUST validate tool arguments before execution (ACL).
 * 3. <b>Observability:</b> Each tool call should be logged for debugging 
 *    AI-driven workflows.
 */

// --- COMMAND CONTRACT ---
interface AgentTool {
    String getName();
    String getDescription(); // Used for prompting the LLM
    String execute(Map<String, String> args);
}

// --- CONCRETE TOOLS ---
class EmailTool implements AgentTool {
    @Override public String getName() { return "send_email"; }
    @Override public String getDescription() { return "Sends an email to a recipient."; }
    
    @Override public String execute(Map<String, String> args) {
        String to = args.getOrDefault("to", "unknown");
        String body = args.get("body");
        System.out.println("[TOOL] Sending email to: " + to);
        return "Email sent successfully to " + to;
    }
}

class DatabaseTool implements AgentTool {
    @Override public String getName() { return "get_user_info"; }
    @Override public String getDescription() { return "Fetches user data by ID."; }

    @Override public String execute(Map<String, String> args) {
        String id = args.get("id");
        return "Found user info for ID: " + id;
    }
}

// --- TOOL REGISTRY (The Orchestrator) ---
class AgentToolRegistry {
    private final Map<String, AgentTool> tools = new HashMap<>();

    public void register(AgentTool tool) {
        System.out.println("[REGISTRY] Added tool: " + tool.getName());
        tools.put(tool.getName(), tool);
    }

    public String callTool(String toolName, Map<String, String> args) {
        AgentTool tool = tools.get(toolName);
        if (tool == null) {
            return "Error: Tool '" + toolName + "' not found.";
        }
        
        // ACL: Basic Validation
        if (args == null || args.isEmpty()) {
            return "Error: Missing arguments for tool '" + toolName + "'";
        }

        return tool.execute(args);
    }
}

public class AgentToolRegistrySDE2 {
    public static void main(String[] args) {
        AgentToolRegistry registry = new AgentToolRegistry();
        registry.register(new EmailTool());
        registry.register(new DatabaseTool());

        // --- SIMULATE AI AGENT WORKFLOW ---
        // LLM returns: "I should call send_email with {to: 'ceo@company.com', body: '...'}"
        Map<String, String> aiArgs = Map.of("to", "ceo@company.com", "body", "Hello!");
        String result = registry.callTool("send_email", aiArgs);
        System.out.println("AI Result: " + result);

        // Simulated malicious/invalid call
        System.out.println("AI Result (Error): " + registry.callTool("delete_db", aiArgs));
    }
}
