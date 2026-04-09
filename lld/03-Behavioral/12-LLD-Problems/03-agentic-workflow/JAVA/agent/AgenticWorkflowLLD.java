package agent;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>LLD Problem: AI Agentic Workflow (2025 SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> You are building an autonomous AI Agent that breaks down a 
 * user prompt ("Book a flight and a hotel") into multiple tasks, executes them, 
 * and streams the status back to the UI.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Command Pattern:</b> Used to encapsulate the specific tasks (Flight, Hotel) 
 *    so the Agent (Invoker) can execute them uniformly.
 * 2. <b>Observer Pattern:</b> Used to stream UI updates (Pending, Running, Done) 
 *    without coupling the tasks to the WebSocket or UI layer.
 * 3. <b>State Pattern (Simplified):</b> The Agent tracks its lifecycle (Idle, Working).
 */

// --- 1. OBSERVER (UI Streaming) ---
interface AgentObserver {
    void onTaskUpdate(String taskName, String status);
}

class WebSocketUI implements AgentObserver {
    @Override public void onTaskUpdate(String t, String s) {
        System.out.println("[WebSocket -> UI] Task '" + t + "' is now: " + s);
    }
}

// --- 2. COMMAND (The Agent's Tools) ---
interface AgentTask {
    void execute(AgentObserver observer);
    String getName();
}

class BookFlightTask implements AgentTask {
    @Override public String getName() { return "Flight Booking"; }
    @Override public void execute(AgentObserver observer) {
        observer.onTaskUpdate(getName(), "RUNNING...");
        System.out.println("   [API] Calling Expedia API...");
        observer.onTaskUpdate(getName(), "COMPLETED");
    }
}

class BookHotelTask implements AgentTask {
    @Override public String getName() { return "Hotel Booking"; }
    @Override public void execute(AgentObserver observer) {
        observer.onTaskUpdate(getName(), "RUNNING...");
        System.out.println("   [API] Calling Booking.com API...");
        observer.onTaskUpdate(getName(), "COMPLETED");
    }
}

// --- 3. THE AGENT (Invoker & Context) ---
class AIAgent {
    private final List<AgentObserver> observers = new ArrayList<>();
    private final List<AgentTask> queue = new ArrayList<>();
    private String state = "IDLE"; // Simplified State

    public void attach(AgentObserver o) { observers.add(o); }

    public void plan(AgentTask task) { 
        queue.add(task); 
        System.out.println("Agent: Added task to plan -> " + task.getName());
    }

    public void executePlan() {
        this.state = "WORKING";
        System.out.println("\nAgent state changed to: " + state);

        for (AgentTask task : queue) {
            // Passes the observer down so tasks can stream their own updates
            task.execute(observers.get(0)); 
        }

        this.state = "IDLE";
        System.out.println("Agent state changed to: " + state);
        queue.clear();
    }
}

public class AgenticWorkflowLLD {
    public static void main(String[] args) {
        AIAgent agent = new AIAgent();
        
        // 1. Attach UI listener (Observer)
        agent.attach(new WebSocketUI());

        // 2. AI parses user intent and builds a plan (Commands)
        System.out.println("User Prompt: 'Book a trip to Paris'");
        agent.plan(new BookFlightTask());
        agent.plan(new BookHotelTask());

        // 3. Agent executes autonomously
        agent.executePlan();
        
        System.out.println("\n✅ Agentic Workflow completed successfully.");
    }
}
