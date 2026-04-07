package proxy;

public interface InventoryService {
    boolean isItemInStock(String itemId, int requestedQuantity);
}
