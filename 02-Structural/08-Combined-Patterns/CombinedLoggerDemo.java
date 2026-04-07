package logger;

/**
 * <h1>Combined Structural Pattern: Enhanced Logger (SDE-2+ Level)</h1>
 * 
 * <b>Patterns Combined:</b> Adapter + Decorator
 * 
 * <b>Scenario:</b> You have a standard <code>Logger</code> interface. 
 * 1. You need to <b>Adapt</b> a 3rd party legacy console (Adapter).
 * 2. You need to <b>Decorate</b> the logs with level-based filtering (Decorator).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * - Use <b>Adapter</b> to unify multiple log targets (File, Console, Cloud).
 * - Use <b>Decorator</b> to add cross-cutting features (Timestamps, JSON formatting, 
 *   LogLevel filtering) without touching the output logic.
 */

// --- COMPONENT (The Standard) ---
interface Logger {
    void log(String message);
}

// --- THE ADAPTER (Adapting a 3rd party tool) ---
class LegacySystem {
    public void printToScreen(String s) { System.out.println("LEGACY: " + s); }
}

class ConsoleAdapter implements Logger {
    private final LegacySystem legacy = new LegacySystem();
    @Override public void log(String msg) { legacy.printToScreen(msg); }
}

// --- THE DECORATOR (Adding "Level" logic) ---
abstract class LoggerDecorator implements Logger {
    protected final Logger wrappee;
    public LoggerDecorator(Logger l) { this.wrappee = l; }
}

class LevelFilterDecorator extends LoggerDecorator {
    private final String requiredLevel;

    public LevelFilterDecorator(Logger l, String level) {
        super(l);
        this.requiredLevel = level;
    }

    @Override
    public void log(String msg) {
        // --- [INTERVIEW_MVP] (Filtering Logic) ---
        if (msg.contains(requiredLevel)) {
            // --- [PRODUCTION_ENHANCEMENT] (Pre-processing: Adding Timestamp) ---
            String timestamped = "[" + System.currentTimeMillis() + "] " + msg;
            wrappee.log(timestamped);
        }
    }
}

public class CombinedLoggerDemo {
    public static void main(String[] args) {
        Logger adapter = new ConsoleAdapter();
        
        // Wrap the adapter with a Level Filter
        Logger secureLogger = new LevelFilterDecorator(adapter, "ERROR");

        secureLogger.log("INFO: Everything is fine."); // Filtered out
        secureLogger.log("ERROR: Database connection lost!"); // Printed with timestamp
    }
}
