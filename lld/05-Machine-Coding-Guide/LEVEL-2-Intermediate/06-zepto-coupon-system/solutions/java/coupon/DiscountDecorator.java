package coupon;

import java.util.List;

public abstract class DiscountDecorator implements Cart {
    protected final Cart decoratedCart;

    public DiscountDecorator(Cart decoratedCart) {
        this.decoratedCart = decoratedCart;
    }

    @Override
    public double getSubtotal() {
        return decoratedCart.getSubtotal();
    }

    @Override
    public List<CartItem> getItems() {
        return decoratedCart.getItems();
    }
}
