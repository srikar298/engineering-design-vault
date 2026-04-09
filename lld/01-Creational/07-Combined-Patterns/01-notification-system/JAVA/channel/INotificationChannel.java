package channel;

import message.NotificationMessage;

/**
 * <h1>INotificationChannel — Product Interface</h1>
 *
 * <p>Part of the <b>Factory Method</b> layer.
 * All concrete channels (Email, SMS, Push) implement this contract.
 * The client only ever talks to this interface — never to the concrete class.
 */
public interface INotificationChannel {
    void send(NotificationMessage message);
    String getChannelName();
}
