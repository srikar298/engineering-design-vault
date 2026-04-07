package composite;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>The Composite Node</h1>
 * 
 * <p>A composite (Branch) contains children. The children can be Leaves (Files), 
 * or they can be other Composites (Directories). 
 * 
 * <p>When a method is called on a Composite, it delegates the work down to its 
 * children and aggregates the result.
 */
public class Directory implements FileSystemComponent {
    
    private final String name;
    
    // Key Concept: It holds a list of the INTERFACE, not concrete classes.
    // This allows it to hold both Files and other Directories.
    private final List<FileSystemComponent> children = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    // --- Composite-Specific Management Methods --- //
    
    public void addComponent(FileSystemComponent component) {
        children.add(component);
    }
    
    public void removeComponent(FileSystemComponent component) {
        children.remove(component);
    }

    // --- Shared Component Methods (Delegation) --- //

    @Override
    public void showDetails(String indentation) {
        System.out.println(indentation + "📁 " + name);
        String childIndentation = indentation + "   ";
        
        // Recursive delegation
        for (FileSystemComponent child : children) {
            child.showDetails(childIndentation);
        }
    }

    @Override
    public long getSize() {
        long totalSize = 0;
        
        // Recursive delegation
        for (FileSystemComponent child : children) {
            totalSize += child.getSize();
        }
        return totalSize;
    }
}
