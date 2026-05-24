package engine;

public class Order {
    private final String id;
    private final String symbol;
    private final OrderSide side;
    private final OrderType type;
    private final double price;
    private final int quantity;
    private int filledQuantity = 0;
    private final long timestamp;

    public Order(String id, String symbol, OrderSide side, OrderType type, double price, int quantity) {
        this.id = id;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = System.nanoTime(); // nanoTime for high-precision time priority
    }

    public String getId() { return id; }
    public String getSymbol() { return symbol; }
    public OrderSide getSide() { return side; }
    public OrderType getType() { return type; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getFilledQuantity() { return filledQuantity; }
    public long getTimestamp() { return timestamp; }

    public int getRemainingQuantity() {
        return quantity - filledQuantity;
    }

    public boolean isFilled() {
        return filledQuantity >= quantity;
    }

    public void fill(int qty) {
        if (qty > getRemainingQuantity()) {
            throw new IllegalArgumentException("Cannot fill more than remaining quantity.");
        }
        filledQuantity += qty;
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', side=%s, type=%s, price=%.2f, qty=%d, remaining=%d}",
                id, side, type, price, quantity, getRemainingQuantity());
    }
}
