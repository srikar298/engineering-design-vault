/**
 * ============================================================================
 * ✅ REFACTORED: Behavioral Segregation (LSP)
 * ============================================================================
 * 
 * SOLUTION:
 * Don't force 'Ostrich' to fly just because it's a 'Bird'. 
 * Split the behaviors into lean interfaces.
 */

import java.util.*;

interface Bird {
    void eat();
}

interface Flyable {
    void fly();
}

class Parrot implements Bird, Flyable {
    @Override
    public void eat() { System.out.println("Parrot is eating seeds."); }
    
    @Override
    public void fly() { System.out.println("Parrot is flying high!"); }
}

class Ostrich implements Bird {
    @Override
    public void eat() { System.out.println("Ostrich is eating grass."); }
    // Ostrich does NOT implement Flyable. 
    // This is LSP-safe because we never promise 'fly()' capability.
}

public class LSP_Correct_Hierarchy {
    static void makeBirdsFly(List<Flyable> flyingBirds) {
        for (Flyable bird : flyingBirds) {
            bird.fly();
        }
    }

    public static void main(String[] args) {
        List<Flyable> flyingBirds = new ArrayList<>();
        flyingBirds.add(new Parrot());
        // flyingBirds.add(new Ostrich()); // 🛡️ COMPILE ERR: Ostrich is not Flyable.
        
        makeBirdsFly(flyingBirds);
    }
}
/**
 * SENIOR INSIGHT:
 * LSP violation is often a sign that your inheritance is too broad.
 * If you find yourself throwing 'UnsupportedOperationException', 
 * you have a leaky abstraction that violates LSP.
 */
