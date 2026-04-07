import cor.CoinValidator;
import cor.DimeValidator;
import cor.QuarterValidator;
import machine.VendingMachine;

/**
 * <h1>Combined Behavioral Patterns Demonstration</h1>
 * 
 * <p>Demonstrates State (Machine Workflow) + Chain of Responsibility (Hardware Filtering).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Vending Machine: State + CoR Demo              ");
        System.out.println("==================================================\n");

        // 1. Build the Hardware CoR Pipeline
        CoinValidator quarterSlot = new QuarterValidator();
        CoinValidator dimeSlot = new DimeValidator();
        quarterSlot.setNext(dimeSlot); // Quarter checks first, then Dime.

        // 2. Build the Machine (Inventory: 1 item)
        VendingMachine machine = new VendingMachine(quarterSlot, 1);

        System.out.println("\n--- Scenario 1: Buying a soda ---");
        machine.insertCoin(0.25); // Valid Quarter
        machine.pressButton();

        System.out.println("\n--- Scenario 2: Trying to buy when empty ---");
        machine.insertCoin(0.10); // Valid Dime
        machine.insertCoin(0.10); // Valid Dime
        machine.insertCoin(0.10); // Valid Dime
        machine.pressButton(); // OUT OF STOCK logic triggers

        System.out.println("\n--- Scenario 3: Inserting a fake slug coin ---");
        machine.insertCoin(0.99); // Invalid weight! Escapes the CoR chain.
    }
}
