package before;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- STAGE 0: The Simple Factory Violation ---");
        
        // Client requests loggers by specific string parameters.
        ILogger errorLogger = LoggerFactory.createLogger("ERROR");
        errorLogger.log("Application crashed!");

        ILogger debugLogger = LoggerFactory.createLogger("DEBUG");
        debugLogger.log("Variable x = 42");
        
        // What happens if we want a "FILE" logger?
        // We have to open LoggerFactory.java, add a new nested if-else clause, 
        // recompile it, and redeploy it. This violates the Open/Closed Principle.
        try {
            ILogger fileLogger = LoggerFactory.createLogger("FILE");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
    }
}
