package evolution.stage2;

/**
 * Stage 2: Copy Constructors (The "Senior" way)
 * 
 * This is the preferred way in modern Java (Joshua Bloch's recommendation).
 * It's safer, doesn't require casting, and doesn't throw checked exceptions.
 */

class StatsModern {
    public int health;

    public StatsModern(int health) {
        this.health = health;
    }

    // Copy Constructor
    public StatsModern(StatsModern other) {
        this.health = other.health;
    }
}

class GameNPCModern {
    private String type;
    private StatsModern stats;

    public GameNPCModern(String type, StatsModern stats) {
        this.type = type;
        this.stats = stats;
    }

    // Copy Constructor (Handles Deep Cloning)
    public GameNPCModern(GameNPCModern other) {
        this.type = other.type;
        // Deep copy nested object using its own copy constructor
        this.stats = new StatsModern(other.stats);
    }

    @Override
    public String toString() {
        return type + " [Health: " + stats.health + "]";
    }

    public void setHealth(int h) { this.stats.health = h; }
}

public class Stage2CopyConstructor {
    public static void main(String[] args) {
        StatsModern s = new StatsModern(150);
        GameNPCModern m1 = new GameNPCModern("Dragon", s);
        
        // Clean and simple deep copy
        GameNPCModern m2 = new GameNPCModern(m1);

        m2.setHealth(300);

        System.out.println("Stage 2 (Copy Constructor):");
        System.out.println("M1: " + m1);
        System.out.println("M2: " + m2);
    }
}
