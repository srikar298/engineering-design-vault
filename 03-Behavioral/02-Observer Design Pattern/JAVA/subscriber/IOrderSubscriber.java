package subscriber;

/**
 * <h1>The Subscriber (Observer) Interface</h1>
 * 
 * <p>All potential Observers must implement this interface. 
 * The Publisher only knows about this interface, preventing tight coupling.
 */
public interface IOrderSubscriber {
    void update(String orderId, String newStatus);
}
