package proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Caching Proxy</h1>
 * 
 * <p>Protects the central database from being bombarded.
 * Checks local cache first before delegating to the real subject.
 */
public class InventoryProxy implements InventoryService {
    
    private final DatabaseInventoryService realService;
    
    // The Cache
    private final Map<String, Integer> localStockCache;

    public InventoryProxy() {
        this.realService = new DatabaseInventoryService();
        this.localStockCache = new HashMap<>();
        // Pre-warm cache for demo purposes
        localStockCache.put("LAPTOP-X", 10);
    }

    @Override
    public boolean isItemInStock(String itemId, int requestedQuantity) {
        System.out.print("   [Proxy] Checking cache for '" + itemId + "': ");
        
        if (localStockCache.containsKey(itemId)) {
            System.out.println("CACHE HIT! ⚡");
            return localStockCache.get(itemId) >= requestedQuantity;
        }

        System.out.println("CACHE MISS. Delegating to real service...");
        boolean result = realService.isItemInStock(itemId, requestedQuantity);
        
        if (result) {
            // Update cache (simplified for demo)
            localStockCache.put(itemId, 5); 
        }
        
        return result;
    }
}
