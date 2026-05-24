package saga;

public interface SagaStep {
    String getName();
    boolean execute(SagaContext context);
    boolean compensate(SagaContext context);
}
