package fmlogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <h1>02 - Factory Method: The "Pluggable Registry" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> You are building a <b>Notification Orchestrator</b>. 
 * Clients choose their transport (SMS, EMAIL, SLACK) via config. 
 * Adding a new transport shouldn't require changing the core logic.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>OCP Mastery:</b> Don't use switch/if-else. Use a Map-based Registry.
 * 2. <b>Lazy Loading:</b> Method references (::new) prevent instantiating 
 *    all factories at startup, saving memory.
 * 3. <b>Dependency Injection:</b> In modern Java, this registry is often 
 *    auto-populated by Spring's <code>List<Factory></code> injection.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Unsupported Types:</b> Throws clear exception or returns Optional.
 * - <b>Case Sensitivity:</b> Inputs are normalized to uppercase.
 * - <b>Null Safety:</b> Prevents NPE if config is missing.
 */
public class RegistryFactory {

    // --- [INTERVIEW_MVP] (Basic Map for OCP - Fast Implementation) ---
    private static final Map<String, ILoggerFactory> SIMPLE_REGISTRY = new HashMap<>();

    // --- [PRODUCTION_ENHANCEMENT] (Lazy Supplier Registry - Memory Efficient) ---
    private static final Map<String, Supplier<ILoggerFactory>> ADVANCED_REGISTRY = new HashMap<>();

    static {
        // [MVP Setup]
        SIMPLE_REGISTRY.put("PROD", new ErrorLoggerFactory());
        SIMPLE_REGISTRY.put("DEV", new DebugLoggerFactory());

        // [Production Setup]
        ADVANCED_REGISTRY.put("PROD", ErrorLoggerFactory::new);
        ADVANCED_REGISTRY.put("DEV", DebugLoggerFactory::new);
    }

    /**
     * [INTERVIEW_MVP]: Standard fast lookup. 
     * @return null if not found (Client must handle).
     */
    public static ILoggerFactory getFactory(String env) {
        if (env == null) return null;
        return SIMPLE_REGISTRY.get(env.toUpperCase());
    }

    /**
     * [PRODUCTION_ENHANCEMENT]: Fail-fast and Safe API.
     * @throws IllegalArgumentException if type is unsupported.
     */
    public static ILoggerFactory getFactorySecure(String env) {
        return Optional.ofNullable(env)
                .map(String::toUpperCase)
                .map(ADVANCED_REGISTRY::get)
                .map(Supplier::get)
                .orElseThrow(() -> new IllegalArgumentException("Unknown environment: " + env));
    }
}
