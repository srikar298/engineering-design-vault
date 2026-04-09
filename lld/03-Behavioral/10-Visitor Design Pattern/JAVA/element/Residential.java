package element;

import visitor.IVisitor;

public class Residential implements IPropertyElement {
    private final int numberOfFamilies;

    public Residential(int numberOfFamilies) {
        this.numberOfFamilies = numberOfFamilies;
    }

    public int getNumberOfFamilies() { return numberOfFamilies; }

    @Override
    public void accept(IVisitor visitor) {
        // Double Dispatch! I am passing "this" (a strictly typed Residential object) 
        // to the visitor, so the compiler routes to the correct overloaded method.
        visitor.visit(this); 
    }
}
