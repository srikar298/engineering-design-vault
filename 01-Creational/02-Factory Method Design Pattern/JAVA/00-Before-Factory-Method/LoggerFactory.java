package before;

public class LoggerFactory {

    // THE PROBLEM: This violates the Open/Closed Principle (OCP).
    // Every time we want to add a new Logger (e.g., CloudLogger), we MUST modify this class.
    // If we have 50 different Logger types, this method becomes a massive, unmaintainable if-else block.
    public static ILogger createLogger(String type) {
        if (type.equalsIgnoreCase("DEBUG")) {
            return new DebugLogger();
        } else if (type.equalsIgnoreCase("INFO")) {
            return new InfoLogger();
        } else if (type.equalsIgnoreCase("ERROR")) {
            return new ErrorLogger();
        } else {
            throw new IllegalArgumentException("Unknown logger type: " + type);
        }
    }
}
