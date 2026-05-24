package agentic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgentContext {
    private final String goal;
    private final List<String> logs;
    private AgentState state;

    public AgentContext(String goal) {
        this.goal = goal;
        this.logs = Collections.synchronizedList(new ArrayList<>());
        this.state = AgentState.PLANNING;
    }

    public String getGoal() {
        return goal;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void addLog(String log) {
        logs.add(log);
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }
}
