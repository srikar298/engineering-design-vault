package state;

import machine.VendingMachine;

public class DispensingState implements IVendingState {

    public DispensingState() {
        System.out.println("   [State: DISPENSING] Motor running...");
    }

    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        System.out.println("      -> ❌ Denied. Machine is currently dispensing.");
    }

    @Override
    public void pressButton(VendingMachine machine) {
        System.out.println("      -> ❌ Ignored. Already dispensing.");
    }

    @Override
    public void dispense(VendingMachine machine) {
        if (machine.getInventory() > 0) {
            machine.decreaseInventory();
            System.out.println("      -> 🥤 Item Dispensed! Enjoy.");
            double change = machine.getBalance() - 0.25;
            if (change > 0) {
                System.out.println("      -> 💰 Returning change: $" + change);
            }
            machine.resetBalance();
            machine.setState(new IdleState());
        } else {
            System.out.println("      -> ❌ ITEM OUT OF STOCK! Refunding: $" + machine.getBalance());
            machine.resetBalance();
            machine.setState(new IdleState()); // Or OutOfStockState
        }
    }
}
