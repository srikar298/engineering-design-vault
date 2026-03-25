/**
 * Demonstrates using Enums for State Machines and structured data.
 */
enum OrderStatus {
    PLACED, 
    CONFIRMED, 
    SHIPPED, 
    DELIVERED, 
    CANCELLED
}

// Rich Enum carrying external name and fee rules
enum PaymentMethod {
    CREDIT_CARD("Credit Card", 2.5),
    DEBIT_CARD("Debit Card", 1.0),
    UPI("UPI", 0.0),
    NET_BANKING("Net Banking", 1.5);

    private final String displayName;
    private final double feePercent;

    PaymentMethod(String displayName, double feePercent) {
        this.displayName = displayName;
        this.feePercent = feePercent;
    }

    public String getDisplayName() { return displayName; }
    public double getFeePercent() { return feePercent; }
}

class Order {
    private final String orderId;
    private OrderStatus status; // The State Machine pivot
    private final PaymentMethod paymentMethod;
    private final double amount;

    public Order(String orderId, PaymentMethod paymentMethod, double amount) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = OrderStatus.PLACED; // Initial state
    }

    // State Machine Transition
    public boolean advanceStatus() {
        switch (status) {
            case PLACED:
                status = OrderStatus.CONFIRMED;
                return true;
            case CONFIRMED:
                status = OrderStatus.SHIPPED;
                return true;
            case SHIPPED:
                status = OrderStatus.DELIVERED;
                return true;
            default:
                return false;
        }
    }

    public boolean cancel() {
        if (status == OrderStatus.PLACED || status == OrderStatus.CONFIRMED) {
            status = OrderStatus.CANCELLED;
            return true;
        }
        return false; // Can't cancel after shipping
    }

    public double getTotalWithFees() {
        return amount + (amount * paymentMethod.getFeePercent() / 100);
    }

    public void displayInfo() {
        System.out.printf("Order %s | Status: %s | Payment: %s | Amount: $%.2f (with fees: $%.2f)%n",
            orderId, status, paymentMethod.getDisplayName(), amount, getTotalWithFees());
    }
}

public class OrderProcessingSystem {
    public static void main(String[] args) {
        Order order = new Order("ORD-001", PaymentMethod.CREDIT_CARD, 99.99);
        order.displayInfo();

        order.advanceStatus(); // PLACED -> CONFIRMED
        order.advanceStatus(); // CONFIRMED -> SHIPPED
        order.displayInfo();

        System.out.println("Attempting to cancel after shipping: " + order.cancel()); // false
        order.displayInfo();
    }
}
