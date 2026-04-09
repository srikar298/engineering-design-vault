/**
 * ============================================================================
 * ☣️ VIOLATION: Hard-Coded Dependency (DIP)
 * ============================================================================
 * 
 * SCENARIO: 
 * A 'NotificationService' that is tightly coupled to 'SmsSender'.
 */

class SmsSender {
    public void send(String msg) {
        System.out.println("Sending SMS: " + msg);
    }
}

// ❌ VIOLATION: NotificationService depends on a CONCRETE class
class NotificationService {
    private final SmsSender sender = new SmsSender();

    public void notify(String message) {
        // Business logic is tangled with the implementation
        sender.send(message);
    }
}

public class DependencyViolation {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        service.notify("Your order has been shipped!");
    }
}
/**
 * SENIOR INSIGHT:
 * Because of the 'new' keyword inside NotificationService, 
 * we CANNOT test this service without sending a real SMS.
 * We also CANNOT use Email or WhatsApp without modifying this file.
 */
