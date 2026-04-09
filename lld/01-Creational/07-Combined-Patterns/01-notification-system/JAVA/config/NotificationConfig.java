package config;

/**
 * <h1>NotificationConfig — Singleton Layer</h1>
 *
 * <p>Uses the <b>Bill Pugh Initialization-on-Demand Holder</b> idiom.
 * Thread-safe, lock-free, and lazily initialized.
 *
 * <p>In production this would read from environment variables or a config file.
 * Here it acts as a shared, globally-accessible configuration store.
 */
public final class NotificationConfig {

    private final String smtpHost;
    private final String smsApiKey;
    private final String pushServiceUrl;
    private final boolean debugMode;

    // Private constructor — only the InstanceHolder can call this
    private NotificationConfig() {
        // In production: read from System.getenv() or a .properties file
        this.smtpHost       = "smtp.company.com";
        this.smsApiKey      = "twilio_api_key_xyz";
        this.pushServiceUrl = "https://fcm.googleapis.com/";
        this.debugMode      = false;
        System.out.println("[Config] NotificationConfig loaded (Bill Pugh Singleton)");
    }

    // Bill Pugh holder — not loaded until getInstance() is first called
    private static final class InstanceHolder {
        private static final NotificationConfig INSTANCE = new NotificationConfig();
    }

    public static NotificationConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    // --- Accessors (read-only — no setters, config is immutable after load) ---
    public String getSmtpHost()       { return smtpHost; }
    public String getSmsApiKey()      { return smsApiKey; }
    public String getPushServiceUrl() { return pushServiceUrl; }
    public boolean isDebugMode()      { return debugMode; }
}
