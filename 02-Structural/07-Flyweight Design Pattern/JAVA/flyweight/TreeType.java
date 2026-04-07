package flyweight;

/**
 * <h1>The Flyweight (Intrinsic State)</h1>
 * 
 * <p>This class contains the state that is SHARED across many objects.
 * Intrinsic state is immutable. A tree's species, color, and high-res texture 
 * are the same for all Oak trees, so we only need ONE instance of this class 
 * for all Oaks.
 * 
 * <p>If texture is a 1MB byte array, creating 1,000,000 Oak trees would normally 
 * take 1 Terabyte of RAM. By extracting the intrinsic state into a Flyweight, 
 * 1,000,000 trees only take 1MB (shared) + a few bytes for coordinates.
 */
public class TreeType {
    private final String name;
    private final String color;
    private final String heavyTexture; // Represents a large, expensive object

    public TreeType(String name, String color, String heavyTexture) {
        this.name = name;
        this.color = color;
        this.heavyTexture = heavyTexture;
        System.out.println("[TreeType] Heavy object loaded into memory: " + name + " (" + heavyTexture + ")");
    }

    /**
     * The Flyweight takes the EXTRINSIC state as method arguments
     * to perform its actions, without storing it.
     */
    public void draw(int x, int y) {
        System.out.println("   └─ Rendering [" + name + "] tree at coordinates (" + x + "," + y + ") with color " + color);
    }
}
