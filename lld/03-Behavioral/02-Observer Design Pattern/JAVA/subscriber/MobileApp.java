package subscriber;

public class MobileApp implements IOrderSubscriber {
    private final String deviceId;

    public MobileApp(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public void update(String orderId, String newStatus) {
        System.out.println("   [Mobile App] 📱 Pushing notification to Device [" + deviceId + "]: 'Order " + orderId + " updated to " + newStatus + ".'");
    }
}
