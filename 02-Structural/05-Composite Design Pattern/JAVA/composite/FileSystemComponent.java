package composite;

/**
 * <h1>The Component Interface</h1>
 * 
 * <p>This is the abstraction for ALL elements in the tree (both leaves and branches).
 * By defining common operations here, the client can treat single objects (Files)
 * and collections of objects (Directories) uniformly.
 */
public interface FileSystemComponent {
    
    /** Common operation: print the name of the component */
    void showDetails(String indentation);
    
    /** Common operation: calculate the size */
    long getSize();
}
