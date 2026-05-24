import elevator.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🛗 Enterprise Multi-Elevator Control System Demo 🛗");
        System.out.println("=================================================\n");

        // 1. Initialize ElevatorSystem singleton
        ElevatorSystem system = ElevatorSystem.getInstance();

        // 2. Add Elevators (started as background tasks)
        system.addElevator(1); // Starts at floor 0
        system.addElevator(2); // Starts at floor 0
        system.addElevator(3); // Starts at floor 0

        // Allow some time for threads to boot up
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- 🏁 Simulating Passenger Requests ---");

        // Passenger A at Floor 5 wants to go UP
        system.pressHallButton(5, Direction.UP);

        // Passenger B at Floor 2 wants to go UP
        system.pressHallButton(2, Direction.UP);

        // Let them travel briefly
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Inside Elevator-1 (which got dispatched to Floor 5), passenger selects Floor 8
        system.pressCarButton(1, 8);

        // Passenger C at Floor 9 wants to go DOWN
        system.pressHallButton(9, Direction.DOWN);

        // Inside Elevator-2 (which got Floor 2), passenger selects Floor 4
        system.pressCarButton(2, 4);

        // Let the simulation run for 5 seconds to clear all requests
        try {
            System.out.println("\n--- Waiting for elevators to finish servicing all requests ---");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. Stop the system
        System.out.println();
        system.stop();
        System.exit(0);
    }
}
