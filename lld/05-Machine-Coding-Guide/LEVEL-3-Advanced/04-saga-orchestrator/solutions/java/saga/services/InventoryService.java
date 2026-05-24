package saga.services;

import java.util.concurrent.ConcurrentHashMap;

public class InventoryService {
    private final ConcurrentHashMap<String, Integer> stock = new ConcurrentHashMap<>();

    public InventoryService() {
        stock.put("item-101", 10);
    }

    public boolean reserve(String item, int qty) {
        if (!stock.containsKey(item)) return false;
        int current = stock.get(item);
        if (current >= qty) {
            stock.put(item, current - qty);
            System.out.printf("[InventoryService] Reserved %d copies of %s. Stock left: %d\n", qty, item, current - qty);
            return true;
        }
        System.out.printf("[InventoryService] Failed reservation: %s out of stock.\n", item);
        return false;
    }

    public boolean release(String item, int qty) {
        if (!stock.containsKey(item)) return false;
        int current = stock.get(item);
        stock.put(item, current + qty);
        System.out.printf("[InventoryService] Released %d copies of %s. New stock: %d\n", qty, item, current + qty);
        return true;
    }
}
