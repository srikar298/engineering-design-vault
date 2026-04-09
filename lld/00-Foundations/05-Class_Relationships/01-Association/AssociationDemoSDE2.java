
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>01 - Association: Peer-to-Peer Interaction (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Hospital Appointment System. 
 * Doctors and Patients are independent peers. They are linked via an Appointment.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Join Entities:</b> Instead of a direct many-to-many list (Doctor.patients), 
 *    use an intermediary (Appointment). This matches how Database Join Tables work.
 * 2. <b>Bidirectional Sync:</b> If A knows B, and B knows A, the constructor of the 
 *    intermediary should update both sides to ensure data consistency.
 * 3. <b>Loose Coupling:</b> Doctors and Patients don't need to know each other's 
 *    internals; they only know about the "Contract" (the Appointment).
 */

class Room {
    private final String number;
    public Room(String n) { this.number = n; }
    @Override public String toString() { return "Room " + number; }
}

class Appointment {
    // --- [INTERVIEW_MVP] (The Peer References) ---
    private final Doctor doctor;
    private final Patient patient;
    private final Room room;

    public Appointment(Doctor d, Patient p, Room r) {
        this.doctor = d;
        this.patient = p;
        this.room = r;

        // --- [PRODUCTION_ENHANCEMENT] (Automatic Bidirectional Sync) ---
        // Ensures both peers are updated as soon as the association is created.
        d.addAppointment(this);
        p.addAppointment(this);
    }

    public Doctor getDoctor() { return doctor; }
    public Patient getPatient() { return patient; }
    public Room getRoom() { return room; }
}

class Doctor {
    private final String name;
    private final List<Appointment> appointments = new ArrayList<>();

    public Doctor(String n) { this.name = n; }
    public void addAppointment(Appointment a) { appointments.add(a); }
    public String getName() { return name; }

    /** [PRODUCTION_ENHANCEMENT]: Navigate to peers via the association */
    public List<Patient> getPatients() {
        return appointments.stream().map(Appointment::getPatient).collect(Collectors.toList());
    }
}

class Patient {
    private final String name;
    private final List<Appointment> appointments = new ArrayList<>();

    public Patient(String n) { this.name = n; }
    public void addAppointment(Appointment a) { appointments.add(a); }
    public String getName() { return name; }
}

public class AssociationDemoSDE2 {
    public static void main(String[] args) {
        Doctor doc = new Doctor("Dr. House");
        Patient patient = new Patient("Wilson");
        Room r1 = new Room("101");

        // [INTERVIEW_MVP]: Creating the association
        new Appointment(doc, patient, r1);

        System.out.println("✅ Association created via Appointment intermediary.");
        System.out.println("Doctor " + doc.getName() + " has patient: " + doc.getPatients().get(0).getName());
    }
}
