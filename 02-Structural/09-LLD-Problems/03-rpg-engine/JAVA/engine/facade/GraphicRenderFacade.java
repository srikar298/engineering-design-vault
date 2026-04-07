package engine.facade;

import engine.context.Particle;
import engine.flyweight.MeshFactory;
import engine.flyweight.ParticleMesh;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>The Facade</h1>
 * 
 * <p>Hides the complexity of Flyweights, Factories, and Physics lists 
 * from the main Game Loop.
 */
public class GraphicRenderFacade {
    
    private final List<Particle> activeParticles;

    public GraphicRenderFacade() {
        this.activeParticles = new ArrayList<>();
    }

    /**
     * Clean API for the game engine to spawn effects.
     */
    public void spawnExplosion(int startX, int startY, int intensity) {
        System.out.println("\n[Facade] Spawning Explosion Event with " + intensity + " particles...");
        
        // Let the Factory handle caching the mesh!
        ParticleMesh fireMesh = MeshFactory.getMesh("FIRE_PARTICLE");
        ParticleMesh smokeMesh = MeshFactory.getMesh("SMOKE_PARTICLE");

        for (int i = 0; i < intensity; i++) {
            // Half fire, half smoke
            ParticleMesh selectedMesh = (i % 2 == 0) ? fireMesh : smokeMesh;
            
            // Random volatile velocities
            int velocity = (int)(Math.random() * 10) + 1;
            int xOffset = startX + (i * 2);
            
            activeParticles.add(new Particle(xOffset, startY, velocity, selectedMesh));
        }
    }

    /**
     * Clean API for the main game loop to call once per frame (60 FPS).
     */
    public void renderFrame() {
        System.out.println("\n[Facade] --- Rendering Frame (" + activeParticles.size() + " active objects) ---");
        
        // 1. Update Physics
        for (Particle p : activeParticles) {
            p.updatePhysics();
        }
        
        // 2. Clear Screen buffer (simulated)
        // 3. Draw
        // For the demo we'll just draw the first 3 to avoid console spam
        for (int i = 0; i < Math.min(3, activeParticles.size()); i++) {
            activeParticles.get(i).draw();
        }
        if (activeParticles.size() > 3) {
            System.out.println("      ... (and " + (activeParticles.size() - 3) + " more particles drawn)");
        }
    }
    
    public void printHardwareStats() {
        System.out.println("\n[GPU Stats] Active objects: " + activeParticles.size());
        System.out.println("[GPU Stats] VRAM Used: " + MeshFactory.getVRAMFootprint() + " MB");
        System.out.println("[GPU Stats] Theoretical RAM w/o Flyweight: " + (activeParticles.size() * 5) + " MB");
    }
}
