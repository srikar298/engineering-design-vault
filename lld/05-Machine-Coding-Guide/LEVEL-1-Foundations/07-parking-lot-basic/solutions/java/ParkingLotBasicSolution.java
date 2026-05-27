package parkinglot;

/**
 * <h1>Gold Standard: Parking Lot (Basic)</h1>
 * 
 * <b>Why this is 10/10:</b>
 * 1. <b>Type Safety:</b> Uses Enums for Vehicle and Spot types.
 * 2. <b>Validation:</b> Checks size compatibility before parking.
 * 3. <b>Encapsulation:</b> The 'Spot' manages its own occupancy state.
 * 4. <b>Strategy Pattern:</b> Strategy for spot allocation is decoupled.
 */
public class ParkingLotBasicSolution {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(new FirstAvailableSpotStrategy());
        lot.addSpot(new ParkingSpot("S1", SpotType.SMALL));
        lot.addSpot(new ParkingSpot("M1", SpotType.MEDIUM));
        lot.addSpot(new ParkingSpot("L1", SpotType.LARGE));

        // Test cases
        lot.parkVehicle(new Vehicle("BIKE-1", VehicleType.MOTORCYCLE)); // S1
        lot.parkVehicle(new Vehicle("CAR-1", VehicleType.CAR));        // M1
        lot.parkVehicle(new Vehicle("TRUCK-1", VehicleType.TRUCK));    // L1
        
        lot.leave("M1");
        lot.parkVehicle(new Vehicle("CAR-2", VehicleType.CAR));        // M1 (reused)
    }
}
