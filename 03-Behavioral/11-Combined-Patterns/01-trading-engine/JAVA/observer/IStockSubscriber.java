package observer;

public interface IStockSubscriber {
    void updatePrice(String ticker, double newPrice);
}
