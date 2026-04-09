package hybrid.factories;

import hybrid.builders.IButtonBuilder;
import hybrid.builders.WindowsButtonBuilder;

public class WindowsBuilderFactory implements IBuilderFactory {
    @Override
    public IButtonBuilder createButtonBuilder() {
        return new WindowsButtonBuilder();
    }
}
