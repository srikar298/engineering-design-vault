package abstract_factory;

import abstract_factory.factories.IGUIFactory;
import abstract_factory.products.IButton;
import abstract_factory.products.ICheckbox;

/**
 * Client class. The Application picks the factory type and creates it in run
 * time (usually at initialization stage), depending on the configuration or
 * environment variables.
 *
 * Notice that the Application class is fully decoupled from concrete products
 * and concrete factories. It only works with interfaces.
 */
public class Application {
    private IButton button;
    private ICheckbox checkbox;

    public Application(IGUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void paint() {
        button.paint();
        checkbox.paint();
    }
}
