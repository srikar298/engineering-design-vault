package pool;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>ConnectionPool — Singleton + Object Pool</h1>
 *
 * <p><b>Singleton Layer (Bill Pugh):</b> Only ONE pool exists per {@code jdbcUrl}.
 * Every thread/service that needs a DB connection goes through this single pool.
 *
 * <p><b>Object Pool Layer:</b> Instead of creating a new {@link DatabaseConnection}
 * on every request (50ms+ per creation), we maintain a fixed pool of pre-created
 * connections and recycle them.
 *
 * <h2>Why Singleton here?</h2>
 * <ul>
 *   <li>If two ConnectionPools exist for the same DB, you can exceed the DB's
 *       max-connection limit (e.g., PostgreSQL default is 100).</li>
 *   <li>The pool's internal state (which connections are free/in-use) must be
 *       globally consistent — impossible with multiple instances.</li>
 * </ul>
 */
public final class ConnectionPool {

    private static final int    DEFAULT_POOL_SIZE = 5;
    private static final String DEFAULT_JDBC_URL  = "jdbc:postgresql://localhost:5432/appdb";

    private final List<DatabaseConnection> pool;
    private final String                   jdbcUrl;
    private final int                      maxSize;

    // Private constructor — builds the pool eagerly at init time
    private ConnectionPool() {
        this.jdbcUrl = DEFAULT_JDBC_URL;
        this.maxSize = DEFAULT_POOL_SIZE;
        this.pool    = new ArrayList<>(maxSize);

        System.out.println("[Pool] Initializing connection pool (size=" + maxSize + ")...");
        for (int i = 0; i < maxSize; i++) {
            pool.add(new DatabaseConnection(jdbcUrl));
        }
        System.out.println("[Pool] Pool ready. All " + maxSize + " connections established.\n");
    }

    // Bill Pugh holder — thread-safe, lazy, lock-free
    private static final class InstanceHolder {
        private static final ConnectionPool INSTANCE = new ConnectionPool();
    }

    public static ConnectionPool getInstance() {
        return InstanceHolder.INSTANCE;
    }

    // ── Object Pool methods ───────────────────────────────────────────────────

    /**
     * Acquires a free connection from the pool.
     * In production: this would block or throw if no connection is free within a timeout.
     *
     * @return a {@link DatabaseConnection} marked as in-use
     * @throws IllegalStateException if no free connection is available
     */
    public synchronized DatabaseConnection acquire() {
        for (DatabaseConnection conn : pool) {
            if (!conn.isInUse()) {
                conn.markInUse();
                System.out.printf("[Pool] Acquired Connection #%d (free connections left: %d)%n",
                    conn.getId(), getFreeCount());
                return conn;
            }
        }
        throw new IllegalStateException(
            "[Pool] ALL " + maxSize + " connections are in use. Consider increasing pool size.");
    }

    /**
     * Returns a connection back to the pool so other threads can reuse it.
     * Critical: forgetting to release causes pool exhaustion (connection leaks).
     */
    public synchronized void release(DatabaseConnection conn) {
        conn.markFree();
        System.out.printf("[Pool] Released Connection #%d (free connections now: %d)%n",
            conn.getId(), getFreeCount());
    }

    public synchronized int getFreeCount() {
        return (int) pool.stream().filter(c -> !c.isInUse()).count();
    }

    public synchronized int getTotalSize() { return pool.size(); }

    /** Prints current pool state — useful for diagnostics */
    public synchronized void printStatus() {
        System.out.println("[Pool Status]");
        pool.forEach(c -> System.out.printf("  %s%n", c));
    }
}
