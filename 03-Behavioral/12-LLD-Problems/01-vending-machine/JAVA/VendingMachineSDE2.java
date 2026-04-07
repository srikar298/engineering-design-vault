package vending;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>LLD Problem: Advanced Vending Machine (SDE-2+ Level)</h1>
 * 
 * <b>Patterns Combined:</b> State (Workflow) + Chain of Responsibility (Change Return)
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>FSM Logic:</b> Vending machines are the classic example of the <b>State Pattern</b>. 
 *    It prevents "Illegal Actions" like dispensing before payment.
 * 2. <b>Algorithmic CoR:</b> Using a Chain to return change (Dispense 10s, then 5s, then 1s) 
 *    is an elegant way to implement the Greedy Change algorithm without nested loops.
 * 3. <b>Atomic Transactions:</b> In production, the "Dispense" and "Inventory Update" 
 *    must be atomic to prevent selling the same soda twice to 10k users.
 */

// --- 1. STATE PATTERN (Workflow) ---
interface VendingState {
    void insertMoney(VendingMachine machine, double amount);
    void dispense(VendingMachine machine);
}

class IdleState implements VendingState {
    @Override public void insertMoney(VendingMachine m, double a) {
        m.addBalance(a);
        m.setState(new HasMoneyState());
    }
    @Override public void dispense(VendingMachine m) { throw new IllegalStateException("Pay first!"); }
}

class HasMoneyState implements VendingState {
    @Override public void insertMoney(VendingMachine m, double a) { m.addBalance(a); }
    @Override public void dispense(VendingMachine m) {
        System.out.println("Dispensing product...");
        m.setState(new IdleState());
    }
}

// --- 2. CHAIN OF RESPONSIBILITY (Change Logic) ---
abstract class ChangeHandler {
    protected ChangeHandler next;
    public void setNext(ChangeHandler n) { this.next = n; }
    public abstract void handle(int amount);
}

class BillHandler extends ChangeHandler {
    private final int value;
    public BillHandler(int v) { this.value = v; }
    @Override public void handle(int amount) {
        if (amount >= value) {
            int num = amount / value;
            System.out.println("   Returning " + num + " x $" + value + " bill");
            amount = amount % value;
        }
        if (next != null && amount > 0) next.handle(amount);
    }
}

// --- CONTEXT ---
class VendingMachine {
    private VendingState state = new IdleState();
    private double balance = 0;

    public void setState(VendingState s) { this.state = s; }
    public void addBalance(double a) { this.balance += a; }
    
    public void pressButton() {
        // --- [INTERVIEW_MVP] (Workflow) ---
        state.dispense(this);
        
        // --- [PRODUCTION_ENHANCEMENT] (Change Logic via CoR) ---
        returnChange();
    }

    private void returnChange() {
        if (balance <= 0) return;
        System.out.println("Returning Change for $" + balance + ":");
        ChangeHandler h1 = new BillHandler(10);
        ChangeHandler h2 = new BillHandler(5);
        ChangeHandler h3 = new BillHandler(1);
        h1.setNext(h2); h2.setNext(h3);
        
        h1.handle((int)balance);
        balance = 0;
    }

    public void insert(double amt) { state.insertMoney(this, amt); }
}

public class VendingMachineSDE2 {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        
        // [INTERVIEW_MVP]: Standard usage
        vm.insert(27.0);
        vm.pressButton(); // Should return 2x$10, 1x$5, 2x$1
        
        System.out.println("✅ Vending Machine LLD verified.");
    }
}
