package class_relationships;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>05 - Class Relationships: Lifecycle & Ownership (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> Building a Vehicle Management System.
 * 1. <b>Dependency:</b> Service uses a tool (Method param).
 * 2. <b>Association:</b> Driver and Passenger (Peer relationship).
 * 3. <b>Aggregation:</b> Car and its Spare Tire (Independent lifecycle).
 * 4. <b>Composition:</b> Car and its Engine (Dependent lifecycle).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * - <b>Composition</b> is the strongest coupling. If the 'Whole' dies, the 'Part' dies.
 * - <b>Aggregation</b> allows the 'Part' to exist without the 'Whole'.
 * - <b>Dependency</b> is the foundation of <b>Dependency Injection</b> and <b>Unit Testing</b>.
 * - <b>Ownership:</b> In high-load systems, knowing who "owns" an object prevents memory leaks.
 */

// --- [RELATIONSHIP 4]: COMPOSITION (Lifecycle: Tied) ---
class Engine {
    public void start() { System.out.println("Engine started."); }
}

// --- [RELATIONSHIP 3]: AGGREGATION (Lifecycle: Independent) ---
class SpareTire {
    private final String brand;
    public SpareTire(String b) { this.brand = b; }
    public String getBrand() { return brand; }
}

class Car {
    // Composition: The engine is created when the car is created.
    private final Engine engine = new Engine(); 

    // Aggregation: The tire exists before/after the car.
    private SpareTire spareTire; 

    public void setSpareTire(SpareTire tire) { this.spareTire = tire; }

    /**
     * [INTERVIEW_MVP]: Show lifecycle coupling.
     */
    public void drive() {
        engine.start();
        System.out.println("Driving with " + (spareTire != null ? spareTire.getBrand() : "no") + " spare.");
    }
}

// --- [RELATIONSHIP 1]: DEPENDENCY (Weakest - "Uses a") ---
class Mechanic {
    /**
     * Dependency: The mechanic 'uses' the car to fix it. 
     * The car is not a property of the mechanic.
     */
    public void repair(Car car) {
        System.out.println("Mechanic: Performing 10-point check...");
        car.drive();
    }
}

// --- [RELATIONSHIP 2]: ASSOCIATION (Peer relationship - "Has a") ---
class Driver {
    private final String name;
    private Car currentCar; // Association: Driver has a car, but can switch cars.

    public Driver(String n) { this.name = n; }
    public void setCar(Car car) { this.currentCar = car; }
}

/**
 * 🎓 SDE-2+ READINESS CHECK:
 * - Composition: 'Private Final' + 'new inside constructor' = Strongest.
 * - Aggregation: 'Passed via Setter/Constructor' = Object lives longer than parent.
 * - Dependency: 'Passed via Method Parameter' = No persistent reference.
 */
public class RelationshipMasterySDE2 {
    public static void main(String[] args) {
        // [INTERVIEW_MVP]: Setup objects
        SpareTire michelin = new SpareTire("Michelin");
        Car myCar = new Car();
        myCar.setSpareTire(michelin); // Aggregation (tire passed in)

        Mechanic specialist = new Mechanic();
        specialist.repair(myCar); // Dependency (car passed to method)

        // [PRODUCTION_ENHANCEMENT]: Lifecycle demo
        myCar = null; 
        System.out.println("Car is scrapped. Engine is gone (Composition).");
        System.out.println("But the spare tire brand is still: " + michelin.getBrand() + " (Aggregation).");
    }
}
