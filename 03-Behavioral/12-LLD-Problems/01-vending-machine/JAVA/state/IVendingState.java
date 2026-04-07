package state;

import machine.VendingMachine;

/**
 * <h1>The State Interface</h1>
 * 
 * <p>Defines actions a user can take against the Vending Machine.
 */
public interface IVendingState {
    void insertCoin(VendingMachine machine, double amount);
    void pressButton(VendingMachine machine);
    void dispense(VendingMachine machine);
}
