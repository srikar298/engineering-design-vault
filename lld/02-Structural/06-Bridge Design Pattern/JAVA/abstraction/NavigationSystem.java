package abstraction;

import implementation.NavigationImpl;

/**
 * <h1>The Abstraction</h1>
 * 
 * <p>Defines the abstraction's interface. It maintains a reference to an object 
 * of type NavigationImpl and bridges to it.
 * 
 * <p>This is the bridge! We prefer composition over inheritance. 
 * Instead of creating UberRideGoogleMaps and UberRideAppleMaps, we just 
 * hold a reference to the map engine and inject it dynamically.
 */
public abstract class NavigationSystem {
    
    // The Bridge to the Implementation hierarchy
    protected NavigationImpl navigationImpl;

    public NavigationSystem(NavigationImpl navigationImpl) {
        this.navigationImpl = navigationImpl;
    }

    public void setNavigationImpl(NavigationImpl impl) {
        this.navigationImpl = impl;
    }

    // High-level operation that relies on the primitive implementation
    public abstract void navigate(String destination);
}
