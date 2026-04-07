package state;

import machine.VendingMachine;

public class HasMoneyState implements IVendingState {

    public HasMoneyState() {
        System.out.println("   [State: HAS MONEY] Ready to Vend!");
    }

    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        machine.addBalance(amount);
        System.out.println("      -> Extra Coin accepted! Balance: $" + machine.getBalance());
    }

    @Override
    public void pressButton(VendingMachine machine) {
        System.out.println("      -> Button Pressed.");
        if (machine.getBalance() >= 0.25) { // Assuming item costs 25c
            machine.setState(new DispensingState());
        } else {
            System.out.println("      -> ❌ Insufficient funds. Item costs $0.25.");
        }
    }

    @Override
    public void dispense(VendingMachine machine) {
        // Must press button to dispense
    }
}
