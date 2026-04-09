package object_adapter;

/**
 * <h1>01 - Adapter: The "Anti-Corruption Layer" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> Your internal system uses a clean <code>NotificationProvider</code> interface. 
 * You need to integrate <b>Twilio</b>. Twilio's SDK uses different field names (e.g. 'Body' 
 * vs 'Message') and error codes.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>ACL Boundary:</b> The Adapter acts as an Anti-Corruption Layer. It prevents 
 *    Twilio-specific logic from leaking into your clean Business Domain.
 * 2. <b>Composition over Inheritance:</b> Use Object Adapter. It is more flexible 
 *    and allows you to swap Twilio SDK versions without changing the Adapter interface.
 * 3. <b>Pluggable Architecture:</b> This enables Hexagonal Architecture (Ports and Adapters).
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Format Translation:</b> Maps domain objects to Twilio-specific request objects.
 * - <b>Error Mapping:</b> Translates 3rd party raw exceptions into meaningful domain exceptions.
 * - <b>Sanitization:</b> Cleans inputs before passing them to the external SDK.
 */

// --- TARGET (Our Clean Domain Interface) ---
interface MessageSender {
    void sendNotification(String userId, String text);
}

// --- ADAPTEE (The Messy 3rd Party SDK) ---
class TwilioSDK {
    public void pushSMS(String phoneWithCode, String bodyContent) {
        System.out.println("Twilio SDK: Pushing '" + bodyContent + "' to " + phoneWithCode);
    }
}

// --- THE PRAGMATIC ADAPTER ---
class TwilioAdapter implements MessageSender {
    private final TwilioSDK twilio;

    public TwilioAdapter(TwilioSDK sdk) { this.twilio = sdk; }

    @Override
    public void sendNotification(String userId, String text) {
        // --- [INTERVIEW_MVP] (Basic Mapping) ---
        String body = text;

        // --- [PRODUCTION_ENHANCEMENT] (ACL: Translation & Security) ---
        // 1. Data Lookup: Map internal ID to Phone Number
        String phoneNumber = lookupPhone(userId);
        
        // 2. Format Sanitization
        String sanitizedText = body.trim().substring(0, Math.min(body.length(), 160));
        
        // 3. Delegate to messy SDK
        twilio.pushSMS(phoneNumber, sanitizedText);
    }

    private String lookupPhone(String id) { return "+1-555-0199"; } // Simulation
}

public class AdapterPragmaticSDE2 {
    public static void main(String[] args) {
        MessageSender sender = new TwilioAdapter(new TwilioSDK());
        sender.sendNotification("USER_123", "Your OTP is 4432");
    }
}
