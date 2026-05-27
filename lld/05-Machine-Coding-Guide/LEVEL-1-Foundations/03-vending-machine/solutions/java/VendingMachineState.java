package vending;

public interface VendingMachineState {
    void insertMoney(double amount);
    void selectProduct(String name);
    void dispense();
}
