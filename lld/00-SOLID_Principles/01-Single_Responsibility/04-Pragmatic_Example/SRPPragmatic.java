

/**
 * <h1>SRP: The "Stakeholder" Principle (SDE-2+ Level)</h1>
 * 
 * Robert C. Martin's actual definition: 
 * "A module should be responsible to one, and only one, actor."
 * 
 * In this refactored version:
 * 1. InvoiceCalculator -> Responsible to the Finance/Tax Actor.
 * 2. InvoicePDFGenerator -> Responsible to the Product/UX Actor.
 */

class Invoice {
    private final double amount;
    private double tax;
    private double total;

    public Invoice(double amount) { this.amount = amount; }
    
    // Getters/Setters
    public double getAmount() { return amount; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}

/**
 * Responsibility 1: Pure Calculation (Finance Domain)
 */
class InvoiceCalculator {
    public void calculateTax(Invoice invoice) {
        // Complex tax logic based on jurisdiction
        double tax = invoice.getAmount() * 0.15;
        invoice.setTax(tax);
        invoice.setTotal(invoice.getAmount() + tax);
    }
}

/**
 * Responsibility 2: Pure Presentation (UX Domain)
 */
class InvoicePDFGenerator {
    public void generate(Invoice invoice) {
        System.out.println("--- GENERATING PDF ---");
        System.out.println("Total: " + invoice.getTotal());
        System.out.println("Footer: (c) 2024 MyCompany");
    }
}

/**
 * Responsibility 3: Pure Persistence (Infrastructure Domain)
 */
class InvoiceRepository {
    public void save(Invoice invoice) {
        System.out.println("Saving invoice to Database...");
    }
}

/**
 * 🎓 SDE-2+ INSIGHT:
 * SRP doesn't mean "small classes." It means "isolated change."
 * If the PDF library changes, 'InvoiceCalculator' is not recompiled or redeployed.
 */
public class SRPPragmatic {
    public static void main(String[] args) {
        Invoice invoice = new Invoice(100.0);
        
        new InvoiceCalculator().calculateTax(invoice);
        new InvoicePDFGenerator().generate(invoice);
        new InvoiceRepository().save(invoice);
        
        System.out.println("✅ SRP Refactored: Isolated change achieved.");
    }
}
