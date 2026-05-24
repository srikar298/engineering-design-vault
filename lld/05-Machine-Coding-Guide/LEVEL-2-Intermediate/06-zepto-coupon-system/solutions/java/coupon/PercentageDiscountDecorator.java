package coupon;

public class PercentageDiscountDecorator extends DiscountDecorator {
    private final double percentage;
    private final double maxCap;

    public PercentageDiscountDecorator(Cart decoratedCart, double percentage, double maxCap) {
        super(decoratedCart);
        this.percentage = percentage;
        this.maxCap = maxCap;
    }

    @Override
    public double getTotal() {
        double currentTotal = decoratedCart.getTotal();
        double discount = (currentTotal * percentage) / 100.0;
        if (discount > maxCap) {
            discount = maxCap;
        }
        return Math.max(0, currentTotal - discount);
    }
}
