package channel;

/**
 * <h1>Concrete Channel Factories — Concrete Creators (Factory Method)</h1>
 *
 * <p>Each factory creates exactly one channel.
 * Being in the same package as the concrete channels, they
 * can legally call the package-private constructors.
 */

class EmailChannelFactory implements IChannelFactory {
    @Override
    public INotificationChannel createChannel() {
        return new EmailChannel();
    }
}

class SmsChannelFactory implements IChannelFactory {
    @Override
    public INotificationChannel createChannel() {
        return new SmsChannel();
    }
}

class PushChannelFactory implements IChannelFactory {
    @Override
    public INotificationChannel createChannel() {
        return new PushChannel();
    }
}
