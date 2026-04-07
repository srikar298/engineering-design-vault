package pizza;

/**
 * <h1>IPizzaStore — Abstract Factory (Creator)</h1>
 *
 * <p>Defines the "menu" contract. Each concrete store (Italian, American)
 * implements this to produce its own style of pizza.
 *
 * <p>Notice: the factory returns the same {@link Pizza} type but with
 * different configurations — this is Abstract Factory's core promise:
 * a consistent interface, brand-specific implementations.
 */
public interface IPizzaStore {
    Pizza createMargherita();
    Pizza createPepperoni();
    Pizza createVegDeluxe();
    String getStoreName();
}
