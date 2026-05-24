package parking;

public class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot assignedSpot;
    private final long entryTime;
    private long exitTime;
    private double amount;
    private TicketStatus status;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot assignedSpot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.assignedSpot = assignedSpot;
        this.entryTime = System.currentTimeMillis();
        this.status = TicketStatus.ACTIVE;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getAssignedSpot() {
        return assignedSpot;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public synchronized long getExitTime() {
        return exitTime;
    }

    public synchronized double getAmount() {
        return amount;
    }

    public synchronized TicketStatus getStatus() {
        return status;
    }

    public synchronized void pay(double amountPaid) {
        if (this.status == TicketStatus.PAID) {
            throw new IllegalStateException("Ticket is already paid.");
        }
        this.exitTime = System.currentTimeMillis();
        this.amount = amountPaid;
        this.status = TicketStatus.PAID;
    }
}
