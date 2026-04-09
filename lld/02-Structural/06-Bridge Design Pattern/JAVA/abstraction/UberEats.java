package abstraction;

import implementation.NavigationImpl;

/**
 * <h1>Refined Abstraction</h1>
 */
public class UberEats extends NavigationSystem {
    
    private final String restaurantName;

    public UberEats(NavigationImpl navigationImpl, String restaurantName) {
        super(navigationImpl);
        this.restaurantName = restaurantName;
    }

    @Override
    public void navigate(String destination) {
        System.out.print("[Uber Eats] Delivery picked up from '" + restaurantName + "' -> ");
        // Delegate low-level work to the connected bridge
        navigationImpl.navigateTo(destination);
    }
}
