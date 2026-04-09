/**
 * ============================================================================
 * ✅ REFACTORED: Dependency Inversion (DIP)
 * ============================================================================
 * 
 * SOLUTION:
 * Use an abstraction (Interface) and Dependency Injection (Constructor).
 */

// 1. The Abstraction
interface MessageSender {
    void send(String msg);
}

// 2. Concrete Implementations (Low-Level Modules)
class EmailSender implements MessageSender {
    @Override
    public void send(String msg) {
        System.out.println("Sending Email: " + msg);
    }
}

class WhatsAppSender implements MessageSender {
    @Override
    public void send(String msg) {
        System.out.println("Sending WhatsApp: " + msg);
    }
}

// 3. The Orchestrator (High-Level Module)
class NotificationServiceRefactored {
    // ✅ DIP: Depends on Abstraction
    private final MessageSender sender;

    // ✅ DI: Injected from outside
    public NotificationServiceRefactored(MessageSender sender) {
        this.sender = sender;
    }

    public void notify(String message) {
        sender.send(message);
    }
}

public class DependencyRefactored {
    public static void main(String[] args) {
        // High-level logic stays the same, we just plug in the implementation
        
        MessageSender email = new EmailSender();
        NotificationServiceRefactored emailService = new NotificationServiceRefactored(email);
        emailService.notify("Hello via Email!");

        MessageSender wa = new WhatsAppSender();
        NotificationServiceRefactored waService = new NotificationServiceRefactored(wa);
        waService.notify("Hello via WhatsApp!");
    }
}
