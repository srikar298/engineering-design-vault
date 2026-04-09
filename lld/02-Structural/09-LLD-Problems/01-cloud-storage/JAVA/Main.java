import cloud.File;
import cloud.FolderAuthProxy;
import cloud.User;

/**
 * <h1>Cloud Storage Architecture Demo</h1>
 * 
 * <p>Demonstrates the Composite Pattern (recursive trees) protected by 
 * the Proxy Pattern (auth gates).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Cloud Storage: Composite + Proxy Demo          ");
        System.out.println("==================================================\n");

        User admin = new User("Alice_Sysadmin", true);
        User guest = new User("Bob_Guest", false);

        System.out.println("--- Scenario 1: Admin creating the file system ---");
        
        // Admin creates the root folder proxy
        FolderAuthProxy rootDrive = new FolderAuthProxy("Root (C:)", admin);
        
        // Admin creates a sub-folder proxy
        FolderAuthProxy financeFolder = new FolderAuthProxy("Finance_Data", admin);
        financeFolder.addComponent(new File("budget_2024.xlsx", 5000));
        financeFolder.addComponent(new File("payroll.csv", 15000));
        
        // Admin adds sub-folder to root
        rootDrive.addComponent(financeFolder);
        rootDrive.addComponent(new File("system_config.xml", 1200));

        System.out.println("✅ Tree structure built successfully by Admin.\n");
        
        System.out.println("--- Scenario 2: Traversal Transparency (Composite) ---");
        // We can just call rootDrive.display() and the Composite pattern natively 
        // recurses through the entire tree! No ugly 'instanceof' loops required.
        rootDrive.display("");
        System.out.println("\nTotal Storage Used: " + rootDrive.getSize() + " bytes\n");

        System.out.println("--- Scenario 3: Unauthorized Mutation (Proxy) ---");
        FolderAuthProxy secureFolder = new FolderAuthProxy("Top_Secret_ProjectX", guest);
        
        try {
            System.out.println("Guest attempting to upload file to Top_Secret folder...");
            secureFolder.addComponent(new File("malware.exe", 99999));
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
        }
    }
}
