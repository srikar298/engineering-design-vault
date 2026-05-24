package parking;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DisplayBoard {
    private final Map<ParkingSpotType, AtomicInteger> freeCounts = new ConcurrentHashMap<>();

    public DisplayBoard() {
        for (ParkingSpotType type : ParkingSpotType.values()) {
            freeCounts.put(type, new AtomicInteger(0));
        }
    }

    public void update(ParkingSpotType type, int delta) {
        freeCounts.get(type).addAndGet(delta);
    }

    public int getFreeCount(ParkingSpotType type) {
        return freeCounts.get(type).get();
    }

    public void show() {
        System.out.println("┌───────────────────────────────────┐");
        System.out.println("│      Floor Availability Board     │");
        System.out.println("├───────────────────────────────────┤");
        for (Map.Entry<ParkingSpotType, AtomicInteger> entry : freeCounts.entrySet()) {
            System.out.printf("│  %-11s: %-16d │\n", entry.getKey(), entry.getValue().get());
        }
        System.out.println("└───────────────────────────────────┘");
    }
}
