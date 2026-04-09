package addons.api;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>10 - API Versioning (The "Evolving" System)</h1>
 * 
 * <b>Scenario:</b> You built a system for 10k users. Six months later, you 
 * need to change the API, but you cannot break the existing users. 
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Evolutionary Architecture:</b> Handle <code>v1</code> and <code>v2</code> 
 *    simultaneously without duplicating business logic.
 * 2. <b>Versioned Routing:</b> Decouple the API version from the Core Logic 
 *    using a Router or Versioned Factory.
 * 3. <b>Deprecation Strategy:</b> Mark old versions with headers (e.g., 
 *    <code>X-Deprecation-Date</code>) to notify clients.
 */

// --- CORE LOGIC (Shared) ---
class UserData {
    String name;
    UserData(String n) { this.name = n; }
}

// --- VERSIONED HANDLERS ---
interface ApiHandler {
    String handleRequest(String input);
}

class V1Handler implements ApiHandler {
    @Override
    public String handleRequest(String input) {
        return "{ \"username\": \"" + input + "\", \"note\": \"Old V1 Format\" }";
    }
}

class V2Handler implements ApiHandler {
    @Override
    public String handleRequest(String input) {
        return "{ \"profile\": { \"display_name\": \"" + input + "\" }, \"meta\": { \"version\": \"2.0\" } }";
    }
}

// --- VERSIONED ROUTER ---
class ApiRouter {
    private final Map<String, ApiHandler> handlers = new HashMap<>();

    public ApiRouter() {
        handlers.put("v1", new V1Handler());
        handlers.put("v2", new V2Handler());
    }

    public String route(String version, String input) {
        ApiHandler handler = handlers.getOrDefault(version.toLowerCase(), handlers.get("v1"));
        System.out.println("[ROUTER] Routing to: " + version);
        
        // --- [PRODUCTION_ENHANCEMENT] (Deprecation Warning) ---
        if (version.equalsIgnoreCase("v1")) {
            System.out.println("   [WARNING] X-API-Status: Deprecated. Switch to V2 by 2026-12-31.");
        }
        
        return handler.handleRequest(input);
    }
}

public class ApiVersioningSDE2 {
    public static void main(String[] args) {
        ApiRouter router = new ApiRouter();

        // 1. Client using legacy V1
        System.out.println("V1 Response: " + router.route("v1", "alice_99"));

        // 2. Client using modern V2
        System.out.println("V2 Response: " + router.route("v2", "bob_builder"));
    }
}
