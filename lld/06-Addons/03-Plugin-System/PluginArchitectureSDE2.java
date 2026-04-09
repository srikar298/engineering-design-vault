package addons.plugins;

import java.util.ServiceLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>03 - Plugin Architecture (The "Open-Closed" Masterclass)</h1>
 * 
 * <b>Scenario:</b> You are building an <b>IDE (like IntelliJ)</b> or a <b>Payment Gateway</b>. 
 * You want to allow third-party developers to add new features (e.g. a new Language 
 * Parser or a new Payment Method) WITHOUT modifying your core source code.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>ServiceLoader (SPI):</b> Use Java's Service Provider Interface to discover 
 *    implementations on the classpath at runtime.
 * 2. <b>Dynamic Extension:</b> This allows true decoupling. The core only depends 
 *    on the Interface, not the Concrete implementations.
 * 3. <b>Inversion of Control:</b> You don't "new up" the objects; you ask the 
 *    runtime to find them for you.
 */

// --- CORE CONTRACT (The Interface) ---
interface PaymentGateway {
    String getProviderName();
    void process(double amount);
}

// --- CORE ENGINE (The Orchestrator) ---
class PaymentEngine {
    private final List<PaymentGateway> providers = new ArrayList<>();

    public PaymentEngine() {
        // --- [INTERVIEW_MVP] (Dynamic Discovery) ---
        // ServiceLoader finds all JARs/Classes that implement PaymentGateway
        ServiceLoader<PaymentGateway> loader = ServiceLoader.load(PaymentGateway.class);
        for (PaymentGateway provider : loader) {
            providers.add(provider);
        }
    }

    public void processAll(double amount) {
        if (providers.isEmpty()) {
            System.out.println("⚠️ No payment providers found. (Try adding them manually for this demo)");
        }
        providers.forEach(p -> p.process(amount));
    }

    // Manual registration (Fallback for non-SPI environments)
    public void register(PaymentGateway p) { providers.add(p); }
}

// --- EXTENSIONS (Isolated Implementations) ---
class PayPalPlugin implements PaymentGateway {
    public String getProviderName() { return "PayPal"; }
    public void process(double amount) { System.out.println("[PayPal] Charging $" + amount); }
}

class StripePlugin implements PaymentGateway {
    public String getProviderName() { return "Stripe"; }
    public void process(double amount) { System.out.println("[Stripe] Charging $" + amount); }
}

public class PluginArchitectureSDE2 {
    public static void main(String[] args) {
        PaymentEngine engine = new PaymentEngine();

        // Normally, PayPal/Stripe would be discovered automatically via SPI 
        // if listed in META-INF/services/addons.plugins.PaymentGateway
        // For this demo, we'll manually register them to show the engine in action.
        engine.register(new PayPalPlugin());
        engine.register(new StripePlugin());

        System.out.println("--- Starting Payment Engine ---");
        engine.processAll(250.0);
    }
}
