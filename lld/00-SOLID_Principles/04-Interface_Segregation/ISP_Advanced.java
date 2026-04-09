import java.util.*;

/**
 * ============================================================================
 * 🎓 ISP MASTERY: Worker System + CQRS Repository Pattern
 * ============================================================================
 * Two realistic enterprise ISP examples:
 *
 * Example 1 → Worker system: RobotWorker forced to implement eat/sleep
 *             Fixed with Workable, Feedable, Restable role interfaces.
 *
 * Example 2 → Repository pattern: Read/Write segregation (CQRS preview)
 *             A ReadOnlyCacheRepo only needs read methods — not write.
 * ============================================================================
 */

// ─────────────────────────────────────────────────────────────
// VIOLATION 1: The Fat IWorker Interface
// ─────────────────────────────────────────────────────────────

interface IWorker {
    void work();
    void eat();    // ❌ RobotWorker doesn't eat
    void sleep();  // ❌ RobotWorker doesn't sleep
    void submitTimesheet(); // ❌ ContractWorker has different payroll
}

// ❌ VIOLATION: Robot must implement behaviors it physically cannot do
class RobotWorkerViolation implements IWorker {
    @Override public void work()            { System.out.println("[ROBOT] Running task..."); }
    @Override public void eat()             { throw new UnsupportedOperationException("Robots don't eat!"); }
    @Override public void sleep()           { throw new UnsupportedOperationException("Robots don't sleep!"); }
    @Override public void submitTimesheet() { throw new UnsupportedOperationException("Robots have no timesheet!"); }
}

// ─────────────────────────────────────────────────────────────
// SOLUTION 1: Role Interfaces (ISP-compliant Worker system)
// ─────────────────────────────────────────────────────────────

interface Workable        { void work(); }
interface Feedable        { void eat(); }
interface Restable        { void sleep(); }
interface TimesheetFiler  { void submitTimesheet(); }

// ✅ Robots: only implement what they actually do
class RobotWorker implements Workable {
    private final String robotId;
    public RobotWorker(String id) { this.robotId = id; }
    @Override public void work() {
        System.out.println("[ROBOT-" + robotId + "] Executing automated task cycle...");
    }
}

// ✅ Human employees implement all relevant roles
class HumanEmployee implements Workable, Feedable, Restable, TimesheetFiler {
    private final String name;
    public HumanEmployee(String name) { this.name = name; }
    @Override public void work()            { System.out.println("[" + name + "] Working on feature..."); }
    @Override public void eat()             { System.out.println("[" + name + "] Taking lunch break..."); }
    @Override public void sleep()           { System.out.println("[" + name + "] Sleep logged at 11 PM."); }
    @Override public void submitTimesheet() { System.out.println("[" + name + "] Timesheet submitted for payroll."); }
}

// ✅ Callers only depend on the role they need — not the entire contract
class AutomationOrchestrator {
    private final List<Workable> workers;
    public AutomationOrchestrator(List<Workable> workers) { this.workers = workers; }
    public void runAllTasks() { workers.forEach(Workable::work); }
}

class HRPayrollSystem {
    // Payroll only cares about TimesheetFiler — no knowledge of eat/sleep needed
    public void processTimesheets(List<TimesheetFiler> filers) {
        filers.forEach(TimesheetFiler::submitTimesheet);
    }
}


// ─────────────────────────────────────────────────────────────
// EXAMPLE 2: Read/Write Repository Segregation (CQRS Pattern)
// ─────────────────────────────────────────────────────────────

// ❌ FAT repository — forces read-only cache to stub write methods
interface UserRepository {
    void save(String user);
    void delete(String userId);
    String findById(String userId);
    List<String> findAll();
    void update(String userId, String newData);
}

// ✅ Segregated repositories — each client sees only what it needs
interface ReadUserRepository {
    String findById(String userId);
    List<String> findAll();
}

interface WriteUserRepository {
    void save(String user);
    void delete(String userId);
    void update(String userId, String newData);
}

// Full DB implementation: implements both (for write-capable services)
class MySQLUserRepository implements ReadUserRepository, WriteUserRepository {
    private final Map<String, String> db = new HashMap<>();
    @Override public void save(String user)                     { db.put(user, user); System.out.println("[MYSQL] Saved: " + user); }
    @Override public void delete(String userId)                 { db.remove(userId); System.out.println("[MYSQL] Deleted: " + userId); }
    @Override public String findById(String userId)             { return db.getOrDefault(userId, null); }
    @Override public List<String> findAll()                     { return new ArrayList<>(db.values()); }
    @Override public void update(String userId, String newData) { db.put(userId, newData); System.out.println("[MYSQL] Updated: " + userId); }
}

// ✅ Read-only cache: only implements ReadUserRepository — zero stub methods
class RedisReadCache implements ReadUserRepository {
    private final Map<String, String> cache = new HashMap<>(Map.of("USR-1", "Alice", "USR-2", "Bob"));
    @Override public String findById(String userId) {
        System.out.println("[REDIS] Cache hit for: " + userId);
        return cache.getOrDefault(userId, null);
    }
    @Override public List<String> findAll() {
        return new ArrayList<>(cache.values());
    }
}

// ─────────────────────────────────────────────────────────────
// 🚀 EXECUTION
// ─────────────────────────────────────────────────────────────
public class ISP_Advanced {
    public static void main(String[] args) {

        System.out.println("=== ❌ Fat Interface: RobotWorker forced to implement eat/sleep ===");
        IWorker badRobot = new RobotWorkerViolation();
        badRobot.work();
        try { badRobot.eat(); } catch (UnsupportedOperationException e) {
            System.out.println("  ISP VIOLATION: " + e.getMessage());
        }

        System.out.println("\n=== ✅ Role Interfaces: Automation Orchestrator ===");
        List<Workable> workforce = Arrays.asList(
            new RobotWorker("R2D2"),
            new RobotWorker("C3PO"),
            new HumanEmployee("Alice")
        );
        new AutomationOrchestrator(workforce).runAllTasks();

        System.out.println("\n=== ✅ Role Interfaces: HR Payroll (humans only) ===");
        List<TimesheetFiler> filers = Arrays.asList(
            new HumanEmployee("Bob"),
            new HumanEmployee("Carol")
            // RobotWorker cannot appear here — type system prevents it!
        );
        new HRPayrollSystem().processTimesheets(filers);

        System.out.println("\n=== ✅ CQRS Repository Segregation ===");
        MySQLUserRepository mysql = new MySQLUserRepository();
        mysql.save("USR-3:David");

        ReadUserRepository cache = new RedisReadCache();
        System.out.println("Cache findById USR-1: " + cache.findById("USR-1"));
        System.out.println("Cache findAll: " + cache.findAll());
        // cache.save(...)  // ✅ COMPILE ERROR — RedisReadCache doesn't expose write methods!
    }
}
