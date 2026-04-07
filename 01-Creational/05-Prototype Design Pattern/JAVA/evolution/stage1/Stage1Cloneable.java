package evolution.stage1;

/**
 * Stage 1: Java's Cloneable (The "Legacy" way)
 * 
 * The Problem: The Cloneable interface is considered broken in Java.
 * It doesn't actually have a clone() method; it's just a marker.
 * You still have to override Object.clone() and handle CloneNotSupportedException.
 */

class StatsLegacy implements Cloneable {
    public int health = 100;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class GameNPCLegacy implements Cloneable {
    public String type;
    public StatsLegacy stats;

    public GameNPCLegacy(String type, StatsLegacy stats) {
        this.type = type;
        this.stats = stats;
    }

    @Override
    public Object clone() {
        try {
            // super.clone() only does a shallow copy
            GameNPCLegacy cloned = (GameNPCLegacy) super.clone();
            // We MUST manually deep clone nested objects!
            cloned.stats = (StatsLegacy) this.stats.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return type + " [Health: " + stats.health + "]";
    }
}

public class Stage1Cloneable {
    public static void main(String[] args) {
        StatsLegacy stats = new StatsLegacy();
        GameNPCLegacy m1 = new GameNPCLegacy("Orc", stats);
        GameNPCLegacy m2 = (GameNPCLegacy) m1.clone();

        m2.stats.health = 200;

        System.out.println("Stage 1 (Cloneable deep copy):");
        System.out.println("M1: " + m1); // Remains 100
        System.out.println("M2: " + m2); // Becomes 200
    }
}
