package saga.states;

import saga.SagaOrchestrator;
import saga.SagaState;
import saga.SagaStep;

import java.util.List;

public class PendingState implements SagaState {
    @Override
    public void process(SagaOrchestrator orchestrator) {
        List<SagaStep> steps = orchestrator.getSteps();
        int index = orchestrator.getCurrentStepIndex();

        if (index >= steps.size()) {
            orchestrator.getContext().setState(new CompletedState());
            return;
        }

        SagaStep currentStep = steps.get(index);
        System.out.printf("[PendingState] Executing step %d: %s\n", index + 1, currentStep.getName());
        
        boolean success = currentStep.execute(orchestrator.getContext());
        if (success) {
            System.out.printf("[PendingState] Step %s succeeded.\n", currentStep.getName());
            orchestrator.setCurrentStepIndex(index + 1);
            if (index + 1 >= steps.size()) {
                orchestrator.getContext().setState(new CompletedState());
            }
        } else {
            System.out.printf("[PendingState] Step %s failed! Transitioning to CompensatingState.\n", currentStep.getName());
            orchestrator.getContext().setState(new CompensatingState());
        }
    }

    @Override
    public String getName() { return "PendingState"; }
}
