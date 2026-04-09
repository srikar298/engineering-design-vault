package evolution.modern;

import java.io.*;

/**
 * <h1>Modern SDE-2+ Deep Copy (Prototype Variation)</h1>
 * 
 * <b>Why this is Senior-Level:</b>
 * Java's built-in <code>Cloneable</code> is broken (shallow copy, throws exceptions).
 * Senior engineers use <b>Serialization</b> or <b>Copy Constructors</b> for deep cloning.
 * 
 * <b>Strategy:</b>
 * 1. Implement basic serialization roundtrip for MVP.
 * 2. Add Generic type-safety and robust error handling for Production.
 */
public class ModernDeepCopy {

    /**
     * [INTERVIEW_MVP]: The core "Cheat" for a 100% deep copy.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(T object) {
        try {
            // Write object to memory
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();

            // Read object back from memory (creates a 100% independent copy)
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bin);
            return (T) ois.readObject();
        } catch (Exception e) {
            // --- [PRODUCTION_ENHANCEMENT] (Custom Error Handling) ---
            throw new RuntimeException("Deep copy failed due to serialization error: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        Stats stats = new Stats(); 
        stats.health = 100;
        GameNPC dragon = new GameNPC("Dragon", stats);
        
        // Performance Note: Serialization is slower than manual cloning but 
        // bulletproof for complex nested object graphs.
        GameNPC clone = deepCopy(dragon);
        clone.stats.health = 50;
        
        System.out.println("✅ Original Health (Expected 100): " + dragon.stats.health);
        System.out.println("✅ Clone Health (Expected 50): " + clone.stats.health);
    }
}

class Stats implements Serializable { public int health; }
class GameNPC implements Serializable {
    public String type;
    public Stats stats;
    public GameNPC(String t, Stats s) { this.type = t; this.stats = s; }
}
