package splitwise;

import java.util.List;

public class ExactExpense extends Expense {
    public ExactExpense(String id, double amount, User paidBy, List<Split> splits) {
        super(id, amount, paidBy, splits);
    }

    @Override
    public boolean validate() {
        for (Split split : getSplits()) {
            if (!(split instanceof ExactSplit)) {
                return false;
            }
        }

        double totalAmount = 0;
        for (Split split : getSplits()) {
            totalAmount += split.getAmount();
        }

        return Math.abs(getAmount() - totalAmount) < 0.01;
    }
}
