package abstract_factory.products;

/**
 * Concrete Product B2
 */
public class MacCheckbox implements ICheckbox {
    @Override
    public void paint() {
        System.out.println("You have created MacCheckbox.");
    }
}
