package engine.flyweight;

/**
 * <h1>The Flyweight (Intrinsic State)</h1>
 * 
 * <p>Contains strictly the heavy, unchangeable 3D mesh data. 
 * Doesn't know where it is located on screen.
 */
public class ParticleMesh {
    private final String meshName;
    private final String heavy3DGeometry; // Pretend this is 5MB of vertex data

    public ParticleMesh(String meshName, String heavy3DGeometry) {
        this.meshName = meshName;
        this.heavy3DGeometry = heavy3DGeometry;
        System.out.println("   [GPU Allocation] Loaded 5MB Mesh to VRAM: " + meshName);
    }

    public void render(int x, int y, int velocity) {
        System.out.println("      -> Rendering Mesh [" + meshName + "] at coords (" + x + "," + y + ") moving at " + velocity + "m/s");
    }
}
