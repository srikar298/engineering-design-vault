package subscriber;

public class LogisticsDepartment implements IOrderSubscriber {
    
    @Override
    public void update(String orderId, String newStatus) {
        if (newStatus.equalsIgnoreCase("SHIPPED")) {
            System.out.println("   [Logistics] 🚚 Order " + orderId + " has shipped! Allocating tracking number...");
        } else {
            System.out.println("   [Logistics] 📦 Noted status change for " + orderId + " (" + newStatus + "). Preparing warehouse...");
        }
    }
}
