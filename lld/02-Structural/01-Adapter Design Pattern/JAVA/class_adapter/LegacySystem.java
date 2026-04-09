package class_adapter;

/**
 * The Adaptee.
 * 
 * A legacy or third-party class that provides the functionality we need,
 * but through an incompatible interface/method name.
 */
public class LegacySystem {
    public void fetchExistingRecords() {
        System.out.println("[LegacySystem] Fetching records using ancient APIs...");
    }
}
