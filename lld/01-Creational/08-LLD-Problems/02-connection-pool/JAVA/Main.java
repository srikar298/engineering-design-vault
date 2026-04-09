import pool.ConnectionPool;
import pool.DatabaseConnection;

/**
 * <h1>Connection Pool — Main Demo</h1>
 *
 * <p><b>Patterns at work:</b>
 * <ul>
 *   <li><b>Singleton (Bill Pugh)</b> — One pool instance per JVM.</li>
 *   <li><b>Object Pool</b> — Connections are reused, not re-created.</li>
 * </ul>
 *
 * <p>Watch the output — notice that connections are NEVER re-initialized
 * after the pool boots. Acquisition and release simply toggle the in-use flag.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       🗄️  Database Connection Pool Demo          ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        ConnectionPool pool = ConnectionPool.getInstance();
        pool.printStatus();
        System.out.println();

        // ── Acquire 3 connections ─────────────────────────────────────────────
        System.out.println("--- Acquiring 3 connections ---");
        DatabaseConnection c1 = pool.acquire();
        DatabaseConnection c2 = pool.acquire();
        DatabaseConnection c3 = pool.acquire();

        // ── Use them ──────────────────────────────────────────────────────────
        System.out.println("\n--- Using connections ---");
        c1.query("SELECT * FROM orders WHERE status='PENDING'");
        c2.query("UPDATE products SET stock = stock - 1 WHERE id = 42");
        c3.query("INSERT INTO audit_log (event) VALUES ('payment_processed')");

        // ── Release back to pool ──────────────────────────────────────────────
        System.out.println("\n--- Releasing connections ---");
        pool.release(c1);
        pool.release(c2);

        // ── Singleton proof: getInstance() returns the SAME pool ──────────────
        System.out.println("\n--- Singleton Proof ---");
        ConnectionPool pool2 = ConnectionPool.getInstance();
        System.out.println("Same pool instance: " + (pool == pool2));
        System.out.println("Free connections in pool2: " + pool2.getFreeCount());  // sees c1, c2 released

        // ── Re-acquire a previously released connection (no new creation!) ─────
        System.out.println("\n--- Re-acquiring released connection ---");
        DatabaseConnection reused = pool.acquire();
        reused.query("SELECT count(*) FROM sessions");
        pool.release(reused);
        pool.release(c3);

        System.out.println("\n--- Final Pool Status ---");
        pool.printStatus();
        System.out.println("\n✅ Notice: Pool created 5 connections at startup. Zero new connections since.");
    }
}
