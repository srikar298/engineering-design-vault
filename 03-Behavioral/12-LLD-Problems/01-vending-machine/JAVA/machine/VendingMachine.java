package machine;

import cor.CoinValidator;
import state.IVendingState;
import state.IdleState;

/**
 * <h1>The Context</h1>
 * 
 * <p>Holds the State and routes hardware-validated coins into the state machine.
 */
public class VendingMachine {
    private IVendingState state;
    private final CoinValidator coinSlot;
    private double currentBalance;
    private int inventoryCount;

    public VendingMachine(CoinValidator coinSlot, int initialInventory) {
        this.coinSlot = coinSlot;
        this.inventoryCount = initialInventory;
        this.currentBalance = 0;
        this.state = new IdleState(); // Initial State
    }

    public void setState(IVendingState state) { this.state = state; }
    
    public double getBalance() { return currentBalance; }
    public void addBalance(double amount) { this.currentBalance += amount; }
    public void resetBalance() { this.currentBalance = 0; }
    
    public int getInventory() { return inventoryCount; }
    public void decreaseInventory() { this.inventoryCount--; }

    // --- Actions ---

    public void insertCoin(double coinWeight) {
        // Step 1: Chain of Responsibility hardware check
        if (coinSlot.validate(coinWeight)) {
            // Step 2: State Machine processing
            state.insertCoin(this, coinWeight);
        } else {
            System.out.println("   ❌ [Hardware Error] Coin rejected! Spitting it back out.");
        }
    }

    public void pressButton() {
        state.pressButton(this);
        state.dispense(this);
    }
}
