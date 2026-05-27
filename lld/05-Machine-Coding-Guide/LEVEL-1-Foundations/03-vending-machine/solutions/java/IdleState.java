package vending;

public class IdleState implements VendingMachineState {
    private final VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        machine.addBalance(amount);
        machine.setState(machine.getHasMoneyState());
        System.out.println("Balance: $" + machine.getBalance());
    }

    @Override
    public void selectProduct(String name) {
        System.out.println("Please insert money first.");
    }

    @Override
    public void dispense() {
        System.out.println("Please insert money and select a product first.");
    }
}
