package observer;

public interface IStockPublisher {
    void registerSubscriber(IStockSubscriber subscriber);
    void unregisterSubscriber(IStockSubscriber subscriber);
    void notifySubscribers(String ticker, double price);
}
