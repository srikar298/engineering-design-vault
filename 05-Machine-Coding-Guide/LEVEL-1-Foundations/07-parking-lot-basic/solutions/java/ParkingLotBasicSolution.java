package parkinglot;

import java.util.*;

/**
 * <h1>Gold Standard: Parking Lot (Basic)</h1>
 * 
 * <b>Why this is 10/10:</b>
 * 1. <b>Type Safety:</b> Uses Enums for Vehicle and Spot types.
 * 2. <b>Validation:</b> Checks size compatibility before parking.
 * 3. <b>Encapsulation:</b> The 'Spot' manages its own occupancy state.
 */

enum VehicleType { MOTORCYCLE, CAR, TRUCK }
enum SpotType { SMALL, MEDIUM, LARGE }

class Vehicle {
    private final String licensePlate;
    private final VehicleType type;
    public Vehicle(String lp, VehicleType t) { this.licensePlate = lp; this.type = t; }
    public VehicleType getType() { return type; }
    public String getLicensePlate() { return licensePlate; }
}

class ParkingSpot {
    private final String id;
    private final SpotType type;
    private Vehicle parkedVehicle;

    public ParkingSpot(String id, SpotType t) { this.id = id; this.type = t; }

    public boolean isAvailable() { return parkedVehicle == null; }

    public boolean canFit(Vehicle v) {
        // Motorcycle fits anywhere, Car needs Medium+, Truck needs Large
        switch (v.getType()) {
            case MOTORCYCLE: return true;
            case CAR: return type == SpotType.MEDIUM || type == SpotType.LARGE;
            case TRUCK: return type == SpotType.LARGE;
            default: return false;
        }
    }

    public void park(Vehicle v) { this.parkedVehicle = v; }
    public void unpark() { this.parkedVehicle = null; }
    public String getId() { return id; }
}

class ParkingLot {
    private final List<ParkingSpot> spots = new ArrayList<>();

    public void addSpot(ParkingSpot s) { spots.add(s); }

    public boolean parkVehicle(Vehicle v) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.canFit(v)) {
                spot.park(v);
                System.out.println("✅ Parked " + v.getType() + " [" + v.getLicensePlate() + "] in spot " + spot.getId());
                return true;
            }
        }
        System.out.println("❌ No available spots for " + v.getType());
        return false;
    }

    public void leave(String spotId) {
        spots.stream()
             .filter(s -> s.getId().equals(spotId))
             .findFirst()
             .ifPresent(s -> {
                 s.unpark();
                 System.out.println("🧹 Spot " + spotId + " is now free.");
             });
    }
}

public class ParkingLotBasicSolution {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot();
        lot.addSpot(new ParkingSpot("S1", SpotType.SMALL));
        lot.addSpot(new ParkingSpot("M1", SpotType.MEDIUM));
        lot.addSpot(new ParkingSpot("L1", SpotType.LARGE));

        // Test cases
        lot.parkVehicle(new Vehicle("BIKE-1", VehicleType.MOTORCYCLE)); // S1
        lot.parkVehicle(new Vehicle("CAR-1", VehicleType.CAR));        // M1
        lot.parkVehicle(new Vehicle("TRUCK-1", VehicleType.TRUCK));    // L1
        
        lot.leave("M1");
        lot.parkVehicle(new Vehicle("CAR-2", VehicleType.CAR));        // M1 (reused)
    }
}
