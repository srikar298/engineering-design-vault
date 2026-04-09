package hybrid.builders;

import hybrid.products.IButton;

/**
 * Common interface for all Button Builders.
 * Notice how it handles the "Complexity" part (Label, Color).
 */
public interface IButtonBuilder {
    IButtonBuilder setLabel(String label);
    IButtonBuilder setColor(String color);
    IButton build();
}
