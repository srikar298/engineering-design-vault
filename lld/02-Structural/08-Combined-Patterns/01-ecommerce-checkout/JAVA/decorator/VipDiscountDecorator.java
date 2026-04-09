package decorator;

public class VipDiscountDecorator extends PriceDecorator {

    private final double discountAmount;

    public VipDiscountDecorator(IPriceComponent wrappee, double discountAmount) {
        super(wrappee);
        this.discountAmount = discountAmount;
    }

    @Override
    public double calculateTotal() {
        // VIPs get a flat discount
        return super.calculateTotal() - discountAmount;
    }

    @Override
    public String getPriceBreakdown() {
        return super.getPriceBreakdown() + "\n - VIP Discount ($" + discountAmount + ")";
    }
}
