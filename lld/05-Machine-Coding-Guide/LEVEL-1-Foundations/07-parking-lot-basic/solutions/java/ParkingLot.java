package parkinglot;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private final List<ParkingSpot> spots = new ArrayList<>();
    private final SpotAllocationStrategy allocationStrategy;

    public ParkingLot() {
        this.allocationStrategy = new FirstAvailableSpotStrategy();
    }

    public ParkingLot(SpotAllocationStrategy strategy) {
        this.allocationStrategy = strategy;
    }

    public synchronized void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public synchronized boolean parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = allocationStrategy.findSpot(vehicle, spots);
        if (spot != null) {
            spot.park(vehicle);
            System.out.println("✅ Parked " + vehicle.getType() + " [" + vehicle.getLicensePlate() + "] in spot " + spot.getId());
            return true;
        }
        System.out.println("❌ No available spots for " + vehicle.getType());
        return false;
    }

    public synchronized void leave(String spotId) {
        spots.stream()
                .filter(s -> s.getId().equals(spotId))
                .findFirst()
                .ifPresent(s -> {
                    s.unpark();
                    System.out.println("🧹 Spot " + spotId + " is now free.");
                });
    }
}
