/**
 * ============================================================================
 * 🎭 POLYMORPHISM MASTERY: "Decoupling with Dynamic Dispatch"
 * ============================================================================
 */

// ----------------------------------------------------------------------------
// ❌ 1. THE JUNIOR APPROACH (Tight Coupling & Switch Statements)
// ----------------------------------------------------------------------------
class BadNotificationEngine {
    // Tightly coupled to specific types. Hard to maintain.
    public void alert(String type, String message) {
        System.out.println("[BAD ENGINE] Preparing alert...");
        
        switch (type) {
            case "EMAIL":
                System.out.println("Connecting to SMTP... EMAIL sent: " + message);
                break;
            case "SMS":
                System.out.println("Connecting to Telecom... SMS sent: " + message);
                break;
            default:
                throw new IllegalArgumentException("Unknown type!");
                // If we want to add "SLACK", we HAVE to modify this class.
                // Violates the Open-Closed Principle!
        }
    }
}


// ----------------------------------------------------------------------------
// ✅ 2. THE SENIOR APPROACH (Interfaces & Dynamic Dispatch)
// ----------------------------------------------------------------------------

// The Contract (The Universal Wall Outlet)
interface NotificationService {
    void send(String message);
}

// Concrete Implementations (The Appliances)
class EmailService implements NotificationService {
    @Override
    public void send(String msg) { 
        System.out.println("Connecting to SMTP... EMAIL sent: " + msg); 
    }
}

class SmsService implements NotificationService {
    @Override
    public void send(String msg) { 
        System.out.println("Connecting to Telecom... SMS sent: " + msg); 
    }
}

class PushService implements NotificationService {
    @Override
    public void send(String msg) { 
        System.out.println("Connecting to APNS/FCM... PUSH sent: " + msg); 
    }
}

// ----------------------------------------------------------------------------
// 🚀 HIGH-LEVEL ENGINE
// ----------------------------------------------------------------------------
class GoodNotificationEngine {
    // Decoupling: This class only knows about the Interface, NOT the concrete services.
    private final NotificationService service;

    public GoodNotificationEngine(NotificationService service) {
        this.service = service;
    }

    public void alert(String message) {
        System.out.println("[GOOD ENGINE] Preparing alert...");
        
        // Polymorphism magic: Dynamic Method Dispatch happens here.
        // The JVM figures out WHICH send() to call based on the Heap object.
        service.send(message); 
    }
}

public class PolymorphismMastery {
    public static void main(String[] args) {
        System.out.println("=== ❌ The Anti-Pattern ===");
        BadNotificationEngine badEngine = new BadNotificationEngine();
        badEngine.alert("EMAIL", "Server Down!");
        badEngine.alert("SMS", "Server Down!");


        System.out.println("\n=== ✅ Polymorphism & Decoupling ===");
        // UPCASTING: The reference is NotificationService, but the Heap object is EmailService.
        NotificationService myService = new EmailService();
        GoodNotificationEngine engine1 = new GoodNotificationEngine(myService);
        engine1.alert("Server Overheating!");

        // We can swap the backend without changing a SINGLE LINE inside GoodNotificationEngine.
        GoodNotificationEngine engine2 = new GoodNotificationEngine(new SmsService());
        engine2.alert("Critical Security Breach!");

        // Adding a new Push capability didn't require breaking open the Engine class!
        GoodNotificationEngine engine3 = new GoodNotificationEngine(new PushService());
        engine3.alert("Budget Exceeded (Minor)");
    }
}
