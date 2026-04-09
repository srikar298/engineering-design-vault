package subscriber;

public class EmailService implements IOrderSubscriber {
    private final String customerEmail;

    public EmailService(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public void update(String orderId, String newStatus) {
        System.out.println("   [Email Service] 📧 Sending email to " + customerEmail + ": 'Your order " + orderId + " is now " + newStatus + ".'");
    }
}
