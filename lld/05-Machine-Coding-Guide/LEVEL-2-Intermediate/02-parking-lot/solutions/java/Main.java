import parking.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🚘 Enterprise Advanced Parking Lot System Demo 🚘");
        System.out.println("=================================================\n");

        // 1. Initialize ParkingLot (Singleton)
        ParkingLot parkingLot = ParkingLot.getInstance("Metropolitan Plaza");
        System.out.println("Created Parking Lot: " + parkingLot.getName());

        // 2. Add Floors and Spots
        // Floor 1 (Let's use floor number 1 as the first character of spot IDs)
        ParkingFloor floor1 = new ParkingFloor(1);
        floor1.addParkingSpot(new MotorcycleSpot("1-M1"));
        floor1.addParkingSpot(new MotorcycleSpot("1-M2"));
        floor1.addParkingSpot(new CompactSpot("1-C1"));
        floor1.addParkingSpot(new CompactSpot("1-C2"));
        floor1.addParkingSpot(new LargeSpot("1-L1"));
        
        // Floor 2
        ParkingFloor floor2 = new ParkingFloor(2);
        floor2.addParkingSpot(new MotorcycleSpot("2-M1"));
        floor2.addParkingSpot(new CompactSpot("2-C1"));
        floor2.addParkingSpot(new LargeSpot("2-L1"));

        parkingLot.addFloor(floor1);
        parkingLot.addFloor(floor2);

        System.out.println("Added Floor 1 and Floor 2 with diverse spots.");
        floor1.getDisplayBoard().show();

        // 3. Initialize Gates and Pricing Strategy
        // We'll use Tiered pricing for the exit gate
        IPricingStrategy tieredPricing = new TieredRateStrategy();
        EntryGate entryGateA = new EntryGate("Gate-A", parkingLot);
        EntryGate entryGateB = new EntryGate("Gate-B", parkingLot);
        ExitGate exitGateX = new ExitGate("Gate-X", parkingLot, tieredPricing);

        // 4. Basic Flow Simulation
        System.out.println("\n--- 🏁 Running Basic Flow Simulation ---");
        
        Vehicle bike = new Motorcycle("MOTO-111");
        Vehicle sedan = new Car("CAR-222");
        Vehicle semi = new Truck("TRUCK-333");

        // Park Motorcycle
        Ticket t1 = entryGateA.processVehicleEntry(bike);
        floor1.getDisplayBoard().show();

        // Park Car
        Ticket t2 = entryGateB.processVehicleEntry(sedan);
        floor1.getDisplayBoard().show();

        // Park Truck
        Ticket t3 = entryGateA.processVehicleEntry(semi);
        floor1.getDisplayBoard().show();

        // Exit Motorcycle
        if (t1 != null) {
            exitGateX.processVehicleExit(t1.getTicketId());
        }
        floor1.getDisplayBoard().show();


        // 5. Concurrency Stress Test
        // Goal: Allocate multiple vehicles simultaneously through concurrent gates
        // to verify that thread safety holds, display boards update atomically, and no double booking occurs.
        System.out.println("\n--- ⚡ Running Concurrency Stress Test ---");
        System.out.println("Resetting parking lot to clean state...");
        
        // Let's exit the car and truck first
        if (t2 != null) exitGateX.processVehicleExit(t2.getTicketId());
        if (t3 != null) exitGateX.processVehicleExit(t3.getTicketId());
        
        System.out.println("Initial status of Floor 1:");
        floor1.getDisplayBoard().show();

        // Let's spawn 10 threads trying to park 10 cars simultaneously.
        // We only have:
        // Floor 1: 2 Compact, 1 Large = 3 spots compatible with Cars
        // Floor 2: 1 Compact, 1 Large = 2 spots compatible with Cars
        // Total Car spots = 5 spots.
        // 10 threads should result in exactly 5 successful bookings and 5 rejections.
        
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<Ticket>> tasks = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final String plate = "CAR-TEST-" + i;
            tasks.add(() -> {
                Vehicle testCar = new Car(plate);
                // Randomly choose entry gate A or B
                EntryGate gate = (ThreadLocalRandom.current().nextBoolean()) ? entryGateA : entryGateB;
                return gate.processVehicleEntry(testCar);
            });
        }

        try {
            System.out.println("Starting 10 concurrent car bookings...");
            List<Future<Ticket>> results = executor.invokeAll(tasks);
            executor.shutdown();
            
            int successCount = 0;
            int failCount = 0;
            List<Ticket> successfulTickets = new ArrayList<>();

            for (Future<Ticket> result : results) {
                Ticket ticket = result.get();
                if (ticket != null) {
                    successCount++;
                    successfulTickets.add(ticket);
                } else {
                    failCount++;
                }
            }

            System.out.println("\n--- Concurrency Test Summary ---");
            System.out.println("Total Requests Submitted: " + numThreads);
            System.out.println("Successful Allocations:  " + successCount + " (Expected: 5)");
            System.out.println("Failed Allocations:      " + failCount + " (Expected: 5)");
            
            System.out.println("\nAvailability after concurrent allocations:");
            System.out.println("Floor 1:");
            floor1.getDisplayBoard().show();
            System.out.println("Floor 2:");
            floor2.getDisplayBoard().show();

            // Perform exit for all successfully parked cars
            System.out.println("\nReleasing all concurrently parked cars...");
            for (Ticket ticket : successfulTickets) {
                exitGateX.processVehicleExit(ticket.getTicketId());
            }

            System.out.println("\nFinal Availability (Should match initial free capacities):");
            System.out.println("Floor 1:");
            floor1.getDisplayBoard().show();
            System.out.println("Floor 2:");
            floor2.getDisplayBoard().show();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
