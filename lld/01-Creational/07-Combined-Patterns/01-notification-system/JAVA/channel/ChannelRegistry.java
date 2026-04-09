package channel;

import java.util.EnumMap;
import java.util.Map;

/**
 * <h1>ChannelRegistry — Channel Dispatcher</h1>
 *
 * <p>Acts as the lookup table for factories. Clients pass a {@code ChannelType}
 * enum constant and receive back the correct {@link INotificationChannel}.
 *
 * <p>Uses an {@link EnumMap} for O(1) lookup — the same production-grade
 * technique used in Simple Factory's production implementation.
 */
public class ChannelRegistry {

    public enum ChannelType { EMAIL, SMS, PUSH }

    private static final Map<ChannelType, IChannelFactory> REGISTRY
        = new EnumMap<>(ChannelType.class);

    static {
        REGISTRY.put(ChannelType.EMAIL, new EmailChannelFactory());
        REGISTRY.put(ChannelType.SMS,   new SmsChannelFactory());
        REGISTRY.put(ChannelType.PUSH,  new PushChannelFactory());
    }

    /**
     * Returns a freshly created channel for the given type.
     * Note: In a high-throughput system you'd cache channels here.
     */
    public static INotificationChannel getChannel(ChannelType type) {
        IChannelFactory factory = REGISTRY.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for: " + type);
        }
        return factory.createChannel();
    }
}
