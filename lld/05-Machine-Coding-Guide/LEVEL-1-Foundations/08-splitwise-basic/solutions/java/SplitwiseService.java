package splitwise;

import java.util.*;

public class SplitwiseService {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Group> groups = new HashMap<>();
    // balanceSheet: UserA owes UserB -> Amount
    // represented as Map<String, Map<String, Double>>
    private final Map<String, Map<String, Double>> balanceSheet = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
        balanceSheet.putIfAbsent(user.getId(), new HashMap<>());
    }

    public void addGroup(Group group) {
        groups.put(group.getId(), group);
    }

    public void addExpense(ExpenseType expenseType, String paidByUserId, double amount, List<Split> splits) {
        User paidBy = users.get(paidByUserId);
        if (paidBy == null) {
            System.out.println("❌ User not found: " + paidByUserId);
            return;
        }

        // Generate unique expense ID
        String expenseId = "exp-" + UUID.randomUUID().toString().substring(0, 8);
        Expense expense = ExpenseFactory.createExpense(expenseType, expenseId, amount, paidBy, splits);

        if (!expense.validate()) {
            System.out.println("❌ Invalid expense split configuration!");
            return;
        }

        // Process expense: update balance sheet
        for (Split split : expense.getSplits()) {
            String owedByUser = split.getUser().getId();
            if (owedByUser.equals(paidByUserId)) continue;

            double splitAmount = split.getAmount();

            // Update how much 'owedByUser' owes 'paidByUserId'
            Map<String, Double> balances = balanceSheet.get(owedByUser);
            balances.put(paidByUserId, balances.getOrDefault(paidByUserId, 0.0) + splitAmount);

            simplify(owedByUser, paidByUserId);
        }
        System.out.println("✅ Added expense: " + expenseType + " of $" + amount + " paid by " + paidBy.getName());
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
        User user = users.get(userId);
        if (user == null) {
            System.out.println("❌ User not found: " + userId);
            return;
        }
        System.out.println("Balances for " + user.getName() + " (" + userId + "):");
        Map<String, Double> balances = balanceSheet.get(userId);
        boolean hasBalance = false;
        if (balances != null) {
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                if (entry.getValue() > 0) {
                    User owesTo = users.get(entry.getKey());
                    System.out.println("  - Owes " + (owesTo != null ? owesTo.getName() : entry.getKey()) + ": $" + entry.getValue());
                    hasBalance = true;
                }
            }
        }
        if (!hasBalance) {
            System.out.println("  - No balances.");
        }
    }

    public void showAllBalances() {
        System.out.println("Overall Balances:");
        boolean hasAnyBalance = false;
        for (String userA : balanceSheet.keySet()) {
            Map<String, Double> owes = balanceSheet.get(userA);
            for (Map.Entry<String, Double> entry : owes.entrySet()) {
                if (entry.getValue() > 0) {
                    User uA = users.get(userA);
                    User uB = users.get(entry.getKey());
                    System.out.println("  - " + (uA != null ? uA.getName() : userA) + " owes " + (uB != null ? uB.getName() : entry.getKey()) + ": $" + entry.getValue());
                    hasAnyBalance = true;
                }
            }
        }
        if (!hasAnyBalance) {
            System.out.println("  - No balances in the system.");
        }
    }
}
