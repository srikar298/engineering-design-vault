import composite.FileSystemComponent;
import composite.File;
import composite.Directory;

/**
 * <h1>Composite Pattern Demonstration</h1>
 * 
 * <p>Notice how the client treats `root` (a Directory holding multiple items) 
 * and `report.pdf` (a single File) EXACTLY the same way. The client just calls 
 * `.getSize()` on the component, and the Composite pattern handles the recursion.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Composite Pattern: Tree Transparency           ");
        System.out.println("==================================================\n");

        // 1. Create simple leaf nodes (Files)
        FileSystemComponent file1 = new File("budget_2024.xlsx", 15000);
        FileSystemComponent file2 = new File("report.pdf", 45000);
        FileSystemComponent file3 = new File("vacation.jpg", 320000);
        
        // 2. Create composite nodes (Directories)
        Directory financeDir = new Directory("Finance Documents");
        Directory personalDir = new Directory("Personal Stuff");
        Directory root = new Directory("Root Drive (C:)");

        // 3. Build the tree structure dynamically
        financeDir.addComponent(file1);
        financeDir.addComponent(file2);
        
        personalDir.addComponent(file3);
        
        root.addComponent(financeDir);
        root.addComponent(personalDir);
        
        // Add a file directly to root
        root.addComponent(new File("system_config.xml", 2048));

        // 4. Client interacts with the tree uniformly
        System.out.println("--- Entire File System ---");
        root.showDetails("");
        System.out.println("\nTotal Size of Root: " + root.getSize() + " bytes\n");

        System.out.println("--- Single Branch (Finance) ---");
        financeDir.showDetails("");
        System.out.println("\nTotal Size of Finance: " + financeDir.getSize() + " bytes\n");

        System.out.println("--- Single Leaf (file1) ---");
        // A single file supports the exact same methods! Client transparency.
        file1.showDetails("");
        System.out.println("Size: " + file1.getSize() + " bytes");
    }
}
