/**
 * ============================================================================
 * ✅ REFACTORED: Avoiding the Dependency Trap
 * ============================================================================
 * 
 * SOLUTION:
 * Separate the "What to calculate" from "How to display it."
 * 
 * Components:
 * 1. TaxCalculator: Owned by Finance. Pure numbers.
 * 2. InvoiceRenderer: Owned by Product. Pure layout.
 * 
 * THE SENIOR INSIGHT:
 * "Execution dependency does not mean Responsibility coupling." 
 * We pass the result of one as the input to the other, keeping the logic isolated.
 */

// --- Component 1: Finance Stakeholder ---
class TaxCalculator {
    public double calculateTax(double amount) {
        return amount * 0.15; // Independent change axis
    }
}

// --- Component 2: Product/UX Stakeholder ---
class InvoiceRenderer {
    public void renderPdf(double amount, double tax) {
        System.out.println("--- GENERATING PDF ---");
        System.out.println("Invoice Total: " + (amount + tax));
        System.out.println("Tax Applied: " + tax);
        System.out.println("Footer: (c) 2024 MyCompany");
        // Changes to layout only affect this class.
    }
}

// --- Orchestrator ---
public class InvoiceRefactored {
    private final TaxCalculator calculator = new TaxCalculator();
    private final InvoiceRenderer renderer = new InvoiceRenderer();

    public void execute(double amount) {
        double tax = calculator.calculateTax(amount);
        renderer.renderPdf(amount, tax);
    }

    public static void main(String[] args) {
        new InvoiceRefactored().execute(100.0);
    }
}
