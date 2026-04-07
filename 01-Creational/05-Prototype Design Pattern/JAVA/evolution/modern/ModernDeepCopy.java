package evolution.modern;

import java.io.*;

/**
 * <h1>Modern SDE-2+ Deep Copy (Serialization Strategy)</h1>
 * 
 * The traditional clone() method is prone to "Shallow Copy" bugs. 
 * A pragmatic, bulletproof way to perform a DEEP COPY is via Serialization.
 * 
 * Note: While slower than manual cloning, this is generic and 
 * works for any depth of nested objects without writing boilerplate.
 */
public class ModernDeepCopy {

    /**
     * Deep Copy Utility Method
     * 1. Serialize the object into a byte array (In-Memory).
     * 2. Deserialize it back into a NEW object.
     * Result: A completely independent copy of the entire object graph.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(T object) {
        try {
            // Write to memory
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();

            // Read from memory
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bin);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        // Nested structure
        Stats stats = new Stats(); 
        stats.health = 100;
        
        GameNPC monster1 = new GameNPC("Dragon", stats);
        
        // DEEP COPY using Serialization
        GameNPC monster2 = deepCopy(monster1);
        
        // Modify M2 - M1 remains unchanged!
        monster2.stats.health = 50;
        
        System.out.println("✅ Deep Copy Successful:");
        System.out.println("M1 Health (Expected 100): " + monster1.stats.health);
        System.out.println("M2 Health (Expected 50):  " + monster2.stats.health);
        
        if (monster1.stats != monster2.stats) {
            System.out.println("SUCCESS: Objects are in independent memory locations.");
        }
    }
}

/**
 * Required to implement Serializable for the deepCopy utility.
 */
class Stats implements Serializable {
    public int health = 100;
}

class GameNPC implements Serializable {
    public String type;
    public Stats stats;

    public GameNPC(String type, Stats stats) {
        this.type = type;
        this.stats = stats;
    }

    @Override
    public String toString() {
        return type + " [Health: " + stats.health + "]";
    }
}
