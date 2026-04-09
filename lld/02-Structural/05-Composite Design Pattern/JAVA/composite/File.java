package composite;

/**
 * <h1>The Leaf Node</h1>
 * 
 * <p>A leaf has no children. It implements the base Component interface 
 * and actually does the heavy lifting for the operations.
 */
public class File implements FileSystemComponent {
    
    private final String name;
    private final long sizeBytes;

    public File(String name, long sizeBytes) {
        this.name = name;
        this.sizeBytes = sizeBytes;
    }

    @Override
    public void showDetails(String indentation) {
        System.out.println(indentation + "📄 " + name + " (" + sizeBytes + " bytes)");
    }

    @Override
    public long getSize() {
        return sizeBytes;
    }
}
