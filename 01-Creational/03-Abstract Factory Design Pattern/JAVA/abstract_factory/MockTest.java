package abstract_factory;

import abstract_factory.factories.IGUIFactory;
import abstract_factory.products.IButton;
import abstract_factory.products.ICheckbox;

/**
 * 10/10 Senior Implementation: Testing with Mocks.
 * 
 * Abstract Factory is a godsend for Unit Testing.
 * You can test your Application's business logic without needing real
 * Windows or Mac environments!
 */
public class MockTest {

    // Mock implementation of the Factory
    static class MockFactory implements IGUIFactory {
        boolean buttonCreated = false;
        boolean checkboxCreated = false;

        @Override
        public IButton createButton() {
            buttonCreated = true;
            return () -> System.out.println("Mock Button Painted");
        }

        @Override
        public ICheckbox createCheckbox() {
            checkboxCreated = true;
            return () -> System.out.println("Mock Checkbox Painted");
        }
    }

    public static void main(String[] args) {
        MockFactory mock = new MockFactory();
        
        // Test: Does Application correctly use the factory?
        Application app = new Application(mock);
        app.paint();

        if (mock.buttonCreated && mock.checkboxCreated) {
            System.out.println("TEST PASSED: Application successfully used the Abstract Factory!");
        } else {
            System.err.println("TEST FAILED: Application did not call the factory methods.");
        }
    }
}
