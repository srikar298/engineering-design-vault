package elevator;

import java.util.TreeSet;
import java.util.Collections;

public class ElevatorController implements Runnable {
    private final ElevatorCar car;
    private final TreeSet<Integer> upRequests = new TreeSet<>();
    private final TreeSet<Integer> downRequests = new TreeSet<>(Collections.reverseOrder());
    private volatile boolean isRunning = true;

    public ElevatorController(int id) {
        this.car = new ElevatorCar(id);
    }

    public ElevatorCar getCar() {
        return car;
    }

    public void stopRunning() {
        this.isRunning = false;
    }

    public void addRequest(int floor) {
        synchronized (this) {
            int currentFloor = car.getCurrentFloor();
            if (floor == currentFloor) {
                if (car.getDirection() == Direction.IDLE) {
                    processStop();
                } else {
                    // Stop at this floor when moving past
                    if (car.getDirection() == Direction.UP) {
                        upRequests.add(floor);
                    } else {
                        downRequests.add(floor);
                    }
                }
                return;
            }

            if (floor > currentFloor) {
                upRequests.add(floor);
            } else {
                downRequests.add(floor);
            }
            
            System.out.printf("[Elevator-%d] Floor %d added to requests. (Pending UP: %s, DOWN: %s)\n", 
                car.getId(), floor, upRequests, downRequests);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(100); // Controller polling interval
                Direction dir = car.getDirection();

                if (dir == Direction.IDLE) {
                    synchronized (this) {
                        int currentFloor = car.getCurrentFloor();
                        // Check if we need to service anything at current floor first
                        if (upRequests.contains(currentFloor) || downRequests.contains(currentFloor)) {
                            processStop();
                            upRequests.remove(currentFloor);
                            downRequests.remove(currentFloor);
                            continue;
                        }

                        // Determine new direction
                        if (!upRequests.isEmpty()) {
                            car.setDirection(Direction.UP);
                        } else if (!downRequests.isEmpty()) {
                            car.setDirection(Direction.DOWN);
                        }
                    }
                } else if (dir == Direction.UP) {
                    moveUpAndService();
                } else if (dir == Direction.DOWN) {
                    moveDownAndService();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void moveUpAndService() throws InterruptedException {
        // Move one floor up
        Thread.sleep(300); // Simulated transit time
        car.stepUp();
        int currentFloor = car.getCurrentFloor();

        synchronized (this) {
            // Check if we should stop at this floor
            if (upRequests.contains(currentFloor)) {
                processStop();
                upRequests.remove(currentFloor);
            }

            // Check if there are further requests above
            Integer nextAbove = upRequests.higher(currentFloor);
            if (nextAbove == null) {
                // No more requests in the UP direction. Check if there are requests below.
                if (!downRequests.isEmpty()) {
                    // Check if there's any down request at current floor that we should catch before descending
                    if (downRequests.contains(currentFloor)) {
                        processStop();
                        downRequests.remove(currentFloor);
                    }
                    car.setDirection(Direction.DOWN);
                } else {
                    car.setDirection(Direction.IDLE);
                    System.out.printf("[Elevator-%d] No more requests. Becoming IDLE.\n", car.getId());
                }
            }
        }
    }

    private void moveDownAndService() throws InterruptedException {
        // Move one floor down
        Thread.sleep(300); // Simulated transit time
        car.stepDown();
        int currentFloor = car.getCurrentFloor();

        synchronized (this) {
            // Check if we should stop at this floor
            if (downRequests.contains(currentFloor)) {
                processStop();
                downRequests.remove(currentFloor);
            }

            // Check if there are further requests below
            Integer nextBelow = downRequests.higher(currentFloor); // Remember downRequests uses reverseOrder()
            if (nextBelow == null) {
                // No more requests in the DOWN direction. Check if there are requests above.
                if (!upRequests.isEmpty()) {
                    // Check if there's any up request at current floor
                    if (upRequests.contains(currentFloor)) {
                        processStop();
                        upRequests.remove(currentFloor);
                    }
                    car.setDirection(Direction.UP);
                } else {
                    car.setDirection(Direction.IDLE);
                    System.out.printf("[Elevator-%d] No more requests. Becoming IDLE.\n", car.getId());
                }
            }
        }
    }

    private void processStop() {
        System.out.printf("[Elevator-%d] Stopping at Floor %d.\n", car.getId(), car.getCurrentFloor());
        car.openDoor();
        try {
            Thread.sleep(200); // Door open simulation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        car.closeDoor();
    }
}
