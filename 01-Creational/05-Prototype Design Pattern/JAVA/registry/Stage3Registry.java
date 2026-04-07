package registry;

import java.util.HashMap;
import java.util.Map;

/**
 * Stage 3: Prototype Registry (The Enterprise Manager)
 * 
 * In real systems, you don't keep track of prototypes manually.
 * A Registry acts as a centralized cache of pre-configured objects.
 */

interface Prototype {
    Prototype clone();
    void render();
}

class UIWidget implements Prototype {
    private String color;
    private String position;

    public UIWidget(String color, String position) {
        this.color = color;
        this.position = position;
    }

    // Copy constructor for internal use
    public UIWidget(UIWidget other) {
        this.color = other.color;
        this.position = other.position;
    }

    @Override
    public Prototype clone() {
        return new UIWidget(this);
    }

    @Override
    public void render() {
        System.out.println("Widget [Color: " + color + ", Pos: " + position + "]");
    }
    
    public void setColor(String color) { this.color = color; }
}

class PrototypeRegistry {
    private Map<String, Prototype> cache = new HashMap<>();

    public void addPrototype(String key, Prototype p) {
        cache.put(key, p);
    }

    public Prototype get(String key) {
        Prototype p = cache.get(key);
        return (p != null) ? p.clone() : null;
    }
}

public class Stage3Registry {
    public static void main(String[] args) {
        PrototypeRegistry registry = new PrototypeRegistry();

        // 1. Seed the registry with "Original Designs"
        registry.addPrototype("DarkButton", new UIWidget("Black", "0,0"));
        registry.addPrototype("DangerButton", new UIWidget("Red", "0,0"));

        // 2. Fetch clones whenever needed
        UIWidget b1 = (UIWidget) registry.get("DarkButton");
        UIWidget b2 = (UIWidget) registry.get("DangerButton");
        
        // 3. Customize the clones
        b1.render();
        b2.render();
        
        System.out.println("\nRegistry successfully provided pre-configured clones.");
    }
}
