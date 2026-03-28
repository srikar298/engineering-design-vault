package abstract_factory.products;

/**
 * Concrete Product A1
 */
public class WindowsButton implements IButton {
    @Override
    public void paint() {
        System.out.println("You have created WindowsButton.");
    }
}
