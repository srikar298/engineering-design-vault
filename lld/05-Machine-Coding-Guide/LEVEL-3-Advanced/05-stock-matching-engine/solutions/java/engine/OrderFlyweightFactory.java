package engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderFlyweightFactory {
    public static class TickerMetadata {
        private final String symbol;
        private final double tickSize;
        private final int lotSize;

        public TickerMetadata(String symbol, double tickSize, int lotSize) {
            this.symbol = symbol;
            this.tickSize = tickSize;
            this.lotSize = lotSize;
        }

        public String getSymbol() { return symbol; }
        public double getTickSize() { return tickSize; }
        public int getLotSize() { return lotSize; }
    }

    private static final Map<String, TickerMetadata> tickerCache = new ConcurrentHashMap<>();

    public static TickerMetadata getMetadata(String symbol) {
        return tickerCache.computeIfAbsent(symbol, s -> {
            // Default setup for popular symbols
            double tick = 0.01;
            int lot = 100;
            if (s.startsWith("BTC")) {
                tick = 0.1;
                lot = 1;
            }
            return new TickerMetadata(s, tick, lot);
        });
    }
}
