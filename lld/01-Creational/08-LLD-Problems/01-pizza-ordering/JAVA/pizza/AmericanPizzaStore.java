package pizza;

import pizza.Pizza.Crust;
import pizza.Pizza.Size;

/**
 * <h1>AmericanPizzaStore — Concrete Factory B</h1>
 * Thick-crust, hearty toppings, extra cheese. New York style.
 */
public class AmericanPizzaStore implements IPizzaStore {

    @Override
    public Pizza createMargherita() {
        return new Pizza.Builder("Classic Cheese", Crust.THICK)
            .size(Size.LARGE)
            .topping("Tomato Sauce")
            .topping("Shredded Mozzarella")
            .extraCheese()
            .build();
    }

    @Override
    public Pizza createPepperoni() {
        return new Pizza.Builder("Meat Lovers Pepperoni", Crust.STUFFED)
            .size(Size.XL)
            .topping("Tangy Tomato Sauce")
            .topping("Pepperoni")
            .topping("Italian Sausage")
            .topping("Bacon Crumbles")
            .extraCheese()
            .build();
    }

    @Override
    public Pizza createVegDeluxe() {
        return new Pizza.Builder("Garden Supreme", Crust.THICK)
            .size(Size.LARGE)
            .topping("Tomato Sauce")
            .topping("Bell Peppers")
            .topping("Red Onion")
            .topping("Black Olives")
            .topping("Mushrooms")
            .topping("Banana Peppers")
            .extraCheese()
            .build();
    }

    @Override
    public String getStoreName() { return "🇺🇸 New York Pizza Co."; }
}
