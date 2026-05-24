package parking;

public class MotorcycleSpot extends ParkingSpot {
    public MotorcycleSpot(String spotId) {
        super(spotId, ParkingSpotType.MOTORCYCLE);
    }

    @Override
    public boolean isCompatible(VehicleType vehicleType) {
        return vehicleType == VehicleType.MOTORCYCLE;
    }
}
