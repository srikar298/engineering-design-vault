package cloud;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>The Real Subject (Composite)</h1>
 * 
 * <p>The pure data structure representing a folder. It knows absolutely 
 * nothing about Users, Authentication, or Security. This maintains the 
 * Single Responsibility Principle.
 */
public class Folder implements IFileSystemNode {
    
    private final String name;
    private final List<IFileSystemNode> children;

    public Folder(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addComponent(IFileSystemNode node) {
        children.add(node);
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📁 [Folder] " + name);
        String childIndent = indent + "   ";
        for (IFileSystemNode child : children) {
            child.display(childIndent); // Recursive delegation
        }
    }

    @Override
    public long getSize() {
        long total = 0;
        for (IFileSystemNode child : children) {
            total += child.getSize(); // Recursive delegation
        }
        return total;
    }
}
