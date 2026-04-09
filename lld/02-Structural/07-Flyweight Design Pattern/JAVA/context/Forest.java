package context;

import flyweight.TreeFactory;
import flyweight.TreeType;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>The Client / Playground</h1>
 * 
 * <p>Manages thousands or millions of Trees.
 */
public class Forest {
    
    // The collection of all Context objects
    private final List<Tree> trees = new ArrayList<>();

    public void plantTree(int x, int y, String name, String color, String texture) {
        // 1. Get or create the shared Flyweight
        TreeType type = TreeFactory.getTreeType(name, color, texture);
        
        // 2. Create the lightweight Context object
        Tree tree = new Tree(x, y, type);
        
        // 3. Store it
        trees.add(tree);
    }

    public void drawForest() {
        System.out.println("\n[Forest] Rendering " + trees.size() + " trees...");
        for (Tree tree : trees) {
            tree.draw();
        }
    }
}
