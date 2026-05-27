package splitwise;

import java.util.List;

public abstract class Expense {
    private final String id;
    private final double amount;
    private final User paidBy;
    private final List<Split> splits;

    public Expense(String id, double amount, User paidBy, List<Split> splits) {
        this.id = id;
        this.amount = amount;
        this.paidBy = paidBy;
        this.splits = splits;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public abstract boolean validate();
}
