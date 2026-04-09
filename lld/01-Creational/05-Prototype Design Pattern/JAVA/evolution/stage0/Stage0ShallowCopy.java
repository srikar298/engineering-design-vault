package evolution.stage0;

/**
 * Stage 0: Shallow Copy Mess (The "Shared Memory" Bug)
 * 
 * The Problem: When we clone an object that has nested reference types
 * (like an inner Position or Stats object), a "shallow" clone just
 * copies the reference. Both the original and the clone now point to
 * the SAME nested object!
 */

class Stats {
    public int health = 100;
}

class GameNPC {
    public String type;
    public Stats stats; // Nested object

    public GameNPC(String type, Stats stats) {
        this.type = type;
        this.stats = stats;
    }

    // Shallow Clone
    public GameNPC shallowClone() {
        return new GameNPC(this.type, this.stats);
    }

    @Override
    public String toString() {
        return type + " [Health: " + stats.health + "]";
    }
}

public class Stage0ShallowCopy {
    public static void main(String[] args) {
        Stats commonStats = new Stats();
        GameNPC monster1 = new GameNPC("Goblin", commonStats);
        
        // Clone the monster
        GameNPC monster2 = monster1.shallowClone();
        
        System.out.println("Before modification:");
        System.out.println("M1: " + monster1);
        System.out.println("M2: " + monster2);

        // Oops! Modifying M2 also affects M1 because they share the same stats object!
        monster2.stats.health = 50;

        System.out.println("\nAfter M2 health boost (The Bug):");
        System.out.println("M1: " + monster1); // M1's health also dropped to 50!
        System.out.println("M2: " + monster2);
        
        if (monster1.stats == monster2.stats) {
            System.err.println("\nBUG FOUND: Both monsters share the same Stats instance (Reference Equality).");
        }
    }
}
