package elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElevatorSystem {
    private static volatile ElevatorSystem instance = null;

    private final List<ElevatorController> controllers = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private IDispatcher dispatcher;

    private ElevatorSystem() {
        this.dispatcher = new NearestDispatcher();
    }

    public static ElevatorSystem getInstance() {
        if (instance == null) {
            synchronized (ElevatorSystem.class) {
                if (instance == null) {
                    instance = new ElevatorSystem();
                }
            }
        }
        return instance;
    }

    public void setDispatcher(IDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void addElevator(int id) {
        ElevatorController ctrl = new ElevatorController(id);
        controllers.add(ctrl);
        threadPool.submit(ctrl);
        System.out.printf("[System] Registered and started Elevator-%d.\n", id);
    }

    public void pressHallButton(int floor, Direction direction) {
        System.out.printf("[System] External Request: Floor %d, direction %s.\n", floor, direction);
        ElevatorController selected = dispatcher.dispatch(controllers, floor, direction);
        if (selected != null) {
            selected.addRequest(floor);
            System.out.printf("[System] Dispatched Elevator-%d to Floor %d.\n", selected.getCar().getId(), floor);
        } else {
            System.out.println("[System] No available elevator found!");
        }
    }

    public void pressCarButton(int elevatorId, int floor) {
        System.out.printf("[System] Internal Request: Elevator-%d select Floor %d.\n", elevatorId, floor);
        for (ElevatorController ctrl : controllers) {
            if (ctrl.getCar().getId() == elevatorId) {
                ctrl.addRequest(floor);
                return;
            }
        }
        System.out.printf("[System] Invalid Elevator ID: %d\n", elevatorId);
    }

    public void stop() {
        System.out.println("[System] Stopping all elevators...");
        for (ElevatorController ctrl : controllers) {
            ctrl.stopRunning();
        }
        threadPool.shutdownNow();
        System.out.println("[System] Elevator system shut down.");
    }
}
