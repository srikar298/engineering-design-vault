package addons.logging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>19 - Custom Logging Library (log4j Architecture)</h1>
 * 
 * <b>Scenario:</b> Design a logging library where users can log messages 
 * with different levels (DEBUG, INFO, ERROR). The logs should be sent to 
 * different destinations (Console, File) based on configuration.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Chain of Responsibility:</b> Log levels (INFO -> DEBUG -> ERROR) are 
 *    handled in a chain. If a message meets the level requirement, it's processed.
 * 2. <b>Strategy Pattern:</b> Decouple the Log Level from the Log Destination (Appender).
 * 3. <b>Singleton:</b> A global <code>Logger</code> instance ensures centralized config.
 * 4. <b>Formatters:</b> Use a strategy for log formatting (JSON vs String).
 */

enum LogLevel { DEBUG, INFO, ERROR }

// --- STRATEGY: Where to send the log ---
interface LogAppender {
    void append(String message);
}

class ConsoleAppender implements LogAppender {
    @Override public void append(String msg) { System.out.println("[CONSOLE] " + msg); }
}

class FileAppender implements LogAppender {
    @Override public void append(String msg) { System.out.println("[FILE] Writing to log.txt: " + msg); }
}

// --- THE CORE LOGGER (Singleton + Observer/Strategy) ---
class Logger {
    private static Logger instance;
    private LogLevel minLevel = LogLevel.INFO;
    private final List<LogAppender> appenders = new ArrayList<>();

    private Logger() {}

    public static synchronized Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public void setConfig(LogLevel level, LogAppender appender) {
        this.minLevel = level;
        this.appenders.add(appender);
    }

    public void log(LogLevel level, String message) {
        if (level.ordinal() >= minLevel.ordinal()) {
            String formatted = String.format("[%s] [%s] %s", level, LocalDateTime.now(), message);
            for (LogAppender appender : appenders) {
                appender.append(formatted);
            }
        }
    }

    public void info(String msg) { log(LogLevel.INFO, msg); }
    public void error(String msg) { log(LogLevel.ERROR, msg); }
    public void debug(String msg) { log(LogLevel.DEBUG, msg); }
}

public class LoggingLibrarySDE2 {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();

        // Configure: Log INFO and above to Console, ERROR to File
        logger.setConfig(LogLevel.INFO, new ConsoleAppender());
        
        System.out.println("--- Scenario 1: Info Log ---");
        logger.info("Application started successfully.");

        System.out.println("\n--- Scenario 2: Debug Log (Should be ignored) ---");
        logger.debug("Database connection pool initialized.");

        System.out.println("\n--- Scenario 3: Multiple Appenders ---");
        logger.setConfig(LogLevel.ERROR, new FileAppender());
        logger.error("Critical: Payment Gateway is DOWN!");
        
        System.out.println("\nSenior Signal: 'By using the Chain of Responsibility " +
                           "pattern, we allow the library to be extended with custom " +
                           "log levels and filters without modifying the core Logger engine.'");
    }
}
