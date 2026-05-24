package engine;

import java.util.UUID;

public class Trade {
    private final String tradeId;
    private final String symbol;
    private final double price;
    private final int quantity;
    private final String buyerOrderId;
    private final String sellerOrderId;
    private final long timestamp;

    public Trade(String symbol, double price, int quantity, String buyerOrderId, String sellerOrderId) {
        this.tradeId = UUID.randomUUID().toString().substring(0, 8);
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTradeId() { return tradeId; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getBuyerOrderId() { return buyerOrderId; }
    public String getSellerOrderId() { return sellerOrderId; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Trade[ID: %s, Symbol: %s, Price: %.2f, Qty: %d, Buyer: %s, Seller: %s]",
                tradeId, symbol, price, quantity, buyerOrderId, sellerOrderId);
    }
}
