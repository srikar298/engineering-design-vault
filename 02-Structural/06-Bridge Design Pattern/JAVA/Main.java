import abstraction.NavigationSystem;
import abstraction.UberRide;
import abstraction.UberEats;

import implementation.NavigationImpl;
import implementation.GoogleMaps;
import implementation.AppleMaps;

/**
 * <h1>Bridge Pattern Demonstration</h1>
 * 
 * <p>Notice how we never had to create classes like `UberRideWithGoogleMaps` 
 * or `UberEatsWithAppleMaps`. The Abstraction (Uber Services) and the 
 * Implementation (Map Engines) can vary completely independently!
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Bridge Pattern: Decoupling Hierarchies         ");
        System.out.println("==================================================\n");

        // 1. Instantiate the lowest level primitive implementations
        NavigationImpl googleMaps = new GoogleMaps();
        NavigationImpl appleMaps = new AppleMaps();

        // 2. Inject the implementations into the high-level abstractions
        NavigationSystem myRide = new UberRide(googleMaps, "Keerti");
        NavigationSystem myDinner = new UberEats(appleMaps, "Pizza Palace");

        // 3. Execute
        System.out.println("--- Scenario 1 ---");
        myRide.navigate("Central Park");
        myDinner.navigate("123 HSR Layout");

        System.out.println("\n--- Scenario 2: Dynamic Swapping at Runtime ---");
        System.out.println("Switching mapping engines dynamically...\n");
        
        // We can swap the bridge at runtime! No need to instantiate new Uber objects.
        myRide.setNavigationImpl(appleMaps);
        myRide.navigate("JFK Airport");
    }
}
