package elevator;

import java.util.List;

public class NearestDispatcher implements IDispatcher {
    @Override
    public ElevatorController dispatch(List<ElevatorController> controllers, int floor, Direction direction) {
        ElevatorController bestController = null;
        int minCost = Integer.MAX_VALUE;

        for (ElevatorController ctrl : controllers) {
            ElevatorCar car = ctrl.getCar();
            int cost = calculateCost(car, floor, direction);
            
            if (cost < minCost) {
                minCost = cost;
                bestController = ctrl;
            }
        }
        return bestController;
    }

    private int calculateCost(ElevatorCar car, int targetFloor, Direction requestDir) {
        int currentFloor = car.getCurrentFloor();
        Direction carDir = car.getDirection();

        // 1. Elevator is Idle: Cost is direct distance
        if (carDir == Direction.IDLE) {
            return Math.abs(currentFloor - targetFloor);
        }

        // 2. Elevator is moving UP
        if (carDir == Direction.UP) {
            // Request is also for UP, and the elevator hasn't passed the floor yet
            if (requestDir == Direction.UP && currentFloor <= targetFloor) {
                return targetFloor - currentFloor;
            }
            // Request has passed or is opposite: penalty cost (needs to finish current run first)
            return 20 + Math.abs(currentFloor - targetFloor);
        }

        // 3. Elevator is moving DOWN
        if (carDir == Direction.DOWN) {
            // Request is also for DOWN, and the elevator hasn't passed the floor yet
            if (requestDir == Direction.DOWN && currentFloor >= targetFloor) {
                return currentFloor - targetFloor;
            }
            // Request has passed or is opposite: penalty cost
            return 20 + Math.abs(currentFloor - targetFloor);
        }

        return Integer.MAX_VALUE;
    }
}
