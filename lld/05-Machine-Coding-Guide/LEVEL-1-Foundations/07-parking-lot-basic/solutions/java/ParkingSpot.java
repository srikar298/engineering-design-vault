package parkinglot;

public class ParkingSpot {
    private final String id;
    private final SpotType type;
    private Vehicle parkedVehicle;

    public ParkingSpot(String id, SpotType type) {
        this.id = id;
        this.type = type;
    }

    public synchronized boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean canFit(Vehicle v) {
        // Motorcycle fits anywhere, Car needs Medium+, Truck needs Large
        switch (v.getType()) {
            case MOTORCYCLE:
                return true;
            case CAR:
                return type == SpotType.MEDIUM || type == SpotType.LARGE;
            case TRUCK:
                return type == SpotType.LARGE;
            default:
                return false;
        }
    }

    public synchronized void park(Vehicle v) {
        this.parkedVehicle = v;
    }

    public synchronized void unpark() {
        this.parkedVehicle = null;
    }

    public String getId() {
        return id;
    }
}
