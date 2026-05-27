package splitwise;

import java.util.List;

public class ExpenseFactory {
    public static Expense createExpense(ExpenseType type, String id, double amount, User paidBy, List<Split> splits) {
        switch (type) {
            case EQUAL:
                int totalSplits = splits.size();
                double splitAmount = Math.round((amount / totalSplits) * 100.0) / 100.0;
                double firstAmount = amount - (splitAmount * (totalSplits - 1));
                for (int i = 0; i < totalSplits; i++) {
                    if (i == 0) {
                        splits.get(i).setAmount(firstAmount);
                    } else {
                        splits.get(i).setAmount(splitAmount);
                    }
                }
                return new EqualExpense(id, amount, paidBy, splits);
            case EXACT:
                return new ExactExpense(id, amount, paidBy, splits);
            case PERCENT:
                for (Split split : splits) {
                    PercentSplit percentSplit = (PercentSplit) split;
                    double amt = Math.round((amount * percentSplit.getPercent() / 100.0) * 100.0) / 100.0;
                    percentSplit.setAmount(amt);
                }
                return new PercentExpense(id, amount, paidBy, splits);
            default:
                throw new IllegalArgumentException("Unknown expense type");
        }
    }
}
