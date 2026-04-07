package context;

import flyweight.TreeType;

/**
 * <h1>The Context (Extrinsic State)</h1>
 * 
 * <p>This object contains the state that is UNIQUE to each occurrence.
 * The `x` and `y` coordinates are different for every single tree.
 * 
 * <p>It also holds a reference to the Flyweight ({@link TreeType}) which contains 
 * the massive shared data.
 */
public class Tree {
    
    // Extrinsic State (Unique per instance, highly volatile)
    private final int x;
    private final int y;
    
    // Reference to Intrinsic State (Shared, immutable)
    private final TreeType type;

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        // Pass the extrinsic state to the flyweight!
        type.draw(x, y);
    }
}
