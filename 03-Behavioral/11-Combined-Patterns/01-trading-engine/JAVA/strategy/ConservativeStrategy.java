package strategy;

public class ConservativeStrategy implements ITradingStrategy {

    @Override
    public String evaluate(String ticker, double price) {
        if (price < 100.0) {
            return "BUY_1_SHARE"; // Minimize risk
        } else if (price > 110.0) {
            return "SELL_1_SHARE"; // Secure tiny profits quickly
        }
        return "HOLD";
    }
}
