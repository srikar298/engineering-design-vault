package hybrid.builders;

import hybrid.products.IButton;

public class MacButtonBuilder implements IButtonBuilder {
    private String label;
    private String color;

    @Override
    public IButtonBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public IButtonBuilder setColor(String color) {
        this.color = color;
        return this;
    }

    @Override
    public IButton build() {
        return () -> System.out.println("[Mac Button] Label: " + label + ", Color: " + color);
    }
}
