package dependency_inversion;

/**
 * <h1>DIP: The "Ownership" Principle (SDE-2+ Level)</h1>
 * 
 * Robert C. Martin's definition: 
 * 1. High-level modules should not depend on low-level modules. Both should depend on abstractions.
 * 2. Abstractions should not depend on details. Details should depend on abstractions.
 * 
 * 🛡️ The Senior Secret: "He who defines the interface, owns the relationship."
 * High-level Policy defines the Interface. 
 * Low-level Detail implements it.
 */

// --- HIGH-LEVEL POLICY (The Domain) ---

/**
 * This interface is OWNED by the NotificationService. 
 * It defines WHAT the service needs to send a message.
 */
interface MessageSender {
    void send(String to, String body);
}

class NotificationService {
    private final MessageSender sender;

    // DIP in action: Dependency is INJECTED via the interface
    public NotificationService(MessageSender sender) {
        this.sender = sender;
    }

    public void sendAlert(String user, String message) {
        // High-level logic doesn't care if it's SMS, Email, or Carrier Pigeon
        sender.send(user, "ALERT: " + message);
    }
}

// --- LOW-LEVEL DETAILS (The Infrastructure) ---

class SendGridEmailSender implements MessageSender {
    @Override
    public void send(String to, String body) {
        System.out.println("Emailing via SendGrid to " + to + ": " + body);
    }
}

class TwilioSmsSender implements MessageSender {
    @Override
    public void send(String to, String body) {
        System.out.println("SMSing via Twilio to " + to + ": " + body);
    }
}

/**
 * 🎓 SDE-2+ INSIGHT:
 * DIP enables "Pluggable" architecture. 
 * The Domain (NotificationService) is stable and tested. 
 * The Infrastructure (SendGrid/Twilio) is a "Detail" that can be swapped.
 */
public class DIPPragmaticSDE2 {
    public static void main(String[] args) {
        // Production wiring
        NotificationService service = new NotificationService(new TwilioSmsSender());
        service.sendAlert("john_doe", "Your server is down!");

        // Testing wiring (easy to mock)
        NotificationService testService = new NotificationService((to, body) -> {
            System.out.println("Mock sender received: " + body);
        });
        testService.sendAlert("test_user", "Testing...");
        
        System.out.println("✅ DIP Refactored: Ownership Inversion achieved.");
    }
}
