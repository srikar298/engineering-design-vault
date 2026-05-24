package parking;

public abstract class ParkingSpot {
    private final String spotId;
    private final ParkingSpotType type;
    private boolean isFree = true;
    private Vehicle parkedVehicle = null;

    protected ParkingSpot(String spotId, ParkingSpotType type) {
        this.spotId = spotId;
        this.type = type;
    }

    public String getSpotId() {
        return spotId;
    }

    public ParkingSpotType getType() {
        return type;
    }

    public synchronized boolean isFree() {
        return isFree;
    }

    public synchronized Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public abstract boolean isCompatible(VehicleType vehicleType);

    public synchronized void park(Vehicle vehicle) {
        if (!isFree) {
            throw new IllegalStateException("Spot is already occupied.");
        }
        if (!isCompatible(vehicle.getType())) {
            throw new IllegalArgumentException("Incompatible vehicle type for this spot.");
        }
        this.parkedVehicle = vehicle;
        this.isFree = false;
    }

    public synchronized void unpark() {
        if (isFree) {
            throw new IllegalStateException("Spot is already free.");
        }
        this.parkedVehicle = null;
        this.isFree = true;
    }
}
