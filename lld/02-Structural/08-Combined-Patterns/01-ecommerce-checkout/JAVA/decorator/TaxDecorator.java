package decorator;

public class TaxDecorator extends PriceDecorator {

    private final double taxRate; // 0.08 for 8%

    public TaxDecorator(IPriceComponent wrappee, double taxRate) {
        super(wrappee);
        this.taxRate = taxRate;
    }

    @Override
    public double calculateTotal() {
        double subtotal = super.calculateTotal();
        return subtotal + (subtotal * taxRate);
    }

    @Override
    public String getPriceBreakdown() {
        return super.getPriceBreakdown() + "\n + State Tax (" + (taxRate * 100) + "%)";
    }
}
