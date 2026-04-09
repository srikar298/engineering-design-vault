package abstract_factory.factories;

import abstract_factory.products.IButton;
import abstract_factory.products.ICheckbox;

/**
 * Abstract Factory
 */
public interface IGUIFactory {
    IButton createButton();
    ICheckbox createCheckbox();
}
