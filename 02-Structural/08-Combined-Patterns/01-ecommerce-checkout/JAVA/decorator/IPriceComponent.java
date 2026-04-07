package decorator;

/**
 * <h1>The Component Interface</h1>
 * 
 * <p>Both the raw Shopping Cart and the Pricing Decorators implement this.
 */
public interface IPriceComponent {
    double calculateTotal();
    String getPriceBreakdown();
}
