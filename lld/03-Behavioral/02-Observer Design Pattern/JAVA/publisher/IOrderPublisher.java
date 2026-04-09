package publisher;

import subscriber.IOrderSubscriber;

/**
 * <h1>The Publisher (Subject) Interface</h1>
 * 
 * <p>Declares a set of methods for managing subscribers. 
 * The Publisher doesn't need to know the concrete classes of the Subscribers, 
 * just that they implement the IOrderSubscriber interface.
 */
public interface IOrderPublisher {
    void subscribe(IOrderSubscriber subscriber);
    void unsubscribe(IOrderSubscriber subscriber);
    void notifySubscribers();
}
