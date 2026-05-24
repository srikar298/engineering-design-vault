package saga.states;

import saga.SagaOrchestrator;
import saga.SagaState;
import saga.SagaStep;

import java.util.List;

public class CompensatingState implements SagaState {
    @Override
    public void process(SagaOrchestrator orchestrator) {
        List<SagaStep> steps = orchestrator.getSteps();
        int index = orchestrator.getCurrentStepIndex();

        System.out.println("[CompensatingState] Initiating reverse-compensation rollbacks...");

        // Compensate completed steps from (currentStepIndex - 1) down to 0
        for (int i = index - 1; i >= 0; i--) {
            SagaStep step = steps.get(i);
            System.out.printf("[CompensatingState] Rolling back step %d: %s\n", i + 1, step.getName());
            boolean rollBackSuccess = step.compensate(orchestrator.getContext());
            if (!rollBackSuccess) {
                System.err.printf("[CompensatingState] CRITICAL: Rollback failed for step: %s! Retrying or logging anomaly.\n", step.getName());
                // In production, we'd queue this for manual intervention or retries.
            }
        }

        System.out.println("[CompensatingState] Compensation complete. Transitioning to FailedState.");
        orchestrator.getContext().setState(new FailedState());
    }

    @Override
    public String getName() { return "CompensatingState"; }
}
