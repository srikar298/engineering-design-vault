package abstraction;

import implementation.NavigationImpl;

/**
 * <h1>Refined Abstraction</h1>
 */
public class UberRide extends NavigationSystem {
    
    private final String driverName;

    public UberRide(NavigationImpl navigationImpl, String driverName) {
        super(navigationImpl); // Initialize the bridge
        this.driverName = driverName;
    }

    @Override
    public void navigate(String destination) {
        System.out.print("[Uber Ride] Dispatching Driver '" + driverName + "' -> ");
        // Delegate low-level work to the connected bridge implementation
        navigationImpl.navigateTo(destination); 
    }
}
