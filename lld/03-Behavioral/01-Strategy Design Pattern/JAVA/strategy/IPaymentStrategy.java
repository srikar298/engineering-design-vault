package strategy;

/**
 * <h1>The Strategy Interface</h1>
 * 
 * <p>Declares the algorithm/behavior that will be interchangeable at runtime.
 */
public interface IPaymentStrategy {
    void pay(double amount);
}
