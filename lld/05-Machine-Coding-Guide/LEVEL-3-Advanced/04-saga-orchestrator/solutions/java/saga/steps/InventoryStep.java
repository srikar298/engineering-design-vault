package saga.steps;

import saga.SagaContext;
import saga.SagaStep;
import saga.services.InventoryService;

public class InventoryStep implements SagaStep {
    private final InventoryService service;
    private final String item;
    private final int quantity;

    public InventoryStep(InventoryService service, String item, int quantity) {
        this.service = service;
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public String getName() { return "InventoryStep"; }

    @Override
    public boolean execute(SagaContext context) {
        boolean success = service.reserve(item, quantity);
        if (success) {
            context.set("reservedItem", item);
            context.set("reservedQuantity", quantity);
        }
        return success;
    }

    @Override
    public boolean compensate(SagaContext context) {
        String reservedItem = (String) context.get("reservedItem");
        Integer qty = (Integer) context.get("reservedQuantity");
        if (reservedItem != null && qty != null) {
            return service.release(reservedItem, qty);
        }
        return true;
    }
}
