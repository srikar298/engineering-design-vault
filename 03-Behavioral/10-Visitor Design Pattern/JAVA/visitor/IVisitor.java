package visitor;

import element.Commercial;
import element.Factory;
import element.Residential;

/**
 * <h1>The Visitor Interface</h1>
 * 
 * <p>Declares a set of visiting methods that correspond to element classes.
 * Notice the method overloading for specific Concrete Elements.
 */
public interface IVisitor {
    void visit(Residential residential);
    void visit(Commercial commercial);
    void visit(Factory factory);
}
