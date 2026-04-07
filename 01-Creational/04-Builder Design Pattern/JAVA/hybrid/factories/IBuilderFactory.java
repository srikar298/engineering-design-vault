package hybrid.factories;

import hybrid.builders.IButtonBuilder;

/**
 * The Abstract Factory interface.
 * Instead of returning a Product, it returns a BUILDER.
 * This solves the "Family Selection" + "Complex Configuration" problem.
 */
public interface IBuilderFactory {
    IButtonBuilder createButtonBuilder();
}
