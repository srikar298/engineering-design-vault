import proxy.Image;
import proxy.ImageProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Proxy Pattern Demonstration</h1>
 * 
 * <p>Demonstrates a <b>Virtual Proxy</b> (Lazy Initialization).
 * We create a "gallery" of 10 images. If we used the RealImage directly, 
 * the application would freeze for 10 seconds on startup just to load images 
 * the user might never scroll to see.
 * 
 * <p>By using the Proxy, the application starts instantly. The heavy lifting 
 * only occurs when an image is actually displayed.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Proxy Pattern: Virtual Surrogate (Lazy Load)   ");
        System.out.println("==================================================\n");

        System.out.println("--- 1. Application Startup (Creating Gallery) ---");
        List<Image> gallery = new ArrayList<>();
        
        // Creating 3 proxies is instant. No disk IO happens here.
        gallery.add(new ImageProxy("photo_hawaii_4k.png"));
        gallery.add(new ImageProxy("photo_paris_4k.png"));
        gallery.add(new ImageProxy("photo_tokyo_4k.png"));
        
        System.out.println("\nApplication UI loaded successfully. (Zero delay!)\n");

        System.out.println("--- 2. User scrolls to the first image ---");
        // The first time display() is called, the proxy loads the real object.
        gallery.get(0).display();
        
        System.out.println("\n--- 3. User views the first image again ---");
        // The second time, the proxy just delegates. No reloading!
        gallery.get(0).display();

        System.out.println("\n--- 4. User scrolls to the third image ---");
        gallery.get(2).display();
        
        System.out.println("\nNotice: The second image (Paris) was NEVER loaded into memory because the user never viewed it. Saved memory and CPU!");
    }
}
