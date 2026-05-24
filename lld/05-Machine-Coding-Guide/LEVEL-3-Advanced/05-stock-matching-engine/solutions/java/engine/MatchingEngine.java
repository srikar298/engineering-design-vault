package engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchingEngine {
    private static MatchingEngine instance;
    private final Map<String, OrderBook> books = new ConcurrentHashMap<>();

    private MatchingEngine() {}

    public static synchronized MatchingEngine getInstance() {
        if (instance == null) {
            instance = new MatchingEngine();
        }
        return instance;
    }

    public OrderBook getOrderBook(String symbol) {
        return books.computeIfAbsent(symbol, OrderBook::new);
    }

    public List<Trade> submitOrder(Order order) {
        OrderBook book = getOrderBook(order.getSymbol());
        return book.submit(order);
    }

    public boolean cancelOrder(String symbol, String orderId) {
        OrderBook book = books.get(symbol);
        if (book != null) {
            return book.cancel(orderId);
        }
        return false;
    }
}
