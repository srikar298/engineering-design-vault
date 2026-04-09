package abstract_factory.factories;

import abstract_factory.products.IButton;
import abstract_factory.products.ICheckbox;
import abstract_factory.products.WindowsButton;
import abstract_factory.products.WindowsCheckbox;

/**
 * Concrete Factory 1
 */
public class WindowsFactory implements IGUIFactory {
    @Override
    public IButton createButton() {
        return new WindowsButton();
    }

    @Override
    public ICheckbox createCheckbox() {
        return new WindowsCheckbox();
    }
}
