package elevator;

public class ElevatorCar {
    private final int id;
    private volatile int currentFloor;
    private volatile Direction direction;
    private volatile DoorState doorState;

    public ElevatorCar(int id) {
        this.id = id;
        this.currentFloor = 0; // Starts at ground floor
        this.direction = Direction.IDLE;
        this.doorState = DoorState.CLOSED;
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public DoorState getDoorState() {
        return doorState;
    }

    public void openDoor() {
        this.doorState = DoorState.OPEN;
        System.out.printf("[Elevator-%d] Doors OPENED at Floor %d.\n", id, currentFloor);
    }

    public void closeDoor() {
        this.doorState = DoorState.CLOSED;
        System.out.printf("[Elevator-%d] Doors CLOSED at Floor %d.\n", id, currentFloor);
    }

    public void stepUp() {
        this.currentFloor++;
        System.out.printf("[Elevator-%d] Moving UP. Reached Floor %d.\n", id, currentFloor);
    }

    public void stepDown() {
        this.currentFloor--;
        System.out.printf("[Elevator-%d] Moving DOWN. Reached Floor %d.\n", id, currentFloor);
    }
}
