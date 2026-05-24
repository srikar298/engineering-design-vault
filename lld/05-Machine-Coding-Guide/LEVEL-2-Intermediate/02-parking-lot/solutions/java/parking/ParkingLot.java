package parking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class ParkingLot {
    private static volatile ParkingLot instance = null;

    private final String name;
    private final List<ParkingFloor> floors = new ArrayList<>();
    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();

    private ParkingLot(String name) {
        this.name = name;
    }

    public static ParkingLot getInstance(String name) {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) {
                    instance = new ParkingLot(name);
                }
            }
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public List<ParkingFloor> getFloors() {
        return floors;
    }

    public ParkingSpot findAndParkVehicle(Vehicle vehicle) {
        // Try to park floor by floor
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.parkVehicle(vehicle);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public void releaseSpot(ParkingFloor floor, ParkingSpot spot) {
        floor.freeSpot(spot);
    }

    public Ticket generateTicket(Vehicle vehicle, ParkingSpot spot) {
        String ticketId = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ticket ticket = new Ticket(ticketId, vehicle, spot);
        activeTickets.put(ticketId, ticket);
        return ticket;
    }

    public Ticket getTicket(String ticketId) {
        return activeTickets.get(ticketId);
    }

    public void removeTicket(String ticketId) {
        activeTickets.remove(ticketId);
    }
}
