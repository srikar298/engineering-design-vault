package elevator;

import java.util.List;

public interface IDispatcher {
    ElevatorController dispatch(List<ElevatorController> controllers, int floor, Direction direction);
}
