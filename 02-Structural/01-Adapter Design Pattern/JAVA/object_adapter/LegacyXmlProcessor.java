package object_adapter;

/**
 * The Adaptee (Legacy/Third-Party Class).
 * 
 * This is an existing, useful but incompatible class.
 * It strictly requires data in XML format and we cannot modify its source code.
 */
public class LegacyXmlProcessor {
    public void analyzeXml(String xmlData) {
        if (!xmlData.contains("<xml>")) {
            System.out.println("[LegacyXmlProcessor] Error: Invalid format. Expected XML.");
            return;
        }
        System.out.println("[LegacyXmlProcessor] Successfully analyzed XML data: " + xmlData);
    }
}
