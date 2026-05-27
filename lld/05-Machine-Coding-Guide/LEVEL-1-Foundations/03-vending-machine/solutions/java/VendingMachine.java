package vending;

import java.util.HashMap;
import java.util.Map;

public class VendingMachine {
    private final VendingMachineState idleState;
    private final VendingMachineState hasMoneyState;
    private final VendingMachineState dispensingState;

    private VendingMachineState currentState;
    private final Map<String, Integer> inventory = new HashMap<>();
    private final Map<String, Product> products = new HashMap<>();
    private double currentBalance = 0;
    private String selectedProductName;

    public VendingMachine() {
        this.idleState = new IdleState(this);
        this.hasMoneyState = new HasMoneyState(this);
        this.dispensingState = new DispensingState(this);
        this.currentState = idleState;
    }

    public void addProduct(Product p, int count) {
        products.put(p.getName(), p);
        inventory.put(p.getName(), count);
    }

    public VendingMachineState getIdleState() {
        return idleState;
    }

    public VendingMachineState getHasMoneyState() {
        return hasMoneyState;
    }

    public VendingMachineState getDispensingState() {
        return dispensingState;
    }

    public void setState(VendingMachineState state) {
        this.currentState = state;
    }

    public VendingMachineState getCurrentState() {
        return currentState;
    }

    public double getBalance() {
        return currentBalance;
    }

    public void addBalance(double amount) {
        this.currentBalance += amount;
    }

    public void setBalance(double balance) {
        this.currentBalance = balance;
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public String getSelectedProductName() {
        return selectedProductName;
    }

    public void setSelectedProductName(String name) {
        this.selectedProductName = name;
    }

    // Delegates to the current state
    public void insertMoney(double amount) {
        currentState.insertMoney(amount);
    }

    public void selectProduct(String name) {
        currentState.selectProduct(name);
    }

    public void dispense() {
        currentState.dispense();
    }
}
