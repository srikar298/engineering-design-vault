import engine.facade.GraphicRenderFacade;

/**
 * <h1>RPG Game Engine Demo</h1>
 * 
 * <p>Demonstrates the Facade pattern hiding the complexity of a 
 * Flyweight-optimized rendering engine.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   RPG Engine: Facade + Flyweight Demo            ");
        System.out.println("==================================================\n");

        // The game loop only knows about the wrapper Faceplate!
        GraphicRenderFacade gpuFacade = new GraphicRenderFacade();

        // Gameplay Event: A massive bomb goes off
        gpuFacade.spawnExplosion(100, 200, 5000); // Spawn 5,000 particles

        // Game Loop (Frame 1)
        gpuFacade.renderFrame();

        // Game Loop (Frame 2)
        gpuFacade.renderFrame();

        // Let's check how effective our optimization was
        gpuFacade.printHardwareStats();
        System.out.println("\nSUCCESS: Game did not crash.");
    }
}
