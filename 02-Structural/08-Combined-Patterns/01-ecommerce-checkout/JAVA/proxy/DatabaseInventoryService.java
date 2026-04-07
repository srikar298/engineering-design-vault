package proxy;

public class DatabaseInventoryService implements InventoryService {
    
    @Override
    public boolean isItemInStock(String itemId, int requestedQuantity) {
        System.out.println("   [Database] ⏳ Querying Central Database for item '" + itemId + "'...");
        try {
            Thread.sleep(800); // Simulate slow DB call
        } catch (InterruptedException ignored) {}
        
        // Mock DB logic
        return requestedQuantity <= 5;
    }
}
