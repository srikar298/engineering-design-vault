/**
 * ============================================================================
 * ☣️ VIOLATION: The "Fat Interface" Pollution (ISP)
 * ============================================================================
 * 
 * SCENARIO: 
 * A single 'SmartDevice' interface for all office equipment.
 * SimplePrinterViolation is forced to implement fax() and scan()
 * even though it has no phone line and no scanner.
 */

interface SmartDevice {
    void print(String document);
    void scan(String document);
    void fax(String document);
}

// ❌ VIOLATION: SimplePrinterViolation is forced to implement fax/scan
class SimplePrinterViolation implements SmartDevice {
    @Override
    public void print(String document) {
        System.out.println("Printing: " + document);
    }

    @Override
    public void scan(String document) {
        // I don't have a scanner!
        throw new UnsupportedOperationException("Scan not supported.");
    }

    @Override
    public void fax(String document) {
        // I don't have a fax line!
        throw new UnsupportedOperationException("Fax not supported.");
    }
}

public class SmartPrinterViolation {
    public static void main(String[] args) {
        SmartDevice printer = new SimplePrinterViolation();
        printer.print("My Budget");
        
        // This will crash unexpectedly for a caller who thinks they have a 'SmartDevice'
        printer.scan("My Secret");
    }
}
