package abstract_factory.products;

/**
 * Concrete Product B1
 */
public class WindowsCheckbox implements ICheckbox {
    @Override
    public void paint() {
        System.out.println("You have created WindowsCheckbox.");
    }
}
