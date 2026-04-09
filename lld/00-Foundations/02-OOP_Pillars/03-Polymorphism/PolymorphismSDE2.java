package polymorphism;

/**
 * <h1>Polymorphism: Covariant Returns (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Vehicle Factory. 
 * A parent factory returns a <code>Vehicle</code>, but a specific factory 
 * returns a <code>Tesla</code>. 
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Covariant Returns:</b> A subclass can override a method and return 
 *    a more narrow type than the parent. This is critical for <b>Factory</b> 
 *    and <b>Builder</b> patterns.
 * 2. <b>Binary Compatibility:</b> Polymorphism allows you to update the 
 *    implementation without recompiling the clients.
 */

class Vehicle { public void drive() { System.out.println("Driving..."); } }
class Tesla extends Vehicle { public void autopilot() { System.out.println("Tesla Autopilot active."); } }

abstract class VehicleFactory {
    public abstract Vehicle create();
}

class TeslaFactory extends VehicleFactory {
    /**
     * --- [PRODUCTION_ENHANCEMENT] (Covariant Return) ---
     * We return Tesla (Specific) instead of just Vehicle (Generic).
     * This is perfectly legal in Java and very useful for specific clients.
     */
    @Override
    public Tesla create() {
        return new Tesla();
    }
}

public class PolymorphismSDE2 {
    public static void main(String[] args) {
        VehicleFactory factory = new TeslaFactory();
        
        // [INTERVIEW_MVP]: Standard Polymorphism
        Vehicle v = factory.create();
        v.drive();

        // [PRODUCTION_ENHANCEMENT]: Using the specific type from the concrete factory
        TeslaFactory specificFactory = new TeslaFactory();
        Tesla t = specificFactory.create(); // No casting needed!
        t.autopilot();
        
        System.out.println("✅ Covariant returns allow for more specific APIs in subclasses.");
    }
}
