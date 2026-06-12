/**
 * ============================================================================
 * ☣️ VIOLATION: The "Dependency Trap" (SRP)
 * ============================================================================
 * 
 * SCENARIO:
 * An Invoice Service that calculates taxes and generates a PDF report.
 * 
 * STAKEHOLDERS:
 * 1. Finance/Tax Team -> (Taxes, jurisdictions, rounding)
 * 2. Product/UX Team -> (PDF Layout, branding, fonts)
 * 
 * WHY IS THIS A VIOLATION?
 * Even though PDF generation "depends" on tax calculation, they change for 
 * completely different reasons. 
 * - If the government changes tax law, you shouldn't risk breaking the PDF layout.
 * - If the marketing team changes the logo, you shouldn't risk breaking tax logic.
 */
public class InvoiceViolation {

    public void processInvoice(double amount) {
        // --- Responsibility 1: Tax Calculation (Finance Stakeholder) ---
        double tax = amount * 0.15; // Imagine complex jurisdictional logic here
        double total = amount + tax;

        // --- Responsibility 2: PDF Generation (Product/UX Stakeholder) ---
        // VIOLATION: Rendering logic is tightly coupled with calculation logic.
        System.out.println("--- GENERATING PDF ---");
        System.out.println("Invoice Total: " + total);
        System.out.println("Tax Applied: " + tax);
        System.out.println("Footer: (c) 2024 MyCompany");
        // Imagine 200 lines of PDF template code here...
    }

    public static void main(String[] args) {
        new InvoiceViolation().processInvoice(100.0);
    }
}
