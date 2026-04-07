package proxy;

/**
 * <h1>The Subject Interface</h1>
 * 
 * <p>Both the Real Subject and the Proxy implement this interface. 
 * This allows the client to treat the proxy and the real object identically.
 */
public interface Image {
    void display();
}
