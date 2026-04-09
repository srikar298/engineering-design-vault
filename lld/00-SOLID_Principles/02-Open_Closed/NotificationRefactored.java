/**
 * ============================================================================
 * ✅ REFACTORED: Open for Extension (OCP)
 * ============================================================================
 * 
 * SOLUTION:
 * Define an interface for the behavior and use Polymorphism.
 * 
 * THE SENIOR INSIGHT:
 * To add a new channel (e.g., WhatsApp), we create a NEW CLASS. 
 * We don't touch the NotificationService or any existing code.
 */

interface NotificationChannel {
    void send(String message);
}

class EmailChannel implements NotificationChannel {
    public void send(String message) {
        System.out.println("Sending Email: " + message);
    }
}

class SmsChannel implements NotificationChannel {
    public void send(String message) {
        System.out.println("Sending SMS: " + message);
    }
}

// --- NEW EXTENSION (Zero changes to existing logic) ---
class WhatsAppChannel implements NotificationChannel {
    public void send(String message) {
        System.out.println("Sending WhatsApp: " + message);
    }
}

class NotificationService {
    // The service is now CLOSED for modification but OPEN for extension
    public void notify(String message, NotificationChannel channel) {
        channel.send(message);
    }
}

public class NotificationRefactored {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        
        // We can plug in any channel at runtime
        service.notify("Hello via Email!", new EmailChannel());
        service.notify("Hello via WhatsApp!", new WhatsAppChannel());
    }
}
