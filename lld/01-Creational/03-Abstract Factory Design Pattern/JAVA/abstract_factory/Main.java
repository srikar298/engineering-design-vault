package abstract_factory;

import abstract_factory.factories.IGUIFactory;
import abstract_factory.factories.MacFactory;
import abstract_factory.factories.WindowsFactory;

/**
 * Entry point. Here we decide which factory to use based on the environment.
 */
public class Main {

    /**
     * Application picks the factory type and creates it in run time (usually at
     * initialization stage), depending on the configuration or environment
     * variables.
     */
    private static Application configureApplication() {
        Application app;
        IGUIFactory factory;
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("mac")) {
            factory = new MacFactory();
        } else {
            factory = new WindowsFactory();
        }
        app = new Application(factory);
        return app;
    }

    public static void main(String[] args) {
        Application app = configureApplication();
        app.paint();
    }
}
