import object_adapter.IAnalyticsTool;
import object_adapter.LegacyXmlProcessor;
import object_adapter.XmlToJsonObjectAdapter;

import class_adapter.IDatabaseReader;
import class_adapter.DatabaseClassAdapter;

/**
 * <h1>Adapter Pattern Demonstration</h1>
 * 
 * Compares Object Adapter (Composition) vs Class Adapter (Inheritance).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   1. Object Adapter (Modern/Preferred Approach)  ");
        System.out.println("==================================================\n");

        // The legacy system we want to use but can't modify
        LegacyXmlProcessor legacySystem = new LegacyXmlProcessor();

        // The adapter wraps the legacy system and implements our expected interface
        IAnalyticsTool adapter = new XmlToJsonObjectAdapter(legacySystem);

        // The client only talks to the Target interface using JSON
        String jsonPayload = "{\"user\": \"John\", \"action\": \"login\"}";
        adapter.processJsonData(jsonPayload);

        System.out.println("\n==================================================");
        System.out.println("   2. Class Adapter (Legacy Approach)             ");
        System.out.println("==================================================\n");

        // The adapter ITSELF is the legacy system due to inheritance
        IDatabaseReader classAdapter = new DatabaseClassAdapter();
        
        // Client uses the Target Interface seamlessly
        classAdapter.readData();
    }
}
