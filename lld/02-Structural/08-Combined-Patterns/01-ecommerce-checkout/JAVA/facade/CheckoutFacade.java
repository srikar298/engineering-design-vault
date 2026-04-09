package facade;

import adapter.IPaymentProcessor;
import adapter.StripeAdapter;
import decorator.BaseCart;
import decorator.IPriceComponent;
import decorator.TaxDecorator;
import decorator.VipDiscountDecorator;
import proxy.InventoryProxy;
import proxy.InventoryService;

/**
 * <h1>The Facade</h1>
 * 
 * <p>The client (e.g., Mobile App) only interacts with this class.
 * It hides the massive complexity of Inventory Proxies, Pricing Decorators, 
 * and Payment Adapters.
 */
public class CheckoutFacade {
    
    private final InventoryService inventoryAPI;
    private final IPaymentProcessor paymentAPI;

    public CheckoutFacade() {
        // Instantiate the complex subsystems
        // Note: we inject the Proxy, NOT the DatabaseService!
        this.inventoryAPI = new InventoryProxy();
        
        // Note: we inject the Adapter, NOT the Stripe API!
        this.paymentAPI = new StripeAdapter();
    }

    /**
     * The single, clean method exposed to the frontend.
     */
    public boolean processOrder(String itemId, int quantity, double basePrice, boolean isVip, String userToken) {
        System.out.println("\n--- [FACADE] Starting Checkout Process ---");

        // 1. Inventory Check (via Proxy)
        System.out.println("[Step 1] Verifying Inventory...");
        if (!inventoryAPI.isItemInStock(itemId, quantity)) {
            System.out.println("[Checkout Failed] Item out of stock.");
            return false;
        }

        // 2. Price Calculation (via Decorators)
        System.out.println("[Step 2] Calculating Final Price...");
        IPriceComponent cartObj = new BaseCart(basePrice * quantity);
        if (isVip) {
            cartObj = new VipDiscountDecorator(cartObj, 50.0); // $50 off for VIP
        }
        cartObj = new TaxDecorator(cartObj, 0.08); // 8% Tax added on top
        
        double finalPrice = cartObj.calculateTotal();
        System.out.println("\n" + cartObj.getPriceBreakdown());
        System.out.println("Final Total: $" + finalPrice);

        // 3. Process Payment (via Adapter)
        System.out.println("\n[Step 3] Processing Payment...");
        boolean paymentSuccess = paymentAPI.processPayment(finalPrice, userToken);
        
        if (!paymentSuccess) {
            System.out.println("[Checkout Failed] Payment declined.");
            return false;
        }

        System.out.println("\n[FACADE] ✨ Checkout Successful! Order Placed.");
        return true;
    }
}
