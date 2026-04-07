package pizza;

import pizza.Pizza.Crust;
import pizza.Pizza.Size;

/**
 * <h1>ItalianPizzaStore — Concrete Factory A</h1>
 * Thin-crust, San Marzano tomatoes, fresh basil. Authentic Italian style.
 */
public class ItalianPizzaStore implements IPizzaStore {

    @Override
    public Pizza createMargherita() {
        return new Pizza.Builder("Margherita Classica", Crust.THIN)
            .size(Size.MEDIUM)
            .topping("San Marzano Tomato Sauce")
            .topping("Fresh Mozzarella")
            .topping("Fresh Basil")
            .vegan()
            .build();
    }

    @Override
    public Pizza createPepperoni() {
        return new Pizza.Builder("Diavola", Crust.THIN)
            .size(Size.LARGE)
            .topping("Spicy Tomato Sauce")
            .topping("Calabrese Pepperoni")
            .topping("Mozzarella Fior di Latte")
            .extraCheese()
            .build();
    }

    @Override
    public Pizza createVegDeluxe() {
        return new Pizza.Builder("Quattro Stagioni", Crust.THIN)
            .size(Size.LARGE)
            .topping("Tomato Base")
            .topping("Artichokes")
            .topping("Mushrooms")
            .topping("Black Olives")
            .topping("Roasted Peppers")
            .vegan()
            .build();
    }

    @Override
    public String getStoreName() { return "🇮🇹 Ristorante Italiano"; }
}
