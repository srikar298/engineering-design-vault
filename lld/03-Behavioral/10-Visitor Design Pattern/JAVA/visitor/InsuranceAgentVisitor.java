package visitor;

import element.Commercial;
import element.Factory;
import element.Residential;

/**
 * <h1>The Concrete Visitor</h1>
 * 
 * <p>Contains the specific algorithm (Insurance Calculation) extracted from the elements.
 */
public class InsuranceAgentVisitor implements IVisitor {

    @Override
    public void visit(Residential residential) {
        System.out.println("   [Insurance Agent] Visiting Residential property.");
        // Logic: $1000 base + $500 per family
        int premium = 1000 + (residential.getNumberOfFamilies() * 500);
        System.out.println("      -> Medical risk is high. Premium calculated: $" + premium);
    }

    @Override
    public void visit(Commercial commercial) {
        System.out.println("   [Insurance Agent] Visiting Commercial property.");
        // Logic: $5 per square foot
        int premium = commercial.getSquareFootage() * 5;
        System.out.println("      -> Theft risk is high. Premium calculated: $" + premium);
    }

    @Override
    public void visit(Factory factory) {
        System.out.println("   [Insurance Agent] Visiting Factory property.");
        // Logic: $2000 per machine
        int premium = factory.getNumberOfMachines() * 2000;
        System.out.println("      -> Fire/Hazard risk is high. Premium calculated: $" + premium);
    }
}
