/**
 * ThisKeywordDemo Deep Dive
 * 
 * Demonstrates:
 * 1. Shadowing (using 'this' to resolve name conflicts)
 * 2. Fluent Chaining (returning 'this')
 * 3. Passing 'this' (sharing identity with outer services)
 */
public class ThisKeywordDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. Shadowing Demo ===");
        Employee emp = new Employee();
        emp.setName("John Doe"); 
        System.out.println("Employee Name: " + emp.getName());

        System.out.println("\n=== 2. Fluent Chaining Demo ===");
        // Method chaining enabled by 'return this'
        Order order = new Order()
                        .setOrderId("ORD-123")
                        .setAmount(500.0)
                        .setCurrency("USD");
        
        System.out.println("Order created: " + order.getOrderId() + " for " + order.getAmount() + " " + order.getCurrency());

        System.out.println("\n=== 3. Passing 'this' Demo ===");
        LoggerService logger = new LoggerService();
        order.process(logger); // Order passes itself ('this') to the logger
    }
}

/**
 * 1. Shadowing / Disambiguation
 */
class Employee {
    private String name;

    public void setName(String name) {
        // Without 'this.', the compiler would think we are assigning 
        // the parameter 'name' into the parameter 'name' (doing nothing).
        this.name = name; 
    }

    public String getName() {
        return this.name;
    }
}

/**
 * 2. Fluent API / Returning 'this'
 */
class Order {
    private String orderId;
    private double amount;
    private String currency;

    public Order setOrderId(String orderId) {
        this.orderId = orderId;
        return this; // Return the CURRENT object reference
    }

    public Order setAmount(double amount) {
        this.amount = amount;
        return this; // Enables chaining
    }

    public Order setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }

    /**
     * 3. Passing 'this' to an external service
     */
    public void process(LoggerService service) {
        // We pass 'this' (the current object) so the service can read our data
        service.logOrderDetails(this); 
    }
}

class LoggerService {
    public void logOrderDetails(Order order) {
        System.out.println("[LOG] Processing Order ID: " + order.getOrderId());
    }
}

// ⚠️ INTERVIEW TRAP: Un-commenting this will cause a compiler error
/*
class StaticTrap {
    private String data = "Secret";

    public static void staticMethod() {
        // System.out.println(this.data); // Error: non-static variable this cannot be referenced from a static context
    }
}
*/
