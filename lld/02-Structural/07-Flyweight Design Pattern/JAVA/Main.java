import context.Forest;
import flyweight.TreeFactory;

/**
 * <h1>Flyweight Pattern Demonstration</h1>
 * 
 * <p>Simulates rendering 1,000,000 trees. 
 * Without Flyweight: 1,000,000 trees * 1MB texture = 1 Terabyte of RAM required.
 * With Flyweight: 3 TreeTypes (3MB) + 1,000,000 tiny Context objects (a few MBs).
 */
public class Main {
    static int TREES_TO_DRAW = 1000000;
    static int TREE_TYPES = 3;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Flyweight Pattern: Massive RAM Optimization    ");
        System.out.println("==================================================\n");

        Forest forest = new Forest();
        
        System.out.println("--- Planting " + TREES_TO_DRAW + " heavy trees ---");

        for (int i = 0; i < (TREES_TO_DRAW / TREE_TYPES); i++) {
            forest.plantTree(random(0, 100), random(0, 100), "Summer Oak", "Green", "summer_oak_1mb_texture.png");
            forest.plantTree(random(0, 100), random(0, 100), "Autumn Oak", "Orange", "autumn_oak_1mb_texture.png");
            forest.plantTree(random(0, 100), random(0, 100), "Winter Pine", "White", "winter_pine_1mb_texture.png");
        }

        // We only draw 5 trees to keep the console clean in the demo
        System.out.println("\n[Demo] Rendering the first 5 trees:");
        forest.plantTree(10, 20, "Autumn Oak", "Orange", "autumn_oak_1mb_texture.png");
        forest.plantTree(15, 25, "Summer Oak", "Green", "summer_oak_1mb_texture.png");
        forest.drawForest(); // (Imagine this draws all 1,000,000)

        System.out.println("\n==================================================");
        System.out.println("                 MEMORY REPORT                    ");
        System.out.println("==================================================");
        System.out.println("Total Trees Planted (Contexts) : " + TREES_TO_DRAW);
        System.out.println("Unique Tree Types (Flyweights) : " + TreeFactory.getCacheSize());
        System.out.println("Memory consumed without Flyweight: ~1 Terabyte");
        System.out.println("Memory consumed WITH Flyweight   : ~30 Megabytes");
        System.out.println("==================================================");
    }

    private static int random(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
}
