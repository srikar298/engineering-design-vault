package interface_segregation;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>ISP: The "Client" Principle (SDE-2+ Level)</h1>
 * 
 * Robert C. Martin's actual definition: 
 * "Clients should not be forced to depend on methods that they do not use."
 * 
 * Key Rules for SDE-2+:
 * 1. Prefer MANY small, role-specific interfaces over ONE large interface.
 * 2. An interface should be tailored to the CLIENT, not the IMPLEMENTATION.
 * 3. ISP is the foundation for "Lean" microservices and modular systems.
 */

// ❌ VIOLATION: A massive interface forced on every user
interface UserActions {
    void login();
    void register();
    void uploadProfilePicture();
    void deleteUser(String id); // ❌ Regular users shouldn't see this
    void managePermissions();   // ❌ Regular users shouldn't see this
}

// ✅ REFACTORED: Segregated Role Interfaces
interface Authenticator {
    void login();
    void register();
}

interface ProfileManager {
    void uploadProfilePicture();
}

interface AdminManager {
    void deleteUser(String id);
    void managePermissions();
}

/**
 * Regular User only implements what they can actually do.
 */
class RegularUser implements Authenticator, ProfileManager {
    @Override public void login() { System.out.println("User Login"); }
    @Override public void register() { System.out.println("User Register"); }
    @Override public void uploadProfilePicture() { System.out.println("Upload Picture"); }
}

/**
 * SuperUser implements all roles.
 */
class SuperUser implements Authenticator, ProfileManager, AdminManager {
    @Override public void login() { System.out.println("Admin Login"); }
    @Override public void register() { System.out.println("Admin Register"); }
    @Override public void uploadProfilePicture() { System.out.println("Upload Admin Picture"); }
    @Override public void deleteUser(String id) { System.out.println("Deleting User: " + id); }
    @Override public void managePermissions() { System.out.println("Managing Permissions"); }
}

/**
 * 🎓 SDE-2+ INSIGHT:
 * By segregating interfaces, we protect the CLIENT. 
 * If 'AdminManager' interface changes, the 'RegularUser' code is not recompiled.
 */
public class ISPPragmaticSDE2 {
    public static void main(String[] args) {
        // A client that only cares about Auth
        Authenticator auth = new RegularUser();
        auth.login();

        // An Admin Dashboard client that only cares about Admin Actions
        AdminManager admin = new SuperUser();
        admin.deleteUser("123");
        
        System.out.println("✅ ISP Refactored: Role Interfaces achieved.");
    }
}
