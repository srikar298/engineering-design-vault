package parkinglot;

import java.util.List;

public class FirstAvailableSpotStrategy implements SpotAllocationStrategy {
    @Override
    public ParkingSpot findSpot(Vehicle vehicle, List<ParkingSpot> spots) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.canFit(vehicle)) {
                return spot;
            }
        }
        return null;
    }
}
