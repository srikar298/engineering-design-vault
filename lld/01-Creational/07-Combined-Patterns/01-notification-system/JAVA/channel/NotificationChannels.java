package channel;

import message.NotificationMessage;
import config.NotificationConfig;

/**
 * <h1>Concrete Channel Implementations</h1>
 *
 * <p>Each class is a <b>Concrete Product</b> in the Factory Method pattern.
 * Constructors are <b>package-private</b> — clients cannot directly instantiate
 * these. They MUST use the factory. This enforces the Factory Method contract.
 */

// ─────────────────────────────────────────────
// Email Channel
// ─────────────────────────────────────────────
class EmailChannel implements INotificationChannel {

    // Package-private — only the factory (same package) can construct this
    EmailChannel() {
        String smtpHost = NotificationConfig.getInstance().getSmtpHost();
        System.out.println("[EmailChannel] Initialized. SMTP host: " + smtpHost);
    }

    @Override
    public void send(NotificationMessage message) {
        System.out.printf("[EMAIL] To: %-30s | Subject: %-20s | Priority: %s%n",
            message.getRecipient(),
            message.getSubject(),
            message.getPriority()
        );
    }

    @Override
    public String getChannelName() { return "EMAIL"; }
}

// ─────────────────────────────────────────────
// SMS Channel
// ─────────────────────────────────────────────
class SmsChannel implements INotificationChannel {

    SmsChannel() {
        String apiKey = NotificationConfig.getInstance().getSmsApiKey();
        System.out.println("[SmsChannel]   Initialized. Twilio key: " + apiKey.substring(0, 6) + "...");
    }

    @Override
    public void send(NotificationMessage message) {
        System.out.printf("[SMS]   To: %-30s | Body: %.40s%n",
            message.getRecipient(),
            message.getBody()
        );
    }

    @Override
    public String getChannelName() { return "SMS"; }
}

// ─────────────────────────────────────────────
// Push Notification Channel
// ─────────────────────────────────────────────
class PushChannel implements INotificationChannel {

    PushChannel() {
        String url = NotificationConfig.getInstance().getPushServiceUrl();
        System.out.println("[PushChannel]  Initialized. FCM endpoint: " + url);
    }

    @Override
    public void send(NotificationMessage message) {
        System.out.printf("[PUSH]  To: %-30s | Template: %-15s | Priority: %s%n",
            message.getRecipient(),
            message.getTemplateId() != null ? message.getTemplateId() : "DEFAULT",
            message.getPriority()
        );
    }

    @Override
    public String getChannelName() { return "PUSH"; }
}
