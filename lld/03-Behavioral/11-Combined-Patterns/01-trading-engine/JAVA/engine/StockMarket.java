package engine;

import observer.IStockPublisher;
import observer.IStockSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Observer: The Publisher (Context)</h1>
 * 
 * <p>Pushes stock market ticks to all registered Trading Bots.
 */
public class StockMarket implements IStockPublisher {
    private final List<IStockSubscriber> bots = new ArrayList<>();

    @Override
    public void registerSubscriber(IStockSubscriber subscriber) {
        bots.add(subscriber);
    }

    @Override
    public void unregisterSubscriber(IStockSubscriber subscriber) {
        bots.remove(subscriber);
    }

    @Override
    public void notifySubscribers(String ticker, double price) {
        for (IStockSubscriber bot : bots) {
            bot.updatePrice(ticker, price);
        }
    }

    /**
     * Simulates the market ticking forward.
     */
    public void executeTick(String ticker, double newPrice) {
        System.out.println("\n[MARKET EVENT] " + ticker + " is now trading at $" + newPrice);
        notifySubscribers(ticker, newPrice);
    }
}
