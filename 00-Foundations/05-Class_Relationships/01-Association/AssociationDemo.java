import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * 🏥 ASSOCIATION MASTERY: The Hospital Appointment System
 * ============================================================================
 * Demonstrates all four association patterns:
 *   1. Unidirectional  (Appointment --> Room)
 *   2. Bidirectional   (Doctor <--> Appointment <--> Patient)
 *   3. One-to-Many     (1 Doctor : * Appointments)
 *   4. Many-to-Many    (Doctor <--> Patient via Appointment as intermediary)
 * ============================================================================
 */

// ── ROOM ────────────────────────────────────────────────────────────────────
// Simple value object. Intentionally unaware of Appointments (unidirectional).
class Room {
    private final String number;
    private final int floor;

    public Room(String number, int floor) {
        this.number = number;
        this.floor = floor;
    }
    public String getNumber() { return number; }
    public int getFloor()     { return floor; }
    @Override public String toString() { return "Room " + number + " (Floor " + floor + ")"; }
}

// ── APPOINTMENT (The Intermediary / Join-Entity) ──────────────────────────
/**
 * Acts as the join-entity between Doctor and Patient.
 * This pattern solves three problems:
 * 1. Eliminates a direct Doctor <-> Patient circular many-to-many reference.
 * 2. Allows the relationship to carry rich metadata (time, room, status).
 * 3. Registers itself on both sides automatically inside the constructor.
 */
class Appointment {
    private final Doctor doctor;
    private final Patient patient;
    private final Room room;      // Unidirectional: Appointment knows Room, Room doesn't care
    private final String time;
    private String status;

    public Appointment(Doctor doctor, Patient patient, Room room, String time) {
        this.doctor  = doctor;
        this.patient = patient;
        this.room    = room;
        this.time    = time;
        this.status  = "SCHEDULED";

        // Self-registration: automatically update both sides of the bidirectional link.
        doctor.addAppointment(this);
        patient.addAppointment(this);
    }

    public Doctor  getDoctor()  { return doctor; }
    public Patient getPatient() { return patient; }
    public Room    getRoom()    { return room; }
    public String  getTime()    { return time; }
    public String  getStatus()  { return status; }
    public void    complete()   { this.status = "COMPLETED"; }

    @Override
    public String toString() {
        return String.format("[%s] %s with %s in %s", time, doctor.getName(), patient.getName(), room);
    }
}

// ── DOCTOR ──────────────────────────────────────────────────────────────────
class Doctor {
    private final String name;
    private final String specialization;
    private final List<Appointment> appointments = new ArrayList<>();

    public Doctor(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    // Called by Appointment's constructor — keeps sync automatic and safe
    void addAppointment(Appointment appt) {
        appointments.add(appt);
    }

    /**
     * Many-to-many navigation without a direct Patient reference.
     * We walk through Appointment objects to reach Patients.
     */
    public List<Patient> getPatients() {
        return appointments.stream()
            .map(Appointment::getPatient)
            .distinct()
            .collect(Collectors.toList());
    }

    public String getName()              { return name; }
    public String getSpecialization()    { return specialization; }
    public List<Appointment> getSchedule() { return List.copyOf(appointments); }
}

// ── PATIENT ──────────────────────────────────────────────────────────────────
class Patient {
    private final String name;
    private final List<Appointment> appointments = new ArrayList<>();

    public Patient(String name) { this.name = name; }

    void addAppointment(Appointment appt) {
        appointments.add(appt);
    }

    public List<Doctor> getDoctors() {
        return appointments.stream()
            .map(Appointment::getDoctor)
            .distinct()
            .collect(Collectors.toList());
    }

    public String getName()                { return name; }
    public List<Appointment> getHistory()  { return List.copyOf(appointments); }
}

// ── EXECUTION ────────────────────────────────────────────────────────────────
public class AssociationDemo {
    public static void main(String[] args) {
        // --- Entities ---
        Doctor drSmith = new Doctor("Dr. Smith", "Cardiology");
        Doctor drPatel = new Doctor("Dr. Patel", "Neurology");

        Patient alice = new Patient("Alice");
        Patient bob   = new Patient("Bob");

        Room r101 = new Room("101", 1);
        Room r205 = new Room("205", 2);

        // --- Associating via Appointment intermediary ---
        // Self-registration inside constructor keeps both sides automatically in sync.
        new Appointment(drSmith, alice, r101, "09:00 AM");
        new Appointment(drSmith, bob,   r101, "10:00 AM");
        new Appointment(drPatel, alice, r205, "02:00 PM");

        // --- Navigation: Doctor side ---
        System.out.println("=== Dr. Smith's Schedule ===");
        drSmith.getSchedule().forEach(a ->
            System.out.printf("  %s | Patient: %-6s | %s%n",
                a.getTime(), a.getPatient().getName(), a.getRoom()));

        System.out.println("\n=== Dr. Smith's Patients (via stream) ===");
        drSmith.getPatients().forEach(p -> System.out.println("  - " + p.getName()));

        // --- Navigation: Patient side ---
        System.out.println("\n=== Alice's Doctors (via stream) ===");
        alice.getDoctors().forEach(d ->
            System.out.println("  - " + d.getName() + " (" + d.getSpecialization() + ")"));

        // --- Demonstrating unidirectionality: Room is blissfully unaware ---
        System.out.println("\n=== Room 101 knows nothing about appointments ===");
        System.out.println("  Room 101 has no appointments list: " + r101);

        // --- Adding metadata to the relationship (advantage of intermediary) ---
        System.out.println("\n=== Completing Alice's cardiology appointment ===");
        alice.getHistory().get(0).complete();
        System.out.println("  Status: " + alice.getHistory().get(0).getStatus());
    }
}
