package filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>LLD Problem: Secure File System (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> Build a file system where you can have files and folders. 
 * Additionally, you must ensure that only users with 'WRITE' access can 
 * delete or modify files.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Patterns:</b> <b>Composite</b> for the tree structure, and <b>Proxy</b> 
 *    for the permission-based access control.
 * 2. <b>Separation of Concerns:</b> The File/Folder classes handle the 
 *    structure; the Proxy handles the security.
 * 3. <b>Recursion:</b> Composite allows for deep nested folder structures.
 */

// --- COMPONENT ---
interface Node {
    void display(String indent);
    void delete(String userRole);
}

// --- LEAF ---
class FileNode implements Node {
    private final String name;
    public FileNode(String n) { this.name = n; }
    @Override public void display(String indent) { System.out.println(indent + "📄 " + name); }
    @Override public void delete(String userRole) { System.out.println("Deleting file: " + name); }
}

// --- COMPOSITE ---
class FolderNode implements Node {
    private final String name;
    private final List<Node> children = new ArrayList<>();
    public FolderNode(String n) { this.name = n; }
    public void add(Node n) { children.add(n); }

    @Override public void display(String indent) {
        System.out.println(indent + "📁 " + name);
        for (Node child : children) child.display(indent + "  ");
    }

    @Override public void delete(String userRole) {
        System.out.println("Deleting folder: " + name);
        // Recursive delete
        for (Node child : children) child.delete(userRole);
    }
}

// --- [PRODUCTION_ENHANCEMENT] (The Security Proxy) ---
class SecureFileSystemProxy implements Node {
    private final Node realNode;
    private final String currentUserRole;

    public SecureFileSystemProxy(Node node, String role) {
        this.realNode = node;
        this.currentUserRole = role;
    }

    @Override public void display(String indent) { realNode.display(indent); }

    @Override public void delete(String userRole) {
        // --- [INTERVIEW_MVP] (Security Check) ---
        if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
            throw new SecurityException("Access Denied: You do not have permission to delete.");
        }
        realNode.delete(userRole);
    }
}

public class SecureFileSystemLLD {
    public static void main(String[] args) {
        FolderNode root = new FolderNode("root");
        root.add(new FileNode("config.txt"));
        
        FolderNode logs = new FolderNode("logs");
        logs.add(new FileNode("error.log"));
        root.add(logs);

        // Usage with Proxy
        Node secureRoot = new SecureFileSystemProxy(root, "GUEST");
        secureRoot.display("");
        
        try {
            secureRoot.delete("GUEST"); // Should fail
        } catch (Exception e) {
            System.err.println("✅ Security working: " + e.getMessage());
        }
    }
}
