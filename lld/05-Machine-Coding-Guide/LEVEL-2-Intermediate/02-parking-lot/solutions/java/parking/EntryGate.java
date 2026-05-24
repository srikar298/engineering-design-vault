package parking;

public class EntryGate {
    private final String gateId;
    private final ParkingLot parkingLot;

    public EntryGate(String gateId, ParkingLot parkingLot) {
        this.gateId = gateId;
        this.parkingLot = parkingLot;
    }

    public String getGateId() {
        return gateId;
    }

    public Ticket processVehicleEntry(Vehicle vehicle) {
        System.out.printf("[%s] Processing entry for vehicle: %s (%s)\n", 
            gateId, vehicle.getLicensePlate(), vehicle.getType());
            
        ParkingSpot spot = parkingLot.findAndParkVehicle(vehicle);
        if (spot == null) {
            System.out.printf("[%s] Parking full! No compatible spot available for %s\n", 
                gateId, vehicle.getType());
            return null;
        }

        Ticket ticket = parkingLot.generateTicket(vehicle, spot);
        System.out.printf("[%s] Ticket issued: %s. Assigned Spot: %s\n", 
            gateId, ticket.getTicketId(), spot.getSpotId());
        return ticket;
    }
}
