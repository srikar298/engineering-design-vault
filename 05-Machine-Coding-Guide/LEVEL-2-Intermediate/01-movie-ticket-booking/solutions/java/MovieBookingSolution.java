package movie_booking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h1>Gold Standard: Movie Ticket Booking</h1>
 */

enum SeatStatus { AVAILABLE, BOOKED, BLOCKED }

class Seat {
    private final String id;
    private final String type; // Silver, Gold, etc.
    // [PRODUCTION_ENHANCEMENT]: We don't store status here because 
    // a physical seat is available for MANY different shows at different times.
    public Seat(String id, String type) { this.id = id; this.type = type; }
    public String getId() { return id; }
}

class Show {
    private final String showId;
    private final Map<String, SeatStatus> seatMap = new ConcurrentHashMap<>();
    private final Lock bookingLock = new ReentrantLock();

    public Show(String id, List<Seat> seats) {
        this.showId = id;
        seats.forEach(s -> seatMap.put(s.getId(), SeatStatus.AVAILABLE));
    }

    /**
     * [INTERVIEW_MVP]: Atomic multi-seat booking.
     */
    public boolean bookSeats(List<String> seatIds) {
        // [PRODUCTION_ENHANCEMENT]: Using a lock to ensure thread-safety
        bookingLock.lock();
        try {
            // 1. Validate all seats are available
            for (String id : seatIds) {
                if (seatMap.get(id) != SeatStatus.AVAILABLE) return false;
            }
            // 2. Perform the booking
            seatIds.forEach(id -> seatMap.put(id, SeatStatus.BOOKED));
            return true;
        } finally {
            bookingLock.unlock();
        }
    }
}

class BookingService {
    private final Map<String, Show> shows = new HashMap<>();

    public void addShow(Show s, String id) { shows.put(id, s); }

    public void makeBooking(String showId, List<String> seatIds) {
        Show show = shows.get(showId);
        if (show == null) throw new IllegalArgumentException("Show not found.");

        if (show.bookSeats(seatIds)) {
            System.out.println("✅ Booking Successful for: " + seatIds);
        } else {
            System.out.println("❌ Booking Failed: One or more seats unavailable.");
        }
    }
}

public class MovieBookingSolution {
    public static void main(String[] args) {
        List<Seat> physicalSeats = Arrays.asList(new Seat("A1", "Gold"), new Seat("A2", "Gold"));
        Show eveningShow = new Show("SHOW-123", physicalSeats);

        BookingService service = new BookingService();
        service.addShow(eveningShow, "SHOW-123");

        // [INTERVIEW_MVP]: Standard Booking
        service.makeBooking("SHOW-123", Arrays.asList("A1", "A2"));

        // [PRODUCTION_ENHANCEMENT]: Failure Case (Double Booking)
        service.makeBooking("SHOW-123", Arrays.asList("A1")); // Should fail
    }
}
