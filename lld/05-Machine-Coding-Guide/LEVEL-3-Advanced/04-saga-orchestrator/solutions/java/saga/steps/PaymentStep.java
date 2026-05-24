package saga.steps;

import saga.SagaContext;
import saga.SagaStep;
import saga.services.PaymentService;

public class PaymentStep implements SagaStep {
    private final PaymentService service;
    private final double amount;

    public PaymentStep(PaymentService service, double amount) {
        this.service = service;
        this.amount = amount;
    }

    @Override
    public String getName() { return "PaymentStep"; }

    @Override
    public boolean execute(SagaContext context) {
        String user = (String) context.get("user");
        boolean success = service.charge(user, amount);
        if (success) {
            context.set("chargedAmount", amount);
        }
        return success;
    }

    @Override
    public boolean compensate(SagaContext context) {
        String user = (String) context.get("user");
        Double charged = (Double) context.get("chargedAmount");
        if (charged != null && charged > 0) {
            return service.refund(user, charged);
        }
        return true;
    }
}
