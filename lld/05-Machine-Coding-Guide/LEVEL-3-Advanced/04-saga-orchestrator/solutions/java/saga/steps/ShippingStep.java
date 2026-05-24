package saga.steps;

import saga.SagaContext;
import saga.SagaStep;
import saga.services.ShippingService;

public class ShippingStep implements SagaStep {
    private final ShippingService service;

    public ShippingStep(ShippingService service) {
        this.service = service;
    }

    @Override
    public String getName() { return "ShippingStep"; }

    @Override
    public boolean execute(SagaContext context) {
        String address = (String) context.get("address");
        String shipmentId = service.ship(address);
        if (shipmentId != null) {
            context.set("shipmentId", shipmentId);
            return true;
        }
        return false;
    }

    @Override
    public boolean compensate(SagaContext context) {
        String shipmentId = (String) context.get("shipmentId");
        if (shipmentId != null) {
            return service.cancelShipment(shipmentId);
        }
        return true;
    }
}
