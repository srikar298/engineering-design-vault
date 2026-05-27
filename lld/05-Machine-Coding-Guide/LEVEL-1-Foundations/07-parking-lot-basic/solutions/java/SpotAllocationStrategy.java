package parkinglot;

import java.util.List;

public interface SpotAllocationStrategy {
    ParkingSpot findSpot(Vehicle vehicle, List<ParkingSpot> spots);
}
