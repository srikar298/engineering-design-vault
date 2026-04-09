package element;

import visitor.IVisitor;

/**
 * <h1>The Visitable Element</h1>
 * 
 * <p>All items in the object structure must implement this interface. 
 * It forces them to accept a Visitor.
 */
public interface IPropertyElement {
    
    /**
     * The core of Double Dispatch.
     */
    void accept(IVisitor visitor);
}
