package implementation;

/**
 * <h1>The Implementation API</h1>
 * 
 * <p>This interface doesn't have to match the abstraction's interface. In fact, the two 
 * interfaces can be entirely different. Typically the Implementation interface provides 
 * only primitive operations, while the Abstraction defines higher-level operations 
 * based on those primitives.
 */
public interface NavigationImpl {
    void navigateTo(String destination);
}
