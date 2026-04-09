package decorator;

public class BaseCart implements IPriceComponent {
    
    private final double basePrice;

    public BaseCart(double basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public double calculateTotal() {
        return basePrice;
    }

    @Override
    public String getPriceBreakdown() {
        return "Base Cart Price: $" + basePrice;
    }
}
