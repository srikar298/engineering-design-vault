package pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <h1>LLD Problem: Database Connection Pool (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> You are building a high-performance DB driver. Creating physical 
 * connections is slow. You must manage a pool of 10 connections for 10k users.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Patterns:</b> Combination of <b>Singleton</b> (the pool) and <b>Object Pool</b>.
 * 2. <b>Concurrency:</b> Use <code>BlockingQueue</code> to handle thread waiting automatically.
 * 3. <b>Resource Leakage:</b> In a real system, use <b>Try-with-Resources</b> or 
 *    a <b>Proxy</b> to ensure the connection is returned to the pool automatically.
 * 
 * <b>Edge Cases:</b>
 * - <b>Pool Exhaustion:</b> What if all connections are busy? (Threads must wait).
 * - <b>Timeout:</b> Prevent threads from waiting forever.
 */

class DbConnection {
    private final String id;
    public DbConnection(String id) { this.id = id; }
    public void execute(String sql) { System.out.println(id + " executing: " + sql); }
}

public class ConnectionPoolManager {
    // --- [INTERVIEW_MVP] (Thread-Safe Storage) ---
    private final BlockingQueue<DbConnection> pool;
    private static volatile ConnectionPoolManager instance;

    private ConnectionPoolManager(int size) {
        this.pool = new LinkedBlockingQueue<>(size);
        for (int i = 1; i <= size; i++) {
            pool.add(new DbConnection("CONN-" + i));
        }
    }

    public static ConnectionPoolManager getInstance() {
        if (instance == null) {
            synchronized (ConnectionPoolManager.class) {
                if (instance == null) instance = new ConnectionPoolManager(5);
            }
        }
        return instance;
    }

    /**
     * [INTERVIEW_MVP]: Acquire a resource.
     * [PRODUCTION_ENHANCEMENT]: Added timeout to prevent thread starvation.
     */
    public DbConnection acquire(long timeoutMs) throws InterruptedException {
        DbConnection conn = pool.poll(timeoutMs, TimeUnit.MILLISECONDS);
        if (conn == null) throw new RuntimeException("Timeout: No connections available.");
        return conn;
    }

    public void release(DbConnection conn) {
        // [PRODUCTION_ENHANCEMENT]: Sanitization before returning to pool
        if (conn != null) pool.offer(conn);
    }

    public static void main(String[] args) throws InterruptedException {
        ConnectionPoolManager pool = ConnectionPoolManager.getInstance();
        
        // MVP Usage
        DbConnection c = pool.acquire(1000);
        c.execute("SELECT * FROM orders");
        pool.release(c);
        
        System.out.println("✅ Connection Pool LLD completed successfully.");
    }
}
