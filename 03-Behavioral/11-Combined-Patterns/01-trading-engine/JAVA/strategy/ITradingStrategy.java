package strategy;

/**
 * <h1>Strategy Pattern: Trading Algorithm</h1>
 * 
 * <p>Standardizes how to evaluate a stock price and decide whether to 
 * Buy, Sell, or Hold.
 */
public interface ITradingStrategy {
    String evaluate(String ticker, double price);
}
