/**
 * ============================================================================
 * 🌟 FOUNDATION: State vs. Behavior (The Smallest Building Block)
 * ============================================================================
 */

/**
 * Example: Smartphone
 * 🔬 ANALYSIS:
 * - State (What it HAS): batteryLevel, model, isOn.
 * - Behavior (What it DOES): powerOn(), charge(), useApp().
 */
class Smartphone {
    // --- STATE (Structure / Data) ---
    // Encapsulation: Fields are private. 
    // They represent the "condition" of the object.
    private int batteryLevel;
    private final String model;
    private boolean isOn;

    public Smartphone(String model) {
        this.model = model;
        this.batteryLevel = 50; 
        this.isOn = false;
    }

    // --- GETTERS (Observability) ---
    // We 'observe' the state from outside, but we don't touch it directly.
    public int getBatteryLevel() {
        return batteryLevel;
    }

    public boolean isScreenOn() {
        return isOn;
    }

    // --- BEHAVIOR (Capabilities / Logic) ---
    // Methods define what the object can do. 
    // They often change the internal state.
    
    public void powerOn() {
        if (batteryLevel > 0) {
            this.isOn = true;
            System.out.println(model + " is now ON.");
        } else {
            System.out.println("Cannot power on " + model + ". Battery empty.");
        }
    }

    public void charge(int amount) {
        this.batteryLevel = Math.min(100, this.batteryLevel + amount);
        System.out.println(model + " charged to " + batteryLevel + "%.");
    }

    /**
     * "Tell, Don't Ask" Principle:
     * We 'tell' the phone to use an app. The phone internally checks 
     * its own battery and adjusts it. We don't ask for the battery 
     * level and subtract it from outside.
     */
    public void useApp(String appName) {
        if (!isOn) {
            System.out.println("Device is OFF. Turn it on first!");
            return;
        }
        if (batteryLevel >= 10) {
            batteryLevel -= 10;
            System.out.println("Using " + appName + "... Battery now: " + batteryLevel + "%");
        } else {
            System.out.println("Battery too low for " + appName + ".");
        }
    }
}

public class StateBehaviorDemo {
    public static void main(String[] args) {
        System.out.println("=== 🔬 Foundation: State vs Behavior ===");
        
        // 1. Instantiation (Creating the Object)
        Smartphone myPhone = new Smartphone("iPhone 15");
        
        // 2. Observing State (Initial)
        System.out.println("Initial Battery: " + myPhone.getBatteryLevel() + "%");
        
        // 3. Triggering Behavior
        myPhone.powerOn();
        myPhone.useApp("LinkedIn");
        myPhone.charge(20);
        
        // 4. Observing State (Final)
        System.out.println("Final Battery: " + myPhone.getBatteryLevel() + "%");
    }
}
