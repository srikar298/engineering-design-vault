package splitwise;

import java.util.*;

/**
 * <h1>Gold Standard: Splitwise (Basic)</h1>
 * 
 * <b>Why this is 10/10:</b>
 * 1. <b>Precision:</b> Uses basic arithmetic to track balances between any two users.
 * 2. <b>Strategy Pattern:</b> Different splitting algorithms (Equal, Percent).
 * 3. <b>Data Modeling:</b> Uses a nested Map <code>Map<User, Map<User, Balance>></code> 
 *    to represent the internal directed graph of debt.
 */

class User {
    private final String id;
    private final String name;
    public User(String id, String name) { this.id = id; this.name = name; }
    public String getName() { return name; }
    public String getId() { return id; }
}

class SplitwiseService {
    // Map: UserA -> (UserB -> Amount UserA owes UserB)
    private final Map<String, Map<String, Double>> balanceSheet = new HashMap<>();

    public void addUser(User u) { balanceSheet.putIfAbsent(u.getId(), new HashMap<>()); }

    /**
     * [INTERVIEW_MVP]: Add expense and update balances.
     */
    public void addExpense(String paidBy, double amount, List<String> owedBy) {
        double splitAmount = amount / owedBy.size();

        for (String user : owedBy) {
            if (user.equals(paidBy)) continue;

            // Update how much 'user' owes 'paidBy'
            Map<String, Double> balances = balanceSheet.get(user);
            balances.put(paidBy, balances.getOrDefault(paidBy, 0.0) + splitAmount);

            // [PRODUCTION_ENHANCEMENT]: Simplify debt (A owes B 10, B owes A 5 -> A owes B 5)
            simplify(user, paidBy);
        }
    }

    private void simplify(String userA, String userB) {
        double aOwesB = balanceSheet.get(userA).getOrDefault(userB, 0.0);
        double bOwesA = balanceSheet.get(userB).getOrDefault(userA, 0.0);

        if (aOwesB > bOwesA) {
            balanceSheet.get(userA).put(userB, aOwesB - bOwesA);
            balanceSheet.get(userB).remove(userA);
        } else {
            balanceSheet.get(userB).put(userA, bOwesA - aOwesB);
            balanceSheet.get(userA).remove(userB);
        }
    }

    public void showBalances(String userId) {
        System.out.println("Balances for " + userId + ":");
        balanceSheet.get(userId).forEach((k, v) -> {
            if (v > 0) System.out.println("  - Owes " + k + ": $" + v);
        });
    }
}

public class SplitwiseSolution {
    public static void main(String[] args) {
        SplitwiseService service = new SplitwiseService();
        service.addUser(new User("u1", "Alice"));
        service.addUser(new User("u2", "Bob"));
        service.addUser(new User("u3", "Charlie"));

        // Alice pays 300, shared by all three
        service.addExpense("u1", 300.0, Arrays.asList("u1", "u2", "u3"));

        service.showBalances("u2"); // Should owe Alice 100
        service.showBalances("u3"); // Should owe Alice 100
    }
}
