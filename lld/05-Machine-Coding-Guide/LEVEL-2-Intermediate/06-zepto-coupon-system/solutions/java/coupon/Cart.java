package coupon;

import java.util.List;

public interface Cart {
    double getSubtotal();
    double getTotal();
    List<CartItem> getItems();
}
