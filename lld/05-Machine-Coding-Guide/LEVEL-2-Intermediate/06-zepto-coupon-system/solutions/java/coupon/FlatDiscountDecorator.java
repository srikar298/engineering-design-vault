package coupon;

public class FlatDiscountDecorator extends DiscountDecorator {
    private final double discountAmount;
    private final double minOrderValue;

    public FlatDiscountDecorator(Cart decoratedCart, double discountAmount, double minOrderValue) {
        super(decoratedCart);
        this.discountAmount = discountAmount;
        this.minOrderValue = minOrderValue;
    }

    @Override
    public double getTotal() {
        double currentTotal = decoratedCart.getTotal();
        if (currentTotal >= minOrderValue) {
            return Math.max(0, currentTotal - discountAmount);
        }
        return currentTotal;
    }
}
