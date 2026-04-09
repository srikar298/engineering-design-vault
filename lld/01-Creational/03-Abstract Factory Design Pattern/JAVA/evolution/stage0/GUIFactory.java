package evolution.stage0;

/**
 * Stage 0: The Inconsistency Mess
 * 
 * Here, we have a "God Factory" that tries to create everything using strings/enums.
 * The Problem: A developer can accidentally ask for a Windows Button 
 * but a Mac Checkbox in the SAME application. The compiler won't stop them.
 */
public class GUIFactory {
    public static Object create(String type, String os) {
        if (type.equalsIgnoreCase("button")) {
            if (os.equalsIgnoreCase("windows")) return "Windows Button";
            if (os.equalsIgnoreCase("mac")) return "Mac Button";
        } else if (type.equalsIgnoreCase("checkbox")) {
            if (os.equalsIgnoreCase("windows")) return "Windows Checkbox";
            if (os.equalsIgnoreCase("mac")) return "Mac Checkbox";
        }
        return null;
    }
}

class Main {
    public static void main(String[] args) {
        // DANGER: Inconsistency!
        Object btn = GUIFactory.create("button", "windows");
        Object chk = GUIFactory.create("checkbox", "mac"); // Oops! Mixed OS products.
        
        System.out.println("Created: " + btn + " and " + chk);
        System.out.println("Result: UI looks broken or crashes because of OS mismatch.");
    }
}
