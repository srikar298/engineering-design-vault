package saga.services;

import java.util.UUID;

public class ShippingService {
    public String ship(String address) {
        if ("invalid-address".equals(address)) {
            System.out.println("[ShippingService] Failed dispatch: Invalid address format.");
            return null;
        }
        String shipmentId = UUID.randomUUID().toString().substring(0, 8);
        System.out.printf("[ShippingService] Dispatched shipment to %s. Shipment ID: %s\n", address, shipmentId);
        return shipmentId;
    }

    public boolean cancelShipment(String shipmentId) {
        System.out.printf("[ShippingService] Cancelled shipment %s successfully.\n", shipmentId);
        return true;
    }
}
