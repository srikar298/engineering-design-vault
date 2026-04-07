package cloud;

/**
 * <h1>The Protection Proxy</h1>
 * 
 * <p>Implements the Component interface but acts as an auth gatekeeper 
 * for the real Folder. 
 */
public class FolderAuthProxy implements IFileSystemNode {
    
    private final Folder realFolder;
    private final User requestingUser;
    
    private final String folderName;

    public FolderAuthProxy(String folderName, User requestingUser) {
        this.folderName = folderName;
        // The Proxy secretly builds the real object
        this.realFolder = new Folder(folderName);
        this.requestingUser = requestingUser;
    }

    // Proxy specifically manages adding components (bypassing interface for demo)
    public void addComponent(IFileSystemNode node) {
        if (!requestingUser.isAdmin()) {
            throw new SecurityException("🚨 403 Forbidden: '" + requestingUser.getName() + "' cannot upload to '" + folderName + "'");
        }
        realFolder.addComponent(node);
    }

    @Override
    public void display(String indent) {
        // Anyone can execute the Read method (display)
        realFolder.display(indent);
    }

    @Override
    public long getSize() {
        // Anyone can calculate the size
        return realFolder.getSize();
    }
}
