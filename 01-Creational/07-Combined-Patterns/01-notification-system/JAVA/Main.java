import channel.ChannelRegistry;
import channel.ChannelRegistry.ChannelType;
import channel.INotificationChannel;
import message.NotificationMessage;
import message.NotificationMessage.Priority;
import config.NotificationConfig;

/**
 * <h1>Main — Integration Demo</h1>
 *
 * <p>Demonstrates all three patterns working together:
 * <ol>
 *   <li><b>Singleton</b>  — {@code NotificationConfig.getInstance()} (called internally)</li>
 *   <li><b>Builder</b>   — {@code new NotificationMessage.Builder(...).subject(...).build()}</li>
 *   <li><b>Factory Method</b> — {@code ChannelRegistry.getChannel(ChannelType.EMAIL)}</li>
 * </ol>
 *
 * <p>The client code is <b>completely decoupled</b> from:
 * <ul>
 *   <li>How config is loaded (Singleton handles it)</li>
 *   <li>Which channel class is actually instantiated (Factory handles it)</li>
 *   <li>Which fields of the message are optional (Builder handles it)</li>
 * </ul>
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       Notification System — Combined Patterns        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        // ── SCENARIO 1: Order Confirmation → Email ──────────────────────────
        System.out.println("─── Scenario 1: Order Confirmation (Email) ───");

        NotificationMessage orderEmail = new NotificationMessage.Builder(
                "customer@example.com",
                "Your order #ORD-9921 has been confirmed and will ship in 2 days.")
            .subject("Order Confirmed ✅")
            .priority(Priority.NORMAL)
            .correlationId("ORD-9921")
            .build();

        INotificationChannel emailChannel = ChannelRegistry.getChannel(ChannelType.EMAIL);
        emailChannel.send(orderEmail);

        System.out.println();

        // ── SCENARIO 2: OTP Delivery → SMS ─────────────────────────────────
        System.out.println("─── Scenario 2: OTP Delivery (SMS) ───");

        NotificationMessage otpSms = new NotificationMessage.Builder(
                "+91-9876543210",
                "Your OTP is 847291. Valid for 5 minutes. Do not share.")
            .priority(Priority.HIGH)
            .correlationId("OTP-847291")
            .build();

        INotificationChannel smsChannel = ChannelRegistry.getChannel(ChannelType.SMS);
        smsChannel.send(otpSms);

        System.out.println();

        // ── SCENARIO 3: Flash Sale Alert → Push Notification ───────────────
        System.out.println("─── Scenario 3: Flash Sale Alert (Push) ───");

        NotificationMessage pushAlert = new NotificationMessage.Builder(
                "device_token_abc123",
                "Flash sale starts now! 40% off all electronics.")
            .subject("⚡ Flash Sale!")
            .templateId("tmpl_flash_sale_v2")
            .priority(Priority.CRITICAL)
            .build();

        INotificationChannel pushChannel = ChannelRegistry.getChannel(ChannelType.PUSH);
        pushChannel.send(pushAlert);

        System.out.println();

        // ── SINGLETON PROOF: Config loaded only once ────────────────────────
        System.out.println("─── Singleton Proof ───");
        NotificationConfig c1 = NotificationConfig.getInstance();
        NotificationConfig c2 = NotificationConfig.getInstance();
        System.out.println("Config instances are same: " + (c1 == c2));
        System.out.println("SMTP Host: " + c1.getSmtpHost());

        System.out.println("\n✅ All patterns integrated successfully.");
    }
}
