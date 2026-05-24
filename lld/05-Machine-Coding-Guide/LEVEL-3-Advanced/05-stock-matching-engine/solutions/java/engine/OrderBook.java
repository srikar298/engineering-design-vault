package engine;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class OrderBook {
    private final String symbol;
    // Bids sorted descending (highest first)
    private final TreeMap<Double, LinkedList<Order>> bids = new TreeMap<>(Collections.reverseOrder());
    // Asks sorted ascending (lowest first)
    private final TreeMap<Double, LinkedList<Order>> asks = new TreeMap<>();
    // Lookup for active orders to support O(1) cancel checks
    private final Map<String, Order> activeOrders = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() { return symbol; }

    public List<Trade> submit(Order order) {
        lock.lock();
        try {
            List<Trade> trades = new ArrayList<>();
            if (order.getSide() == OrderSide.BUY) {
                matchBuyOrder(order, trades);
            } else {
                matchSellOrder(order, trades);
            }
            return trades;
        } finally {
            lock.unlock();
        }
    }

    public boolean cancel(String orderId) {
        lock.lock();
        try {
            Order order = activeOrders.get(orderId);
            if (order == null || order.isFilled()) {
                return false;
            }
            // Remove from the bids or asks queue
            TreeMap<Double, LinkedList<Order>> book = (order.getSide() == OrderSide.BUY) ? bids : asks;
            LinkedList<Order> queue = book.get(order.getPrice());
            if (queue != null) {
                queue.remove(order);
                if (queue.isEmpty()) {
                    book.remove(order.getPrice());
                }
            }
            activeOrders.remove(orderId);
            System.out.printf("[OrderBook - %s] Cancelled Order ID: %s\n", symbol, orderId);
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void matchBuyOrder(Order buyOrder, List<Trade> trades) {
        while (buyOrder.getRemainingQuantity() > 0 && !asks.isEmpty()) {
            double bestAskPrice = asks.firstKey();
            // For limit orders, we cannot buy if the ask price exceeds our limit price
            if (buyOrder.getType() == OrderType.LIMIT && buyOrder.getPrice() < bestAskPrice) {
                break;
            }

            LinkedList<Order> queue = asks.get(bestAskPrice);
            while (buyOrder.getRemainingQuantity() > 0 && !queue.isEmpty()) {
                Order sellOrder = queue.peekFirst();
                int matchQty = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
                
                // Fill both orders
                buyOrder.fill(matchQty);
                sellOrder.fill(matchQty);

                // Use the matching price (the price of the resting order in the book)
                double executionPrice = sellOrder.getPrice();
                trades.add(new Trade(symbol, executionPrice, matchQty, buyOrder.getId(), sellOrder.getId()));

                if (sellOrder.isFilled()) {
                    queue.pollFirst();
                    activeOrders.remove(sellOrder.getId());
                }
            }

            if (queue.isEmpty()) {
                asks.remove(bestAskPrice);
            }
        }

        // If the LIMIT order is not fully filled, add it to the bids book
        if (buyOrder.getRemainingQuantity() > 0 && buyOrder.getType() == OrderType.LIMIT) {
            bids.computeIfAbsent(buyOrder.getPrice(), k -> new LinkedList<>()).addLast(buyOrder);
            activeOrders.put(buyOrder.getId(), buyOrder);
        } else if (buyOrder.getRemainingQuantity() > 0 && buyOrder.getType() == OrderType.MARKET) {
            System.out.printf("[OrderBook - %s] Market order %s partially filled. Expiring remaining quantity: %d\n",
                    symbol, buyOrder.getId(), buyOrder.getRemainingQuantity());
        }
    }

    private void matchSellOrder(Order sellOrder, List<Trade> trades) {
        while (sellOrder.getRemainingQuantity() > 0 && !bids.isEmpty()) {
            double bestBidPrice = bids.firstKey();
            // For limit orders, we cannot sell if the bid price is less than our limit price
            if (sellOrder.getType() == OrderType.LIMIT && sellOrder.getPrice() > bestBidPrice) {
                break;
            }

            LinkedList<Order> queue = bids.get(bestBidPrice);
            while (sellOrder.getRemainingQuantity() > 0 && !queue.isEmpty()) {
                Order buyOrder = queue.peekFirst();
                int matchQty = Math.min(sellOrder.getRemainingQuantity(), buyOrder.getRemainingQuantity());

                // Fill both orders
                sellOrder.fill(matchQty);
                buyOrder.fill(matchQty);

                double executionPrice = buyOrder.getPrice();
                trades.add(new Trade(symbol, executionPrice, matchQty, buyOrder.getId(), sellOrder.getId()));

                if (buyOrder.isFilled()) {
                    queue.pollFirst();
                    activeOrders.remove(buyOrder.getId());
                }
            }

            if (queue.isEmpty()) {
                bids.remove(bestBidPrice);
            }
        }

        // If the LIMIT order is not fully filled, add it to the asks book
        if (sellOrder.getRemainingQuantity() > 0 && sellOrder.getType() == OrderType.LIMIT) {
            asks.computeIfAbsent(sellOrder.getPrice(), k -> new LinkedList<>()).addLast(sellOrder);
            activeOrders.put(sellOrder.getId(), sellOrder);
        } else if (sellOrder.getRemainingQuantity() > 0 && sellOrder.getType() == OrderType.MARKET) {
            System.out.printf("[OrderBook - %s] Market order %s partially filled. Expiring remaining quantity: %d\n",
                    symbol, sellOrder.getId(), sellOrder.getRemainingQuantity());
        }
    }

    // Helper for debugging/printing order book state
    public void printBook() {
        lock.lock();
        try {
            System.out.printf("--- Order Book for %s ---\n", symbol);
            System.out.println("ASKS (Sells):");
            asks.descendingMap().forEach((price, queue) -> {
                System.out.printf("  $%.2f -> Total Qty: %d (%d orders)\n",
                        price, queue.stream().mapToInt(Order::getRemainingQuantity).sum(), queue.size());
            });
            System.out.println("BIDS (Buys):");
            bids.forEach((price, queue) -> {
                System.out.printf("  $%.2f -> Total Qty: %d (%d orders)\n",
                        price, queue.stream().mapToInt(Order::getRemainingQuantity).sum(), queue.size());
            });
            System.out.println("------------------------\n");
        } finally {
            lock.unlock();
        }
    }
}
