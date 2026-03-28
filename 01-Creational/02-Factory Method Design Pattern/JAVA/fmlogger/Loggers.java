package fmlogger;

/**
 * <h1>Concrete Products</h1>
 * These are the actual implementations of the ILogger interface.
 * 
 * Notice that the constructors are package-private. This is a deliberate
 * architectural decision: clients cannot run `new DebugLogger()` directly.
 * They MUST go through the designated Factory Method to get an instance.
 */
class DebugLogger implements ILogger {
    
    // Package-private constructor: forces use of the Factory
    DebugLogger() {}
    
    @Override
    public void log(String message) {
        System.out.println("[DEBUG] " + message);
    }
}

class InfoLogger implements ILogger {
    
    InfoLogger() {}
    
    @Override
    public void log(String message) {
        System.out.println("[INFO] " + message);
    }
}

class ErrorLogger implements ILogger {
    
    ErrorLogger() {}
    
    @Override
    public void log(String message) {
        System.err.println("[ERROR] " + message);
    }
}
