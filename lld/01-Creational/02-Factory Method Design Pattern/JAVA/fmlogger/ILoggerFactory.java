package fmlogger;

/**
 * <h1>The Creator Interface (The Factory Method)</h1>
 * This is the core of the Factory Method Design Pattern.
 * 
 * Instead of a single class with a massive switch statement (Simple Factory),
 * we define an interface with a method that returns the Product (ILogger).
 * 
 * We defer the actual instantiation logic to the subclasses (Concrete Creators).
 * This completely satisfies the Open/Closed Principle: adding a new Logger
 * requires zero changes to this interface or existing factories. We just
 * create a new class that implements this interface.
 */
public interface ILoggerFactory {
    
    // The Factory Method
    ILogger createLogger();
    
}
