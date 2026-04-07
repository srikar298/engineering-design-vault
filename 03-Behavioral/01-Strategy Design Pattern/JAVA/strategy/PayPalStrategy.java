package strategy;

public class PayPalStrategy implements IPaymentStrategy {
    private final String emailId;

    public PayPalStrategy(String emailId) {
        this.emailId = emailId;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Processing PayPal payment of $" + amount + " using account: " + emailId);
        // Complex OAuth logic and PayPal API calls would go here
    }
}
