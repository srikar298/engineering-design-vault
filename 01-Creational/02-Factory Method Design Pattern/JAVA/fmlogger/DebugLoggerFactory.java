package fmlogger;

/**
 * <h1>Concrete Creators</h1>
 * Each creator overrides the Factory Method to instantiate and return 
 * its corresponding Concrete Product.
 * 
 * Because the Product constructors (DebugLogger, etc) are package-private,
 * these factories are the ONLY legal way those objects can be brought into
 * existence.
 */

public class DebugLoggerFactory implements ILoggerFactory {
    @Override
    public ILogger createLogger() {
        return new DebugLogger(); // Direct instantiation
    }
}
