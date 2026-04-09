/**
 * ============================================================================
 * ☣️ VIOLATION: The "Switch Trap" (OCP)
 * ============================================================================
 * 
 * SCENARIO:
 * A Notification Service that handles different channels.
 * 
 * WHY IS THIS A VIOLATION?
 * Every time we add a new channel (WhatsApp, Slack, Slack), we MUST modify 
 * this stable 'send' method. This increases the risk of breaking existing 
 * channels (Email/SMS).
 */
enum NotificationType {
    EMAIL, SMS, PUSH
}

public class NotificationViolation {
    public void send(String message, NotificationType type) {
        if (type == NotificationType.EMAIL) {
            System.out.println("Sending Email: " + message);
        } else if (type == NotificationType.SMS) {
            System.out.println("Sending SMS: " + message);
        } else if (type == NotificationType.PUSH) {
            System.out.println("Sending Push: " + message);
        }
        // SHOTGUN SURGERY: Adding WHATSAPP requires modifying this method.
    }

    public static void main(String[] args) {
        NotificationViolation service = new NotificationViolation();
        service.send("Hello!", NotificationType.EMAIL);
    }
}
