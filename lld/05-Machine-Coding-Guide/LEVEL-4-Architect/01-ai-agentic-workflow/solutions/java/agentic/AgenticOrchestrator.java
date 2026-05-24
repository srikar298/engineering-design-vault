package agentic;

import java.util.Map;

public class AgenticOrchestrator {
    private final ToolRegistry toolRegistry;
    private final BaseLLMProvider llmChain;

    public AgenticOrchestrator(ToolRegistry toolRegistry, BaseLLMProvider llmChain) {
        this.toolRegistry = toolRegistry;
        this.llmChain = llmChain;
    }

    public AgentContext execute(String goal) {
        AgentContext context = new AgentContext(goal);
        context.addLog("Initializing orchestrator for goal: " + goal);
        
        int loopCount = 0;
        int maxLoops = 5;

        while (context.getState() != AgentState.SUCCESS && context.getState() != AgentState.FAILED && loopCount < maxLoops) {
            loopCount++;
            
            // 1. PLANNING State
            context.setState(AgentState.PLANNING);
            context.addLog(String.format("[Loop-%d] Analyzing status and planning next action...", loopCount));
            
            // Build the prompt containing history/logs
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Goal: ").append(context.getGoal()).append("\nHistory Logs:\n");
            for (String log : context.getLogs()) {
                promptBuilder.append(" - ").append(log).append("\n");
            }
            promptBuilder.append("Determine next action: return either a ToolCall JSON or a final text response.");

            LLMResponse response;
            try {
                // Request generation through the Chain of Responsibility fallback strategy
                response = llmChain.generateWithFallback(promptBuilder.toString());
            } catch (Exception e) {
                context.setState(AgentState.FAILED);
                context.addLog("Error executing LLM request: " + e.getMessage());
                break;
            }

            if (response.hasToolCall()) {
                // 2. EXECUTING State
                context.setState(AgentState.EXECUTING);
                ToolCall call = response.getToolCall();
                context.addLog(String.format("Decided to trigger tool: %s with parameters %s", call.getToolName(), call.getParameters()));
                
                // Trigger command pattern tool
                try {
                    String result = toolRegistry.executeTool(call.getToolName(), call.getParameters());
                    
                    // 3. EVALUATING State
                    context.setState(AgentState.EVALUATING);
                    context.addLog(String.format("Tool %s finished. Output: \"%s\"", call.getToolName(), result));
                } catch (Exception e) {
                    context.setState(AgentState.FAILED);
                    context.addLog(String.format("Tool execution %s failed: %s", call.getToolName(), e.getMessage()));
                    break;
                }
            } else {
                // Goal satisfied!
                context.setState(AgentState.SUCCESS);
                context.addLog("Reasoning completed. Final LLM Answer: " + response.getTextResponse());
            }
        }

        if (loopCount >= maxLoops && context.getState() != AgentState.SUCCESS) {
            context.setState(AgentState.FAILED);
            context.addLog("Exceeded maximum execution loops (" + maxLoops + ") without completing the goal.");
        }

        return context;
    }
}
