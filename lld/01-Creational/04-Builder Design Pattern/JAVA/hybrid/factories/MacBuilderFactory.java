package hybrid.factories;

import hybrid.builders.IButtonBuilder;
import hybrid.builders.MacButtonBuilder;

public class MacBuilderFactory implements IBuilderFactory {
    @Override
    public IButtonBuilder createButtonBuilder() {
        return new MacButtonBuilder();
    }
}
