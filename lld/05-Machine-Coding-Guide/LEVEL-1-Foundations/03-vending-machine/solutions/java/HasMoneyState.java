package vending;

public class HasMoneyState implements VendingMachineState {
    private final VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        machine.addBalance(amount);
        System.out.println("Balance: $" + machine.getBalance());
    }

    @Override
    public void selectProduct(String name) {
        Product p = machine.getProducts().get(name);
        Integer count = machine.getInventory().get(name);
        if (p == null || count == null || count <= 0) {
            System.out.println("❌ Product unavailable.");
            return;
        }

        if (machine.getBalance() < p.getPrice()) {
            System.out.println("❌ Insufficient funds. Need $" + (p.getPrice() - machine.getBalance()));
            return;
        }

        machine.setSelectedProductName(name);
        machine.setState(machine.getDispensingState());
        machine.dispense();
    }

    @Override
    public void dispense() {
        System.out.println("Please select a product.");
    }
}
