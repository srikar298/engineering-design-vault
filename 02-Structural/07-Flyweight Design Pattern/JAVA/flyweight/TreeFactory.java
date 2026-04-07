package flyweight;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>The Flyweight Factory</h1>
 * 
 * <p>Responsible for managing and caching Flyweight objects. 
 * When a client requests a `TreeType`, the factory checks if it already exists.
 * If yes, it returns the cached instance. If no, it creates a new one, caches it, and returns it.
 */
public class TreeFactory {
    
    // The Cache
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String heavyTexture) {
        // We use the 'name' as the cache key
        TreeType result = treeTypes.get(name);
        
        if (result == null) {
            result = new TreeType(name, color, heavyTexture);
            treeTypes.put(name, result);
        }
        
        return result;
    }
    
    public static int getCacheSize() {
        return treeTypes.size();
    }
}
