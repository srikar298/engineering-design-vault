package smarthome.platforms;

/**
 * <h1>Bridge Implementation Interface</h1>
 * 
 * <p>Defines the low-level primitive operations that all smart platforms 
 * (Google, Samsung, Hue) must support.
 */
public interface IPlatform {
    void executeAction(String action, String value);
}
