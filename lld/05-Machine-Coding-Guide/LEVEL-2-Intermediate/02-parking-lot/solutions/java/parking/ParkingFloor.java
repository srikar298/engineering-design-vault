package parking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSpot> spots = new ArrayList<>();
    private final DisplayBoard displayBoard = new DisplayBoard();
    private final ReentrantLock lock = new ReentrantLock();

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public DisplayBoard getDisplayBoard() {
        return displayBoard;
    }

    public void addParkingSpot(ParkingSpot spot) {
        lock.lock();
        try {
            spots.add(spot);
            displayBoard.update(spot.getType(), 1);
        } finally {
            lock.unlock();
        }
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {
        lock.lock();
        try {
            ParkingSpot spot = findCompatibleFreeSpot(vehicle.getType());
            if (spot != null) {
                spot.park(vehicle);
                displayBoard.update(spot.getType(), -1);
                return spot;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void freeSpot(ParkingSpot spot) {
        lock.lock();
        try {
            spot.unpark();
            displayBoard.update(spot.getType(), 1);
        } finally {
            lock.unlock();
        }
    }

    private ParkingSpot findCompatibleFreeSpot(VehicleType type) {
        // We look for the most specific/best-fit spot type first to save large spots
        if (type == VehicleType.MOTORCYCLE) {
            // Moto fits in Motorcycle -> Compact -> Large
            ParkingSpot spot = findFreeSpotOfType(ParkingSpotType.MOTORCYCLE);
            if (spot != null) return spot;
            spot = findFreeSpotOfType(ParkingSpotType.COMPACT);
            if (spot != null) return spot;
            return findFreeSpotOfType(ParkingSpotType.LARGE);
        } else if (type == VehicleType.CAR) {
            // Car fits in Compact -> Large
            ParkingSpot spot = findFreeSpotOfType(ParkingSpotType.COMPACT);
            if (spot != null) return spot;
            return findFreeSpotOfType(ParkingSpotType.LARGE);
        } else {
            // Truck fits only in Large
            return findFreeSpotOfType(ParkingSpotType.LARGE);
        }
    }

    private ParkingSpot findFreeSpotOfType(ParkingSpotType type) {
        for (ParkingSpot spot : spots) {
            if (spot.getType() == type && spot.isFree()) {
                return spot;
            }
        }
        return null;
    }
}
