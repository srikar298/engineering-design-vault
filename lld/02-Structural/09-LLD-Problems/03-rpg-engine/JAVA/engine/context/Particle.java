package engine.context;

import engine.flyweight.ParticleMesh;

/**
 * <h1>The Context (Extrinsic State)</h1>
 * 
 * <p>Tiny, lightweight object created millions of times. 
 * Only holds volatile coordinates and a reference pointer to the heavy mesh.
 */
public class Particle {
    private int x;
    private int y;
    private int velocity;
    
    private final ParticleMesh mesh;

    public Particle(int x, int y, int velocity, ParticleMesh mesh) {
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.mesh = mesh;
    }

    public void updatePhysics() {
        this.y += velocity; // Simple gravity/movement
    }

    public void draw() {
        // Pass the highly volatile extrinsic state into the shared intrinsic flyweight
        mesh.render(x, y, velocity);
    }
}
