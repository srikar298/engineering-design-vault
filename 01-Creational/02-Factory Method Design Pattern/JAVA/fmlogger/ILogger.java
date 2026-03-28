package fmlogger;

/**
 * <h1>The Product Interface</h1>
 * Defines the standard behavior that all Loggers must implement.
 * The Creator (Factory) will return objects of this interface type.
 */
public interface ILogger {
    void log(String message);
}
