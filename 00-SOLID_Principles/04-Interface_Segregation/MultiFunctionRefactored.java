/**
 * ============================================================================
 * ✅ REFACTORED: Role-Based Segregation (ISP)
 * ============================================================================
 * 
 * SOLUTION:
 * Break the monolith interface into lean, specific roles.
 */

interface Printer {
    void print(String document);
}

interface Scanner {
    void scan(String document);
}

interface FaxHandler {
    void fax(String document);
}

// ✅ SUCCESS: SimplePrinter only depends on what it actually DOES.
class SimplePrinter implements Printer {
    @Override
    public void print(String document) {
        System.out.println("Printing: " + document);
    }
}

// ✅ SUCCESS: HighEndPhotocopier can implement multiple roles.
class HighEndPhotocopier implements Printer, Scanner {
    @Override
    public void print(String document) {
        System.out.println("HQ Printing: " + document);
    }

    @Override
    public void scan(String document) {
        System.out.println("High DPI Scanning...");
    }
}

public class MultiFunctionRefactored {
    public static void main(String[] args) {
        Printer p = new SimplePrinter();
        p.print("Simple Doc");

        HighEndPhotocopier copyMachine = new HighEndPhotocopier();
        copyMachine.print("Glossy Photo");
        copyMachine.scan("Old Manuscript");
    }
}
