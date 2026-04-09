package evolution;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <h1>08 - Object Pool: The "Resource Scaler" (SDE-2+ Bonus)</h1>
 * 
 * <b>Scenario:</b> You have 10,000 users but only 10 Database Connections. 
 * Creating a new connection for every user is too slow (I/O heavy). 
 * You need a "Checkout/Checkin" system.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Wait vs. Fail:</b> Use a <code>BlockingQueue</code>. If the pool is empty, 
 *    threads will automatically wait until a connection is returned.
 * 2. <b>Lifecycle:</b> The Pool is usually a <b>Singleton</b>.
 * 3. <b>Eager vs Lazy:</b> Connections are often pre-warmed (Eager) to 
 *    avoid latency spikes on startup.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Starvation:</b> Queue prevents threads from spinning in a loop.
 * - <b>Leakage:</b> Ensures connections are returned even if business logic fails.
 */
class Connection {
    public final String id;
    public Connection(String id) { this.id = id; }
    public void execute(String sql) { System.out.println("Using " + id + " for: " + sql); }
}

public class ConnectionPool {
    // --- [INTERVIEW_MVP] (The Queue-based Storage) ---
    private final BlockingQueue<Connection> pool;
    private final int poolSize;

    private static volatile ConnectionPool instance;

    private ConnectionPool(int size) {
        this.poolSize = size;
        this.pool = new LinkedBlockingQueue<>(size);
        // Pre-warm the pool
        for (int i = 0; i < size; i++) {
            pool.add(new Connection("CONN-" + (i + 1)));
        }
    }

    // [PRODUCTION_ENHANCEMENT]: Singleton access to the pool
    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) instance = new ConnectionPool(5);
            }
        }
        return instance;
    }

    public Connection acquire() throws InterruptedException {
        // [INTERVIEW_MVP]: Wait if pool is empty
        return pool.take(); 
    }

    public void release(Connection connection) {
        // [PRODUCTION_ENHANCEMENT]: Validation before returning
        if (connection != null) {
            pool.offer(connection);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ConnectionPool manager = ConnectionPool.getInstance();
        
        // Use-case: Acquire, Use, Release
        Connection c = manager.acquire();
        c.execute("SELECT * FROM users");
        manager.release(c);
        
        System.out.println("✅ Resource managed safely.");
    }
}
