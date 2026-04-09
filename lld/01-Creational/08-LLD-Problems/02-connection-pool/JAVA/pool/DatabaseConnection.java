package pool;

import java.time.Instant;

/**
 * <h1>DatabaseConnection — The Pooled Resource</h1>
 *
 * <p>Represents an expensive-to-create database connection.
 * In production this wraps a JDBC {@code Connection}.
 * Here, we simulate cost with a 50ms sleep during construction.
 */
public final class DatabaseConnection {

    private static int idCounter = 0;

    private final int    id;
    private final String jdbcUrl;
    private       boolean inUse;
    private       Instant lastUsed;

    DatabaseConnection(String jdbcUrl) {
        this.id      = ++idCounter;
        this.jdbcUrl = jdbcUrl;
        this.inUse   = false;
        this.lastUsed = Instant.now();

        // Simulate expensive TCP handshake + auth
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        System.out.printf("  [Connection #%d] Established to %s%n", id, jdbcUrl);
    }

    /** Execute a simulated query */
    public void query(String sql) {
        if (!inUse) throw new IllegalStateException("Connection #" + id + " not acquired!");
        System.out.printf("  [Connection #%d] Executing: %s%n", id, sql);
    }

    // ── Pool-internal methods (package-private) ──────────────────────────────
    boolean isInUse()    { return inUse; }
    void    markInUse()  { this.inUse = true;  this.lastUsed = Instant.now(); }
    void    markFree()   { this.inUse = false; this.lastUsed = Instant.now(); }
    int     getId()      { return id; }

    @Override
    public String toString() {
        return String.format("Connection{id=%d, inUse=%s, lastUsed=%s}", id, inUse, lastUsed);
    }
}
