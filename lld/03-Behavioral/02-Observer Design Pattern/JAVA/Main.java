import publisher.OnlineStore;
import subscriber.EmailService;
import subscriber.LogisticsDepartment;
import subscriber.MobileApp;

/**
 * <h1>Observer Pattern Demonstration</h1>
 * 
 * <p>Notice how we dynamically add and remove subscribers at runtime.
 * When the store's state changes, all subscribers are instantly notified 
 * without the store needing to hardcode method calls for Emails or Apps.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Observer Pattern: E-Commerce Pub/Sub Demo      ");
        System.out.println("==================================================\n");

        // 1. Create the Publisher
        OnlineStore store = new OnlineStore("ORD-999X");

        // 2. Create Subscribers
        EmailService emailSub = new EmailService("alice@example.com");
        MobileApp pushSub = new MobileApp("Device_iPhone_14");
        LogisticsDepartment warehouseSub = new LogisticsDepartment();

        // 3. Subscribe them to the Publisher
        store.subscribe(emailSub);
        store.subscribe(pushSub);
        store.subscribe(warehouseSub);

        // 4. Trigger State Changes
        System.out.println("--- Scenario 1: Payment Cleared ---");
        store.setStatus("PAYMENT_SUCCESS");

        System.out.println("\n--- Scenario 2: User unsubscribes from annoying Push notifications ---");
        store.unsubscribe(pushSub); // User turns off notifications
        
        System.out.println("\n--- Scenario 3: Order Shipped ---");
        store.setStatus("SHIPPED"); // Email & Warehouse will trigger, Mobile will NOT trigger
    }
}
