package builder;

import builder.builders.DesktopBuilder;
import builder.products.Desktop;

/**
 * The Director class. It controls the construction process.
 */
public class DesktopDirector {
    public Desktop buildDesktop(DesktopBuilder builder) {
        builder.buildMotherboard();
        builder.buildProcessor();
        builder.buildMemory();
        builder.buildStorage();
        builder.buildGraphicsCard();
        return builder.getDesktop();
    }
}
