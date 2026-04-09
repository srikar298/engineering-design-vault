package proxy;

/**
 * <h1>The Real Subject</h1>
 * 
 * <p>This object contains the real business logic. Crucially, it is 
 * EXPENSIVE to create (e.g., loading a massive 50MB image from a slow hard drive 
 * or a remote network server).
 */
public class RealImage implements Image {
    private final String filename;

    public RealImage(String filename) {
        this.filename = filename;
        // EXPENSIVE OPERATION happens in constructor!
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("[RealImage] ⏳ Loading heavy image from disk: " + filename + " ... (takes 5 seconds)");
        try {
            // Simulated delay
            Thread.sleep(1000); 
        } catch (InterruptedException ignored) {}
        System.out.println("[RealImage] ✅ Finished loading: " + filename);
    }

    @Override
    public void display() {
        System.out.println("[RealImage] 🖼️  Displaying image: " + filename);
    }
}
