package splitwise;

import java.util.List;

public class PercentExpense extends Expense {
    public PercentExpense(String id, double amount, User paidBy, List<Split> splits) {
        super(id, amount, paidBy, splits);
    }

    @Override
    public boolean validate() {
        for (Split split : getSplits()) {
            if (!(split instanceof PercentSplit)) {
                return false;
            }
        }

        double totalPercent = 0;
        for (Split split : getSplits()) {
            PercentSplit percentSplit = (PercentSplit) split;
            totalPercent += percentSplit.getPercent();
        }

        return Math.abs(100.0 - totalPercent) < 0.01;
    }
}
