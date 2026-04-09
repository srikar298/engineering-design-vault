package class_adapter;

/**
 * The Class Adapter.
 * 
 * Uses INHERITANCE to bridge the gap. It extends the Adaptee (LegacySystem)
 * and implements the Target interface (IDatabaseReader).
 * 
 * NOTE: This relies on Multiple Inheritance (Interface + Class in Java).
 * Generally discouraged in modern systems compared to Object Adapters due to tight coupling.
 */
public class DatabaseClassAdapter extends LegacySystem implements IDatabaseReader {

    @Override
    public void readData() {
        System.out.println("[Class Adapter] Mapping readData() call directly to inherited fetchExistingRecords()");
        // By extending LegacySystem, we inherit its methods directly
        this.fetchExistingRecords();
    }
    
}
