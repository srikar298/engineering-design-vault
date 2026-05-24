package saga.states;

import saga.SagaOrchestrator;
import saga.SagaState;

public class FailedState implements SagaState {
    @Override
    public void process(SagaOrchestrator orchestrator) {
        System.out.println("[FailedState] Saga transaction aborted. Compensation completed.");
        orchestrator.getContext().setState(null); // Terminate loop
    }

    @Override
    public String getName() { return "FailedState"; }
}
