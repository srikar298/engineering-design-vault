package state;

import machine.VendingMachine;

public class IdleState implements IVendingState {

    public IdleState() {
        System.out.println("   [State: IDLE] Waiting for coins...");
    }

    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        machine.addBalance(amount);
        System.out.println("      -> Coin accepted! Balance: $" + machine.getBalance());
        
        // Transition to HasMoneyState
        machine.setState(new HasMoneyState());
    }

    @Override
    public void pressButton(VendingMachine machine) {
        System.out.println("      -> ❌ Ignored. Please insert coins first.");
    }

    @Override
    public void dispense(VendingMachine machine) {
        // Impossible
    }
}
