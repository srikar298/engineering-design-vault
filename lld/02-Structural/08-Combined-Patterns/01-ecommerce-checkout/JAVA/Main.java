import facade.CheckoutFacade;

/**
 * <h1>Combined Structural Patterns Demo</h1>
 * 
 * <p>Notice how unbelievably thin and clean the Client layer is!
 * The client knows absolutely nothing about:
 * - Inventory caching mechanisms (Proxy)
 * - Complex pricing math (Decorator)
 * - Third-party API translations (Adapter)
 * 
 * <p>All of that structural complexity is perfectly governed by the Facade.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   E-Commerce Checkout Engine (Structural Mix)    ");
        System.out.println("==================================================\n");

        CheckoutFacade api = new CheckoutFacade();

        // Scenario 1: First time checking an item (Cache Miss) + VIP User
        System.out.println(">>> User 1 Checkout <<<");
        api.processOrder("LAPTOP-X", 1, 1000.00, true, "TOKEN_BOB_123");

        System.out.println("\n--------------------------------------------------");

        // Scenario 2: Second time checking same item (Cache Hit) + Non-VIP User
        System.out.println("\n>>> User 2 Checkout <<<");
        api.processOrder("LAPTOP-X", 2, 1000.00, false, "TOKEN_ALICE_456");
    }
}
