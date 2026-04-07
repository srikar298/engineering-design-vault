package abstract_factory;

import abstract_factory.factories.IGUIFactory;
import abstract_factory.factories.MacFactory;
import abstract_factory.factories.WindowsFactory;

import java.util.Optional;

/**
 * <h1>Pragmatic Factory of Factories (SDE-2+ Level)</h1>
 * 
 * In a real application, you don't want the client code to know 
 * about MacFactory or WindowsFactory. 
 * 
 * Instead, you use a Factory Provider to abstract away the "Which Factory?" 
 * logic. This is often called a "Factory of Factories" (Simple Factory returning 
 * an Abstract Factory).
 */
public class FactoryProvider {

    /**
     * Picks the right factory based on the OS or configuration.
     * This is a "Pragmatic" implementation that simplifies client usage.
     */
    public static Optional<IGUIFactory> getFactory(String osType) {
        if (osType == null) return Optional.empty();

        switch (osType.toLowerCase()) {
            case "windows":
                return Optional.of(new WindowsFactory());
            case "mac":
            case "macos":
                return Optional.of(new MacFactory());
            default:
                // Log warning and return empty/default
                System.err.println("Unsupported OS: " + osType);
                return Optional.empty();
        }
    }
}
