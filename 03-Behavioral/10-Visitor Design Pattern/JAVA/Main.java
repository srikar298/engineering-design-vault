import element.Commercial;
import element.Factory;
import element.IPropertyElement;
import element.Residential;
import visitor.IVisitor;
import visitor.InsuranceAgentVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Visitor Pattern Demonstration</h1>
 * 
 * <p>Demonstrates Double Dispatch. The list simply calls `accept(visitor)`. 
 * At runtime, the element determines its own type, passes itself back to the visitor, 
 * and the visitor executes the correct algorithm.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Visitor Pattern: Insurance Agent Demo          ");
        System.out.println("==================================================\n");

        List<IPropertyElement> properties = new ArrayList<>();
        properties.add(new Residential(4));
        properties.add(new Commercial(5000));
        properties.add(new Factory(10));

        IVisitor insuranceAgent = new InsuranceAgentVisitor();

        System.out.println("--- Scenario 1: Agent calculates insurance across all properties ---");
        
        for (IPropertyElement property : properties) {
            // MAGIC HAPPENS HERE: Double Dispatch
            property.accept(insuranceAgent);
        }
    }
}
