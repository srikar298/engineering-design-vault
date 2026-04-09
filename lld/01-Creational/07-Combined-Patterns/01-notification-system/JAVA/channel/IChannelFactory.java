package channel;

/**
 * <h1>IChannelFactory — Creator Interface (Factory Method)</h1>
 *
 * <p>This is the Factory Method pattern's Creator interface.
 * Each concrete factory will create exactly one channel type.
 *
 * <p>Because the concrete channel constructors ({@code EmailChannel}, etc.)
 * are package-private, only factories <em>within this package</em> can
 * instantiate them. This enforces the architectural boundary.
 */
public interface IChannelFactory {
    INotificationChannel createChannel();
}
