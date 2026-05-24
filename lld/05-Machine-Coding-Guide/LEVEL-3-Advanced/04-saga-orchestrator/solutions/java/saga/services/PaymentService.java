package saga.services;

import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {
    // Simple mock balance tracking
    private final ConcurrentHashMap<String, Double> balances = new ConcurrentHashMap<>();

    public PaymentService() {
        balances.put("alice", 500.0);
    }

    public boolean charge(String user, double amount) {
        if (!balances.containsKey(user)) return false;
        double current = balances.get(user);
        if (current >= amount) {
            balances.put(user, current - amount);
            System.out.printf("[PaymentService] Charged %s $%.2f. Remaining balance: $%.2f\n", user, amount, current - amount);
            return true;
        }
        System.out.printf("[PaymentService] Failed charge: Insufficient funds for %s\n", user);
        return false;
    }

    public boolean refund(String user, double amount) {
        if (!balances.containsKey(user)) return false;
        double current = balances.get(user);
        balances.put(user, current + amount);
        System.out.printf("[PaymentService] Refunded %s $%.2f. New balance: $%.2f\n", user, amount, current + amount);
        return true;
    }
}
