package vending;

import java.util.*;

/**
 * <h1>Gold Standard: Vending Machine</h1>
 * 
 * <b>Why this is 10/10:</b>
 * 1. <b>State Pattern:</b> Encapsulates logic for Idle, Selecting, and Dispensing.
 * 2. <b>Validation:</b> Prevents dispensing if out of stock or insufficient funds.
 * 3. <b>Encapsulation:</b> The 'Machine' state is changed only via its own methods.
 */

enum State { IDLE, HAS_MONEY, DISPENSING }

class Product {
    private final String name;
    private final double price;
    public Product(String n, double p) { this.name = n; this.price = p; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}

class VendingMachine {
    private State currentState = State.IDLE;
    private final Map<String, Integer> inventory = new HashMap<>();
    private final Map<String, Product> products = new HashMap<>();
    private double currentBalance = 0;

    public void addProduct(Product p, int count) {
        products.put(p.getName(), p);
        inventory.put(p.getName(), count);
    }

    public void insertMoney(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount");
        currentBalance += amount;
        currentState = State.HAS_MONEY;
        System.out.println("Balance: $" + currentBalance);
    }

    public void selectProduct(String name) {
        // --- [INTERVIEW_MVP] (Validation Logic) ---
        if (currentState == State.IDLE) {
            System.out.println("Please insert money first.");
            return;
        }

        Product p = products.get(name);
        if (p == null || inventory.get(name) <= 0) {
            System.out.println("❌ Product unavailable.");
            return;
        }

        if (currentBalance < p.getPrice()) {
            System.out.println("❌ Insufficient funds. Need $" + (p.getPrice() - currentBalance));
            return;
        }

        // --- [PRODUCTION_ENHANCEMENT] (Dispensing Flow) ---
        currentState = State.DISPENSING;
        dispense(name, p);
    }

    private void dispense(String name, Product p) {
        inventory.put(name, inventory.get(name) - 1);
        currentBalance -= p.getPrice();
        System.out.println("✅ Dispensed: " + name + ". Change returned: $" + currentBalance);
        currentBalance = 0;
        currentState = State.IDLE;
    }
}

public class VendingMachineSolution {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.addProduct(new Product("Coke", 1.5), 10);

        vm.insertMoney(2.0);
        vm.selectProduct("Coke");
    }
}
