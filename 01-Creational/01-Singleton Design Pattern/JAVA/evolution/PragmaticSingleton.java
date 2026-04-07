package evolution;

import java.io.Serializable;

/**
 * <h1>Pragmatic Singleton (SDE-2+ Level)</h1>
 * 
 * This class demonstrates the "Production Grade" way to write a non-enum Singleton.
 * While Enums are preferred (Stage 4), you may be forced to use a Class if you 
 * need to extend another class or have complex initialization logic.
 * 
 * Key Protections Included:
 * 1. Double-Checked Locking (Performance + Thread Safety)
 * 2. Volatile Keyword (Prevents instruction reordering)
 * 3. Reflection Protection (Throws exception if constructor called twice)
 * 4. Serialization Protection (readResolve)
 * 5. Cloning Protection (Override clone)
 */
public class PragmaticSingleton implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // 1. Volatile is CRITICAL for double-checked locking
    private static volatile PragmaticSingleton instance;

    private PragmaticSingleton() {
        // 2. REFLECTION PROTECTION
        // Even if someone uses Constructor.setAccessible(true), this prevents a 2nd instance
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        System.out.println("✅ PragmaticSingleton initialized with full protections.");
    }

    public static PragmaticSingleton getInstance() {
        if (instance == null) { // 1st check
            synchronized (PragmaticSingleton.class) {
                if (instance == null) { // 2nd check
                    instance = new PragmaticSingleton();
                }
            }
        }
        return instance;
    }

    /**
     * 3. SERIALIZATION PROTECTION
     * Without this, deserializing a Singleton creates a NEW instance.
     * The JVM calls this method during deserialization if it exists.
     */
    protected Object readResolve() {
        return getInstance();
    }

    /**
     * 4. CLONING PROTECTION
     * Prevents creating a copy of the singleton via Object.clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of Singleton is not allowed");
    }

    // Business Logic
    public void doWork() {
        System.out.println("Working...");
    }
}
