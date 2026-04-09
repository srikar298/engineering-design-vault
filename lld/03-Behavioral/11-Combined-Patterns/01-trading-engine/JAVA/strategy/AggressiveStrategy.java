package strategy;

public class AggressiveStrategy implements ITradingStrategy {

    @Override
    public String evaluate(String ticker, double price) {
        if (price < 50.0) {
            return "BUY_MAX"; // Buy aggressively if the dip is hard
        } else if (price > 150.0) {
            return "SELL_ALL";
        }
        return "HOLD";
    }
}
