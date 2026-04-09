package abstract_factory.factories;

import abstract_factory.products.IButton;
import abstract_factory.products.ICheckbox;
import abstract_factory.products.MacButton;
import abstract_factory.products.MacCheckbox;

/**
 * Concrete Factory 2
 */
public class MacFactory implements IGUIFactory {
    @Override
    public IButton createButton() {
        return new MacButton();
    }

    @Override
    public ICheckbox createCheckbox() {
        return new MacCheckbox();
    }
}
