import pizza.*;
import pizza.Pizza.Crust;
import pizza.Pizza.Size;

/**
 * <h1>Pizza Ordering System — Main Demo</h1>
 *
 * <p><b>Patterns at work:</b>
 * <ul>
 *   <li><b>Abstract Factory</b> — {@code IPizzaStore} gives each store its own
 *       product family. Swapping Italian → American changes the ENTIRE menu style.</li>
 *   <li><b>Builder</b>— Each store internally uses {@code Pizza.Builder} to construct
 *       pizzas with optional toppings, sizes, and flags.</li>
 * </ul>
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       🍕 Pizza Ordering System Demo              ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        // ── Abstract Factory: two different store "families" ──────────────────
        IPizzaStore italianStore  = new ItalianPizzaStore();
        IPizzaStore americanStore = new AmericanPizzaStore();

        // ── Each store creates the SAME menu items in its own style ───────────
        System.out.println("=== " + italianStore.getStoreName() + " ===");
        Pizza italianMarg = italianStore.createMargherita();
        Pizza italianPepp = italianStore.createPepperoni();
        System.out.println(italianMarg);
        System.out.println(italianPepp);

        System.out.println("\n=== " + americanStore.getStoreName() + " ===");
        Pizza americanMarg = americanStore.createMargherita();
        Pizza americanPepp = americanStore.createPepperoni();
        System.out.println(americanMarg);
        System.out.println(americanPepp);

        // ── Builder: customer creates a CUSTOM pizza ──────────────────────────
        System.out.println("\n=== 🧑‍🍳 Custom Order (Builder Direct) ===");
        Pizza customPizza = new Pizza.Builder("My Custom Create", Crust.STUFFED)
            .size(Size.XL)
            .topping("BBQ Sauce")
            .topping("Grilled Chicken")
            .topping("Red Onion")
            .topping("Jalapeños")
            .extraCheese()
            .build();
        System.out.println(customPizza);

        // ── OCP Proof: swap the entire store, zero client code changes ─────────
        System.out.println("\n=== 🔄 OCP Proof: same client code, different factory ===");
        orderFullMenu(italianStore);
        System.out.println();
        orderFullMenu(americanStore);
    }

    /** The client only depends on IPizzaStore — never on concrete stores */
    private static void orderFullMenu(IPizzaStore store) {
        System.out.println("--- " + store.getStoreName() + " Full Menu ---");
        System.out.println("  Margherita : " + store.createMargherita().getName());
        System.out.println("  Pepperoni  : " + store.createPepperoni().getName());
        System.out.println("  Veg Deluxe : " + store.createVegDeluxe().getName());
    }
}
