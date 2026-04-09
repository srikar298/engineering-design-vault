package addons.apps;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <h1>20 - Uber LLD (The "Scale & Speed" Champion)</h1>
 * 
 * <b>Scenario:</b> Design a ride-sharing service where Riders can find the 
 * closest available Driver and book a ride. 
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Spatial Indexing:</b> In a real system, use Geo-hashes or Quad-Trees. 
 *    Here, we simulate this with a <code>Location</code> class and a distance formula.
 * 2. <b>Concurrency:</b> Use <code>AtomicBoolean</code> or Locks to ensure two 
 *    riders don't book the same driver at the exact same millisecond.
 * 3. <b>Strategy Pattern:</b> Decouple the <b>Pricing Strategy</b> (Surge vs Normal).
 */

class Location {
    double x, y;
    public Location(double x, double y) { this.x = x; this.y = y; }
    public double distanceTo(Location other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}

class Driver {
    String id;
    Location location;
    AtomicBoolean isAvailable = new AtomicBoolean(true);

    public Driver(String id, Location loc) { this.id = id; this.location = loc; }
}

interface PricingStrategy {
    double calculate(double distance);
}

class DefaultPricing implements PricingStrategy {
    @Override public double calculate(double dist) { return dist * 10.0; }
}

class SurgePricing implements PricingStrategy {
    @Override public double calculate(double dist) { return dist * 25.0; }
}

class UberSystem {
    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();
    private PricingStrategy pricing = new DefaultPricing();

    public void addDriver(Driver d) { drivers.put(d.id, d); }
    public void setSurge(boolean isSurge) {
        this.pricing = isSurge ? new SurgePricing() : new DefaultPricing();
    }

    public String bookRide(String riderId, Location pickup) {
        System.out.println("[Uber] Rider " + riderId + " searching for drivers...");
        
        // 1. Find the Closest Available Driver
        Driver closest = null;
        double minDist = Double.MAX_VALUE;

        for (Driver d : drivers.values()) {
            if (d.isAvailable.get()) {
                double dist = d.location.distanceTo(pickup);
                if (dist < minDist) {
                    minDist = dist;
                    closest = d;
                }
            }
        }

        if (closest == null) return "Error: No drivers nearby.";

        // 2. Atomic Booking (Prevent double-booking)
        if (closest.isAvailable.compareAndSet(true, false)) {
            double cost = pricing.calculate(minDist);
            return String.format("Ride Booked with Driver %s. Distance: %.2f km. Cost: $%.2f", 
                                 closest.id, minDist, cost);
        } else {
            // Recursive retry or fail
            return bookRide(riderId, pickup);
        }
    }
}

public class UberAppSDE2 {
    public static void main(String[] args) {
        UberSystem uber = new UberSystem();
        uber.addDriver(new Driver("D1", new Location(1, 1)));
        uber.addDriver(new Driver("D2", new Location(10, 10)));

        System.out.println("--- Normal Traffic ---");
        System.out.println(uber.bookRide("User_Alice", new Location(0, 0)));

        System.out.println("\n--- Surge Pricing Enabled ---");
        uber.setSurge(true);
        System.out.println(uber.bookRide("User_Bob", new Location(11, 11)));
        
        System.out.println("\nSenior Signal: 'We use the Strategy pattern for pricing " +
                           "to allow runtime switching during high-demand events, and " +
                           "leverage Atomic variables to handle concurrent booking " +
                           "requests without global locks.'");
    }
}
