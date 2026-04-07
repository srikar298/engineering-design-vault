package fmlogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <h1>Pragmatic Factory Registry (SDE-2+ Level)</h1>
 * 
 * In real-world enterprise applications (like Spring or a Plugin System), 
 * we often combine the Factory Method with a Registry.
 * 
 * Instead of long if-else or switch blocks, we use a Map to register 
 * and retrieve our Factories. This keeps the code high-performance O(1)
 * and even more aligned with the Open/Closed Principle.
 */
public class RegistryFactory {

    private static final Map<String, Supplier<ILoggerFactory>> FACTORY_REGISTRY;

    static {
        Map<String, Supplier<ILoggerFactory>> registry = new HashMap<>();
        
        // Registering factories using Java 8 Method References (Suppliers)
        // This is extremely efficient and lazy-initialized
        registry.put("PROD", ErrorLoggerFactory::new);
        registry.put("DEV", DebugLoggerFactory::new);
        registry.put("STAGING", InfoLoggerFactory::new);
        
        FACTORY_REGISTRY = Collections.unmodifiableMap(registry);
    }

    /**
     * Retrieves the factory safely using Optional.
     * Prevents NullPointerExceptions (NPEs) and provides a clear API.
     */
    public static Optional<ILoggerFactory> getFactory(String env) {
        Supplier<ILoggerFactory> supplier = FACTORY_REGISTRY.get(env.toUpperCase());
        return (supplier != null) ? Optional.of(supplier.get()) : Optional.empty();
    }

    /**
     * Demonstrates how easy it is to "Plug in" a new factory 
     * without modifying any if-else logic.
     */
    public static void registerNewFactory(String name, Supplier<ILoggerFactory> supplier) {
        // In a real plugin system, this wouldn't be unmodifiable 
        // OR we'd use a different concurrent map structure.
        System.out.println("Registering new factory: " + name);
    }
}
