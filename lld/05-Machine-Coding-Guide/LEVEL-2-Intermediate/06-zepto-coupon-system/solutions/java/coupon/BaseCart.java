package coupon;

import java.util.ArrayList;
import java.util.List;

public class BaseCart implements Cart {
    private final List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        items.add(item);
    }

    @Override
    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    @Override
    public double getTotal() {
        return getSubtotal();
    }

    @Override
    public List<CartItem> getItems() {
        return items;
    }
}
