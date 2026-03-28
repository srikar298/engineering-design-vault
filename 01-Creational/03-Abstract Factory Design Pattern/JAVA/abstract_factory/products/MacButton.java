package abstract_factory.products;

/**
 * Concrete Product A2
 */
public class MacButton implements IButton {
    @Override
    public void paint() {
        System.out.println("You have created MacButton.");
    }
}
