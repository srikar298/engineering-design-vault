import collection.IAmazonInventory;
import collection.WarehouseInventory;
import iterator.IIterator;
import model.Product;

/**
 * <h1>Iterator Pattern Demonstration</h1>
 * 
 * <p>Notice how the Main client loops over the inventory.
 * The client has ZERO IDEA if the inventory is a List, an Array, or a Tree!
 * It just uses `hasNext()` and `getNext()`.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Iterator Pattern: Amazon Inventory Demo        ");
        System.out.println("==================================================\n");

        IAmazonInventory inventory = new WarehouseInventory();
        inventory.addProduct(new Product("MacBook Pro", 2400.00));
        inventory.addProduct(new Product("AirPods", 200.00));
        inventory.addProduct(new Product("Magic Mouse", 80.00));

        System.out.println("--- Scenario: Iterating over hidden data structure ---");
        
        IIterator iterator = inventory.createIterator();
        
        double totalValue = 0;
        
        while (iterator.hasNext()) {
            Product p = iterator.getNext();
            System.out.println("   -> Found: " + p.getName() + " ($" + p.getPrice() + ")");
            totalValue += p.getPrice();
        }

        System.out.println("\nTotal Warehouse Value: $" + totalValue);
    }
}
