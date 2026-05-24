package parking;

public class ExitGate {
    private final String gateId;
    private final ParkingLot parkingLot;
    private final IPricingStrategy pricingStrategy;

    public ExitGate(String gateId, ParkingLot parkingLot, IPricingStrategy pricingStrategy) {
        this.gateId = gateId;
        this.parkingLot = parkingLot;
        this.pricingStrategy = pricingStrategy;
    }

    public String getGateId() {
        return gateId;
    }

    public double processVehicleExit(String ticketId) {
        Ticket ticket = parkingLot.getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Invalid ticket ID: " + ticketId);
        }

        // Simulate some duration (calculate fee based on entry and current time)
        long durationMs = System.currentTimeMillis() - ticket.getEntryTime();
        
        // Let's add a small simulated time multiplier if duration is too small 
        // to make sure we get a non-zero fee for demonstration purposes (e.g. 1 ms = 1 hour)
        if (durationMs < 1000) {
            durationMs = 3600000; // Force 1 hour duration
        }

        double fee = pricingStrategy.calculateFee(durationMs);
        ticket.pay(fee);
        
        // Release the spot
        ParkingSpot spot = ticket.getAssignedSpot();
        
        // Find which floor the spot belongs to and release it
        ParkingFloor assignedFloor = null;
        for (ParkingFloor floor : parkingLot.getFloors()) {
            if (floor.getFloorNumber() == Character.getNumericValue(spot.getSpotId().charAt(0))) {
                assignedFloor = floor;
                break;
            }
        }
        
        if (assignedFloor != null) {
            parkingLot.releaseSpot(assignedFloor, spot);
        }

        parkingLot.removeTicket(ticketId);
        
        System.out.printf("[%s] Exit complete for Ticket: %s. Fee: $%.2f. Spot %s is now free.\n", 
            gateId, ticketId, fee, spot.getSpotId());
            
        return fee;
    }
}
