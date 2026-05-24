import agentic.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🤖 Enterprise AI Agentic Workflow Simulation 🤖");
        System.out.println("=================================================\n");

        // 1. Set up Tool Registry (Command Pattern)
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.registerTool("CalculatorTool", new CalculatorTool());
        toolRegistry.registerTool("WebSearchTool", new WebSearchTool());

        System.out.println("--- 🔧 Registered Tools: CalculatorTool, WebSearchTool ---");

        // 2. Set up LLM Provider chain (Chain of Responsibility + Strategy)
        // Scenario A: Healthy Gemini provider (No failures)
        GeminiProvider healthyGemini = new GeminiProvider(null, false);

        System.out.println("\n--- 🟢 Scenario 1: Standard Reason-And-Solve Loop ---");
        AgenticOrchestrator healthyOrchestrator = new AgenticOrchestrator(toolRegistry, healthyGemini);
        AgentContext weatherContext = healthyOrchestrator.execute("What is the current weather in Seattle?");

        System.out.println("\nExecution History Logs:");
        for (String log : weatherContext.getLogs()) {
            System.out.println(" > " + log);
        }
        System.out.println("Final State: " + weatherContext.getState()); // Expected: SUCCESS


        // Scenario B: Gemini has transient errors, fallback to OpenAI
        System.out.println("\n--- ⏸️ Scenario 2: Resilient Provider Fallback Flow ---");
        System.out.println("Setting up Gemini with a transient failure trigger. Fallback is set to GPT-4o...");
        OpenAIProvider openAIFallback = new OpenAIProvider(null);
        GeminiProvider failingGemini = new GeminiProvider(openAIFallback, true); // simulateFailure = true

        AgenticOrchestrator resilientOrchestrator = new AgenticOrchestrator(toolRegistry, failingGemini);
        AgentContext mathContext = resilientOrchestrator.execute("Calculate 15 multiplied by 32.");

        System.out.println("\nExecution History Logs:");
        for (String log : mathContext.getLogs()) {
            System.out.println(" > " + log);
        }
        System.out.println("Final State: " + mathContext.getState()); // Expected: SUCCESS


        // Scenario C: Concurrency check
        System.out.println("\n--- ⚡ Scenario 3: Isolated Concurrent Execution ---");
        System.out.println("Running 5 concurrent agent reasoning pipelines in parallel to verify thread-safe state separation.");

        int workflowCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(workflowCount);
        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < workflowCount; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    // Mix goals to ensure context separation
                    String goal = (id % 2 == 0) 
                        ? "What is the current weather in Seattle? (ID: " + id + ")"
                        : "Calculate 15 multiplied by 32. (ID: " + id + ")";
                    
                    AgentContext ctx = healthyOrchestrator.execute(goal);
                    if (ctx.getState() == AgentState.SUCCESS) {
                        successCounter.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.err.println("Concurrent run failed: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Executor interrupted: " + e.getMessage());
        }

        System.out.println("\n--- Concurrency Test Summary ---");
        System.out.println("Total Concurrent Workflows Run: " + workflowCount);
        System.out.println("Successful Workflows Completed: " + successCounter.get() + " (Expected: " + workflowCount + ")");
        if (successCounter.get() == workflowCount) {
            System.out.println("🟢 SUCCESS: All concurrent agent workflows finished with isolated history states!");
        } else {
            System.out.println("🔴 FAILURE: Race conditions detected or state sharing occurred.");
        }
    }
}
