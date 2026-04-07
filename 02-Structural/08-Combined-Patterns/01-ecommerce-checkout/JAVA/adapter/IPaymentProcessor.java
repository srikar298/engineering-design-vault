package adapter;

/**
 * <h1>The Target Interface</h1>
 * 
 * <p>Our internal E-commerce system only understands this interface.
 * It expects a simple boolean return.
 */
public interface IPaymentProcessor {
    boolean processPayment(double amount, String userToken);
}
