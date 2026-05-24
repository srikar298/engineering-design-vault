package parking;

public class LargeSpot extends ParkingSpot {
    public LargeSpot(String spotId) {
        super(spotId, ParkingSpotType.LARGE);
    }

    @Override
    public boolean isCompatible(VehicleType vehicleType) {
        return true;
    }
}
