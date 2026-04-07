package abstraction;

/**
 * <h1>Bridge: The "Cartesian Killer" (SDE-2+ Level)</h1>
 * 
 * <b>Why this is Senior-Level:</b>
 * You are preventing "Class Explosion" (N message types x M platforms). 
 * By separating the Abstraction (Message Type) from the Implementation (Platform),
 * both can grow independently without creating hundreds of subclasses.
 * 
 * <b>Strategy:</b>
 * 1. Define the Bridge interface (Sender) and the Abstraction base for MVP.
 * 2. Implement runtime platform switching for Production.
 */

// --- IMPLEMENTOR (The Bridge Interface) ---
interface MessageSender { void send(String msg); }

class SmsSender implements MessageSender {
    @Override public void send(String msg) { System.out.println("Pushing SMS: " + msg); }
}

class EmailSender implements MessageSender {
    @Override public void send(String msg) { System.out.println("Sending Email: " + msg); }
}

// --- ABSTRACTION (The Policy) ---
abstract class Notification {
    // [INTERVIEW_MVP]: The composition reference (The Bridge)
    protected MessageSender sender;
    public Notification(MessageSender s) { this.sender = s; }
    public abstract void announce(String msg);
}

class UrgentNotification extends Notification {
    public UrgentNotification(MessageSender s) { super(s); }
    @Override public void announce(String msg) {
        // [PRODUCTION_ENHANCEMENT]: Custom logic per Abstraction
        sender.send("[URGENT]: " + msg);
    }
}

public class BridgePragmaticSDE2 {
    public static void main(String[] args) {
        // Runtime selection of platform (Bridge in action)
        Notification alert = new UrgentNotification(new SmsSender());
        alert.announce("System Overload!");
    }
}
