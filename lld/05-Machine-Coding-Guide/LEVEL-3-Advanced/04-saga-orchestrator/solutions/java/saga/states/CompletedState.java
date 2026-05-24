package saga.states;

import saga.SagaOrchestrator;
import saga.SagaState;

public class CompletedState implements SagaState {
    @Override
    public void process(SagaOrchestrator orchestrator) {
        System.out.println("[CompletedState] Saga transaction successfully completed. No actions needed.");
        orchestrator.getContext().setState(null); // Terminate loop
    }

    @Override
    public String getName() { return "CompletedState"; }
}
