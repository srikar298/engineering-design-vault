package hybrid.builders;

import hybrid.products.IButton;

public class WindowsButtonBuilder implements IButtonBuilder {
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
        return () -> System.out.println("[Windows Button] Label: " + label + ", Color: " + color);
    }
}
