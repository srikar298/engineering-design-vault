import engine.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Low-Latency Stock Order Matching Engine Simulation ===");
        MatchingEngine engine = MatchingEngine.getInstance();
        String symbol = "AAPL";

        // 1. Get Ticker Flyweight Metadata
        OrderFlyweightFactory.TickerMetadata metadata = OrderFlyweightFactory.getMetadata(symbol);
        System.out.printf("Initialized symbol: %s (Tick Size: %.2f, Min Lot Size: %d)\n\n",
                metadata.getSymbol(), metadata.getTickSize(), metadata.getLotSize());

        // 2. Submit Limit Buy (Bid) orders
        System.out.println("--- Submitting Initial Buy Orders ---");
        // Bid 1: BUY LIMIT 100 AAPL @ $150.00
        Order o1 = new Order("buy-1", symbol, OrderSide.BUY, OrderType.LIMIT, 150.00, 100);
        engine.submitOrder(o1);

        // Bid 2: BUY LIMIT 150 AAPL @ $150.00 (same price, newer timestamp)
        Order o2 = new Order("buy-2", symbol, OrderSide.BUY, OrderType.LIMIT, 150.00, 150);
        engine.submitOrder(o2);

        // Bid 3: BUY LIMIT 200 AAPL @ $151.00 (higher price, should match first)
        Order o3 = new Order("buy-3", symbol, OrderSide.BUY, OrderType.LIMIT, 151.00, 200);
        engine.submitOrder(o3);

        // Print book state
        OrderBook book = engine.getOrderBook(symbol);
        book.printBook();

        // 3. Submit a Limit Sell (Ask) order - price matches bid level
        System.out.println("--- Submitting Limit Sell: SELL LIMIT 150 @ $150.00 ---");
        Order sell1 = new Order("sell-1", symbol, OrderSide.SELL, OrderType.LIMIT, 150.00, 150);
        List<Trade> trades1 = engine.submitOrder(sell1);
        printTrades(trades1);
        book.printBook();

        // 4. Submit a Market Sell (Ask) order
        System.out.println("--- Submitting Market Sell: SELL MARKET 150 ---");
        Order sell2 = new Order("sell-2", symbol, OrderSide.SELL, OrderType.MARKET, 0.0, 150);
        List<Trade> trades2 = engine.submitOrder(sell2);
        printTrades(trades2);
        book.printBook();

        // 5. Cancel remaining Buy Order 2
        System.out.println("--- Cancelling Order buy-2 ---");
        boolean cancelSuccess = engine.cancelOrder(symbol, "buy-2");
        System.out.printf("Cancel success: %s\n\n", cancelSuccess);
        book.printBook();

        System.out.println("=== Matching Engine Simulation Finished ===");
    }

    private static void printTrades(List<Trade> trades) {
        if (trades.isEmpty()) {
            System.out.println("  No matches executed.");
        } else {
            for (Trade t : trades) {
                System.out.println("  EXECUTION Match: " + t);
            }
        }
        System.out.println();
    }
}
