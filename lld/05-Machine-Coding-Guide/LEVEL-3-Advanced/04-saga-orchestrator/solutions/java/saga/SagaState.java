package saga;

public interface SagaState {
    void process(SagaOrchestrator orchestrator);
    String getName();
}
