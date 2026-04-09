package template;

/**
 * <h1>The Abstract Template</h1>
 * 
 * <p>Defines the skeleton of an algorithm (the Template Method) and delegates 
 * the specific implementation of some steps to subclasses.
 */
public abstract class DataMiner {

    /**
     * The Template Method!
     * Notice it is `final`. Subclasses CANNOT change the order of execution.
     * They can only fill in the blank steps.
     */
    public final void mineData(String filePath) {
        System.out.println("\n[DataMiner] Starting pipeline for " + filePath);
        
        openFile(filePath);            // Shared/Standard step
        byte[] rawData = extractData(); // Abstract step (Implemented by children)
        String parsedData = parseData(rawData); // Abstract step (Implemented by children)
        analyzeData(parsedData);       // Shared/Standard step (Optional hook)
        closeFile();                   // Shared/Standard step
        
        System.out.println("[DataMiner] Pipeline finished.\n");
    }

    // --- Primitive Abstract Operations (MUST be implemented) ---
    protected abstract byte[] extractData();
    protected abstract String parseData(byte[] rawData);
    
    // --- Standard Operations (Shared by all subclasses) ---
    protected void openFile(String path) {
        System.out.println("   -> Standard Action: Opening file connection...");
    }

    protected void closeFile() {
        System.out.println("   -> Standard Action: Closing file connection.");
    }

    // --- Hook Operation (Optional override) ---
    protected void analyzeData(String parsedData) {
        System.out.println("   -> Default Hook Action: Storing parsed data into standard Database.");
    }
}
