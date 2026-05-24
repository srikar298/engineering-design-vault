package saga;

import java.util.List;

public class SagaOrchestrator {
    private final List<SagaStep> steps;
    private final SagaContext context;
    private int currentStepIndex = 0;

    public SagaOrchestrator(List<SagaStep> steps, SagaContext context) {
        this.steps = steps;
        this.context = context;
    }

    public List<SagaStep> getSteps() { return steps; }
    public SagaContext getContext() { return context; }
    public int getCurrentStepIndex() { return currentStepIndex; }
    public void setCurrentStepIndex(int index) { this.currentStepIndex = index; }

    public boolean executeSaga() {
        // Initial transition to PendingState
        // To avoid circular package dependencies, we will dynamically instantiate the concrete states.
        // Wait, since we need to use states from another package (or the same package),
        // let's put all states in the same package (saga) or sub-package.
        // Putting them in 'saga.states' means they need to import 'saga.SagaOrchestrator'. That's perfectly fine!
        try {
            Class<?> pendingClass = Class.forName("saga.states.PendingState");
            SagaState pendingState = (SagaState) pendingClass.getDeclaredConstructor().newInstance();
            context.setState(pendingState);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize PendingState: " + e.getMessage());
        }

        System.out.printf("Starting Saga %s in %s state...\n", context.getSagaId(), context.getState().getName());

        SagaState lastState = null;
        while (context.getState() != null) {
            lastState = context.getState();
            context.getState().process(this);
        }
        
        return lastState != null && "CompletedState".equals(lastState.getName());
    }
}
