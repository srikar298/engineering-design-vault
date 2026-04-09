package hybrid;

import hybrid.builders.IButtonBuilder;
import hybrid.factories.IBuilderFactory;
import hybrid.factories.MacBuilderFactory;
import hybrid.factories.WindowsBuilderFactory;
import hybrid.products.IButton;

/**
 * 10/10 Senior Implementation: The Builder Factory Hybrid.
 * 
 * We use Abstract Factory to decide the OS Family.
 * We use Builder to configure the specific UI element.
 */
public class HybridMain {
    public static void main(String[] args) {
        // 1. Choose a Factory (Family Selection)
        IBuilderFactory factory = getFactoryByOS("Mac");

        // 2. The Factory gives us a Builder (Delegating Complexity)
        IButtonBuilder builder = factory.createButtonBuilder();

        // 3. Configure the product fluently
        IButton okButton = builder
                .setLabel("Confirm Purchase")
                .setColor("Green")
                .build();

        okButton.render();
    }

    private static IBuilderFactory getFactoryByOS(String os) {
        if (os.equalsIgnoreCase("Mac")) {
            return new MacBuilderFactory();
        } else {
            return new WindowsBuilderFactory();
        }
    }
}
