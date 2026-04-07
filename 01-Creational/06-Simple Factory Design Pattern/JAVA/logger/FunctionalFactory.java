package logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * <h1>Pragmatic SDE-2+ Simple Factory (Functional Approach)</h1>
 * 
 * Traditional Simple Factories often return static instances or 
 * use 'new' directly in a switch/if-else. 
 * 
 * PROBLEM: What if each product needs DIFFERENT parameters at creation?
 * SOLUTION: Use a Map of Functions (Lambdas)!
 */
public class FunctionalFactory {

    /**
     * A registry of creation functions. 
     * Key: LogLevel
     * Value: A function that takes a 'prefix' String and returns a new ILogger instance.
     */
    private static final Map<LogLevel, Function<String, ILogger>> REGISTRY = new HashMap<>();

    static {
        // We register "How to create" each logger
        // This allows each logger to be created with dynamic runtime parameters
        REGISTRY.put(LogLevel.DEBUG, prefix -> new CustomLogger("[DEBUG] " + prefix, LogLevel.DEBUG));
        REGISTRY.put(LogLevel.INFO,  prefix -> new CustomLogger("[INFO]  " + prefix, LogLevel.INFO));
        REGISTRY.put(LogLevel.ERROR, prefix -> new CustomLogger("[ERROR] " + prefix, LogLevel.ERROR));
    }

    /**
     * Dynamic creation with parameters.
     */
    public static Optional<ILogger> createLogger(LogLevel level, String prefix) {
        Function<String, ILogger> creator = REGISTRY.get(level);
        return (creator != null) ? Optional.of(creator.apply(prefix)) : Optional.empty();
    }
}

/**
 * A sample logger that takes a dynamic prefix.
 */
class CustomLogger implements ILogger {
    private final String prefix;
    private final LogLevel level;

    CustomLogger(String prefix, LogLevel level) {
        this.prefix = prefix;
        this.level = level;
    }

    @Override
    public void log(String message) {
        System.out.println(prefix + ": " + message);
    }

    @Override
    public LogLevel getLevel() {
        return level;
    }
}
