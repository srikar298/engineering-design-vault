package vending;

public class DispensingState implements VendingMachineState {
    private final VendingMachine machine;

    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertMoney(double amount) {
        System.out.println("Dispensing in progress. Cannot insert money.");
    }

    @Override
    public void selectProduct(String name) {
        System.out.println("Dispensing in progress. Cannot select product.");
    }

    @Override
    public void dispense() {
        String name = machine.getSelectedProductName();
        Product p = machine.getProducts().get(name);
        if (p == null) {
            System.out.println("Error dispensing product.");
            machine.setSelectedProductName(null);
            machine.setState(machine.getIdleState());
            return;
        }

        int newCount = machine.getInventory().get(name) - 1;
        machine.getInventory().put(name, newCount);

        double change = machine.getBalance() - p.getPrice();
        System.out.println("✅ Dispensed: " + name + ". Change returned: $" + change);

        machine.setBalance(0);
        machine.setSelectedProductName(null);
        machine.setState(machine.getIdleState());
    }
}
