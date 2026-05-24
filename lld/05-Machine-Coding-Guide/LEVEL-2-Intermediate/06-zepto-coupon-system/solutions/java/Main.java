import coupon.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🏷️ Enterprise Pluggable Coupon & Discount System Demo 🏷️");
        System.out.println("=================================================\n");

        // 1. Initialize Products
        Product apples = new Product("Organic Fuji Apples", "Fruits", 3.00);
        Product headphones = new Product("Noise Cancelling Headphones", "Electronics", 100.00);
        Product milk = new Product("Whole Milk 1 Gallon", "Groceries", 4.00);

        // 2. Build Cart
        BaseCart rawCart = new BaseCart();
        rawCart.addItem(new CartItem(apples, 3));      // $9.00 in Fruits
        rawCart.addItem(new CartItem(headphones, 1));  // $100.00 in Electronics
        rawCart.addItem(new CartItem(milk, 2));        // $8.00 in Groceries

        System.out.println("--- 🛒 Cart Summary ---");
        System.out.printf("  Subtotal: $%.2f\n\n", rawCart.getSubtotal());

        // Scenario 1: Apply Single Coupon (10% off entire cart, max discount cap $15)
        System.out.println("--- 🏷️ Scenario 1: Apply 10% Off Coupon (Max Cap $15) ---");
        Cart singleDiscountCart = new PercentageDiscountDecorator(rawCart, 10.0, 15.0);
        System.out.printf("  Initial Subtotal:   $%.2f\n", singleDiscountCart.getSubtotal());
        System.out.printf("  Final Total Price:  $%.2f\n\n", singleDiscountCart.getTotal());

        // Scenario 2: Apply Category Discount Only
        System.out.println("--- 🏷️ Scenario 2: Apply 20% Off specifically on Fruits ---");
        Cart fruitDiscountCart = new CategoryDiscountDecorator(rawCart, "Fruits", 20.0);
        System.out.printf("  Fruit discount applied (20%% of $9.00 = $1.80)\n");
        System.out.printf("  Final Total Price:  $%.2f\n\n", fruitDiscountCart.getTotal());

        // Scenario 3: Coupon Stacking (Open/Closed Proof via Decorator nesting)
        // Order of application:
        // 1. Base Cart ($117.00)
        // 2. Apply 20% off on Fruits ($1.80 off -> $115.20)
        // 3. Apply Flat $15 off coupon (min order $100 -> $15.00 off -> $100.20)
        // 4. Apply 10% off coupon (max cap $8 -> 10% of $100.20 is $10.02, capped to $8.00 -> $92.20)
        System.out.println("--- 🏷️ Scenario 3: Coupon Stacking (Fruits 20% -> Flat $15 -> 10% Capped) ---");
        Cart stackedCart = rawCart;
        
        // Step A: Apply Category Fruits Discount
        stackedCart = new CategoryDiscountDecorator(stackedCart, "Fruits", 20.0);
        System.out.printf("  Step A (Fruits 20%% off) Total:        $%.2f\n", stackedCart.getTotal());

        // Step B: Apply Flat $15 Discount
        stackedCart = new FlatDiscountDecorator(stackedCart, 15.0, 100.0);
        System.out.printf("  Step B (Flat $15 off, Min $100) Total:  $%.2f\n", stackedCart.getTotal());

        // Step C: Apply Capped 10% Discount
        stackedCart = new PercentageDiscountDecorator(stackedCart, 10.0, 8.0);
        System.out.printf("  Step C (10%% off, Max Cap $8) Total:    $%.2f\n", stackedCart.getTotal());

        System.out.printf("\nSuccess! Final Stacked Total: $%.2f (Subtotal: $%.2f)\n", 
            stackedCart.getTotal(), stackedCart.getSubtotal());
    }
}
