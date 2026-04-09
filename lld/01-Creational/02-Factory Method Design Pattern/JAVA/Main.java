import fmlogger.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- STAGE 1: Factory Method Pattern ---");

        // 1. The client only needs to know about the Creator Interface.
        // In a real application, this factory is usually injected via Dependency Injection (e.g., Spring).
        ILoggerFactory factory = getFactoryFromConfig("PROD");

        // 2. The client uses the factory to get the product, completely unaware
        // of whether it's an ErrorLogger, InfoLogger, etc.
        ILogger logger = factory.createLogger();

        // 3. The client uses the product.
        logger.log("Application started successfully.");
    }

    /**
     * Simulates fetching a configuration or utilizing Dependency Injection.
     * Note how the rest of the application (the main method above) is completely decoupled
     * from the specific implementation choices here.
     */
    private static ILoggerFactory getFactoryFromConfig(String env) {
        if (env.equals("PROD")) {
            return new ErrorLoggerFactory();
        } else if (env.equals("DEV")) {
            return new DebugLoggerFactory();
        } else {
            return new InfoLoggerFactory();
        }
    }
}
