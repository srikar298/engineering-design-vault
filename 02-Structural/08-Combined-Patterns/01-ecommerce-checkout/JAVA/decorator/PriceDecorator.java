package decorator;

public abstract class PriceDecorator implements IPriceComponent {
    protected final IPriceComponent wrappee;

    public PriceDecorator(IPriceComponent wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public double calculateTotal() {
        return wrappee.calculateTotal(); // Default delegation
    }

    @Override
    public String getPriceBreakdown() {
        return wrappee.getPriceBreakdown();
    }
}
