package parking;

public class CompactSpot extends ParkingSpot {
    public CompactSpot(String spotId) {
        super(spotId, ParkingSpotType.COMPACT);
    }

    @Override
    public boolean isCompatible(VehicleType vehicleType) {
        return vehicleType == VehicleType.MOTORCYCLE || vehicleType == VehicleType.CAR;
    }
}
