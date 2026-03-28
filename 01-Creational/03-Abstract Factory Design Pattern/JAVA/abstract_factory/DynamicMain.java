package abstract_factory;

import abstract_factory.factories.IGUIFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 10/10 Senior Implementation: Dynamic Factory Loading.
 * 
 * Instead of hardcoding if/else checks for OS names, we load the factory
 * class name from a configuration file and instantiate it via Reflection.
 * 
 * This makes the system "Plug-and-Play" — you can add a new OS factory
 * without recompiling this Main class!
 */
public class DynamicMain {

    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            // Load configuration
            props.load(new FileInputStream("e:/job-hunt/LLD/LLD-Design-Patterns-main/01-Creational/03-Abstract Factory Design Pattern/JAVA/abstract_factory/config.properties"));
            String factoryClassName = props.getProperty("factory_class");

            // Reflection: Instantiate the factory by name
            Class<?> factoryClass = Class.forName(factoryClassName);
            IGUIFactory factory = (IGUIFactory) factoryClass.getDeclaredConstructor().newInstance();

            // Inject into Application
            Application app = new Application(factory);
            app.paint();

        } catch (IOException | ReflectiveOperationException e) {
            System.err.println("Critical Error: Could not initialize the UI Toolkit.");
            e.printStackTrace();
        }
    }
}
