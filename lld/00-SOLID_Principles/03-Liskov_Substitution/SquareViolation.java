/**
 * ============================================================================
 * ☣️ VIOLATION: The Square-Rectangle Trap (LSP)
 * ============================================================================
 * 
 * SCENARIO: 
 * A 'Rectangle' class is extended by a 'Square'.
 */

class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Forced equality
    }

    @Override
    public void setHeight(int height) {
        this.width = height; // Forced equality
        this.height = height;
    }
}

public class SquareViolation {
    static void testRectangle(Rectangle r) {
        r.setWidth(10);
        r.setHeight(5);
        
        // ❌ VIOLATION: The caller expects area to be 50.
        // If 'r' is a Square, the area will be 25!
        if (r.getArea() != 50) {
            System.out.println("LSP VIOLATION: Expected 50, but got " + r.getArea());
        }
    }

    public static void main(String[] args) {
        testRectangle(new Rectangle());
        testRectangle(new Square());
    }
}
