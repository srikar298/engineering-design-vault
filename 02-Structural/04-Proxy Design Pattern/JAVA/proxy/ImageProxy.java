package proxy;

/**
 * <h1>The Virtual Proxy</h1>
 * 
 * <p>A surrogate or placeholder for the {@link RealImage}. It implements the same 
 * interface, so the client doesn't know the difference.
 * 
 * <p><b>Core Value:</b> Lazy Initialization. It intercepts calls to the real object 
 * and only instantiates the heavy {@code RealImage} at the exact moment it is actually needed.
 */
public class ImageProxy implements Image {
    
    // The wrapped actual object. Initially null.
    private RealImage realImage;
    private final String filename;

    public ImageProxy(String filename) {
        this.filename = filename;
        System.out.println("[ImageProxy] Surrogate created for: " + filename + ". Heavy object is NOT loaded yet.");
    }

    @Override
    public void display() {
        // LAZY INITIALIZATION: We only pay the cost of loading if/when
        // the client actually asks to display the image.
        if (realImage == null) {
            System.out.println("[ImageProxy] Intercepted display request. Loading real object now...");
            realImage = new RealImage(filename);
        }
        
        // Pass the request through to the real object
        realImage.display();
    }
}
