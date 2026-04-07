package object_adapter;

/**
 * The Object Adapter.
 * 
 * Implements the Target interface (IAnalyticsTool) and 
 * wraps the Adaptee (LegacyXmlProcessor) via COMPOSITION.
 * 
 * This is the modern, preferred way of creating Adapters.
 */
public class XmlToJsonObjectAdapter implements IAnalyticsTool {
    
    // Uses Composition to hold a reference to the legacy system
    private final LegacyXmlProcessor legacyProcessor;

    public XmlToJsonObjectAdapter(LegacyXmlProcessor legacyProcessor) {
        this.legacyProcessor = legacyProcessor;
    }

    @Override
    public void processJsonData(String jsonData) {
        System.out.println("[Adapter] Received JSON: " + jsonData);
        
        // 1. Translate the data format (JSON -> XML)
        String convertedToXml = convertJsonToXml(jsonData);
        
        // 2. Delegate the call to the legacy system
        System.out.println("[Adapter] Delegating to legacy processor...");
        legacyProcessor.analyzeXml(convertedToXml);
    }

    private String convertJsonToXml(String json) {
        System.out.println("[Adapter] Translating JSON into XML...");
        // Dummy conversion logic for demonstration
        return "<xml><data>" + json.replace("{", "").replace("}", "") + "</data></xml>";
    }
}
