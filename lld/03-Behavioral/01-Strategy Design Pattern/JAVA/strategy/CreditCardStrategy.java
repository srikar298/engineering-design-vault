package strategy;

public class CreditCardStrategy implements IPaymentStrategy {
    private final String name;
    private final String cardNumber;

    public CreditCardStrategy(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Processing credit card payment of $" + amount + " for " + name + " (Card ends in " + cardNumber.substring(cardNumber.length() - 4) + ")");
        // Complex logic for interacting with Stripe/Visa gateway would go here
    }
}
