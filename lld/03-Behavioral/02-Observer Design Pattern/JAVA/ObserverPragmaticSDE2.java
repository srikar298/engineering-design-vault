package publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <h1>02 - Observer: The "Event-Driven" Foundation (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Real-time Stock Ticker. 
 * Multiple dashboards, mobile apps, and algorithmic traders must react instantly 
 * to price changes.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Concurrency Trap:</b> If you use <code>ArrayList</code> for subscribers, 
 *    adding/removing during a notification loop throws <code>ConcurrentModificationException</code>. 
 *    Use <b>CopyOnWriteArrayList</b> for thread-safety.
 * 2. <b>Push vs Pull:</b> Do you send the <i>entire</i> data object (Push), or just a 
 *    notification that "data changed" (Pull)? Push is faster but Pull is more decoupled.
 * 3. <b>Memory Leaks:</b> Always provide an <code>unsubscribe()</code> method. 
 *    In production, consider <b>WeakReferences</b> to prevent subscribers from being 
 *    stuck in memory.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Empty List:</b> Notifies zero subscribers gracefully.
 * - <b>Duplicate Subscriptions:</b> Ensures an observer isn't added twice.
 */

// --- OBSERVER INTERFACE (The Subscriber) ---
interface StockObserver {
    void onPriceChange(String ticker, double newPrice);
}

// --- SUBJECT INTERFACE (The Publisher) ---
interface StockPublisher {
    void register(StockObserver o);
    void unregister(StockObserver o);
    void notifyObservers();
}

// --- CONCRETE SUBJECT ---
class StockMarket implements StockPublisher {
    private final String ticker;
    private double price;
    
    // [PRODUCTION_ENHANCEMENT]: Thread-safe collection for 10k users
    private final List<StockObserver> observers = new CopyOnWriteArrayList<>();

    public StockMarket(String ticker, double initialPrice) {
        this.ticker = ticker;
        this.price = initialPrice;
    }

    @Override
    public void register(StockObserver o) { 
        if (!observers.contains(o)) observers.add(o); 
    }

    @Override
    public void unregister(StockObserver o) { observers.remove(o); }

    @Override
    public void notifyObservers() {
        // [INTERVIEW_MVP]: The Notification Loop
        for (StockObserver o : observers) {
            o.onPriceChange(ticker, price);
        }
    }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
        System.out.println("Market Update: " + ticker + " is now $" + price);
        notifyObservers();
    }
}

/**
 * 🎓 SDE-2+ READINESS CHECK:
 * - Difference between Observer and Pub/Sub? Observer is in-memory/direct. 
 *   Pub/Sub uses a Message Broker (Kafka/Redis) and is asynchronous.
 */
public class ObserverPragmaticSDE2 {
    public static void main(String[] args) {
        StockMarket apple = new StockMarket("AAPL", 150.0);

        // [INTERVIEW_MVP]: Dynamic registration
        StockObserver mobileApp = (t, p) -> System.out.println("📱 Mobile: " + t + " price alert: " + p);
        apple.register(mobileApp);

        // [PRODUCTION_ENHANCEMENT]: Multiple subscribers reacting independently
        apple.register((t, p) -> System.out.println("💻 Dashboard: New price for " + t + ": " + p));

        apple.updatePrice(155.5);
    }
}
