package cloud;

/**
 * <h1>The Component Interface</h1>
 * 
 * <p>Both Leaves (File) and Branches (Folder + Proxy) implement this.
 */
public interface IFileSystemNode {
    void display(String indent);
    long getSize();
}
