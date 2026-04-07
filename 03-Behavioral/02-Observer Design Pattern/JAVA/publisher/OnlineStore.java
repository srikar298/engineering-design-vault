package publisher;

import subscriber.IOrderSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>The Real Publisher</h1>
 * 
 * <p>Maintains state (the order status) and notifies all subscribers 
 * whenever the state changes.
 */
public class OnlineStore implements IOrderPublisher {

    private final String orderId;
    private String status;
    private final List<IOrderSubscriber> subscribers;

    public OnlineStore(String orderId) {
        this.orderId = orderId;
        this.status = "CREATED";
        this.subscribers = new ArrayList<>();
    }

    @Override
    public void subscribe(IOrderSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(IOrderSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers() {
        for (IOrderSubscriber sub : subscribers) {
            sub.update(orderId, status);
        }
    }

    /**
     * Business logic method. When the state changes, we automatically trigger notification.
     */
    public void setStatus(String newStatus) {
        System.out.println("\n[Online Store] Order '" + orderId + "' status changed to: " + newStatus);
        this.status = newStatus;
        notifySubscribers();
    }
}
