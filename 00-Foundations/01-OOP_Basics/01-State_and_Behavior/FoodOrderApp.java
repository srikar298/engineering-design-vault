import java.util.ArrayList;
import java.util.List;

class FoodOrder {
    private String orderId;
    private String customerName;
    private List<String> items;
    private double totalAmount;
    private boolean isPlaced;

    public FoodOrder(String orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        this.isPlaced = false;
    }

    // Only allows adding items before the order is placed
    public void addItem(String name, double price) {
        if (isPlaced) {
            System.out.println("Cannot modify a placed order.");
            return;
        }
        items.add(name);
        totalAmount += price;
    }

    // Places the order if it has at least one item and hasn't been placed yet
    public boolean placeOrder() {
        if (isPlaced || items.isEmpty()) {
            return false;
        }
        isPlaced = true;
        return true;
    }

    public int getItemCount() {
        return items.size();
    }

    public void displayOrder() {
        String status = isPlaced ? "PLACED" : "PENDING";
        System.out.println("Order " + orderId + " (" + customerName + ") - " + status);
        for (String item : items) {
            System.out.println("  - " + item);
        }
        System.out.printf("  Total: $%.2f%n", totalAmount);
    }
}

// Usage
public class FoodOrderApp {
    public static void main(String[] args) {
        FoodOrder order1 = new FoodOrder("ORD-101", "Alice");
        order1.addItem("Pizza", 12.99);
        order1.addItem("Garlic Bread", 4.99);
        order1.addItem("Coke", 2.49);
        order1.placeOrder();

        FoodOrder order2 = new FoodOrder("ORD-102", "Bob");
        order2.addItem("Burger", 9.99);
        order2.addItem("Fries", 3.99);
        // Bob hasn't placed his order yet

        order1.displayOrder();
        System.out.println();
        order2.displayOrder();
    }
}
