package coupon;

public class CategoryDiscountDecorator extends DiscountDecorator {
    private final String category;
    private final double percentage;

    public CategoryDiscountDecorator(Cart decoratedCart, String category, double percentage) {
        super(decoratedCart);
        this.category = category;
        this.percentage = percentage;
    }

    @Override
    public double getTotal() {
        double currentTotal = decoratedCart.getTotal();
        double categoryTotal = 0;
        for (CartItem item : decoratedCart.getItems()) {
            if (item.getProduct().getCategory().equalsIgnoreCase(category)) {
                categoryTotal += item.getTotalPrice();
            }
        }
        double discount = (categoryTotal * percentage) / 100.0;
        return Math.max(0, currentTotal - discount);
    }
}
