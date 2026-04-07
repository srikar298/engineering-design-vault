package element;

import visitor.IVisitor;

public class Commercial implements IPropertyElement {
    private final int squareFootage;

    public Commercial(int squareFootage) {
        this.squareFootage = squareFootage;
    }

    public int getSquareFootage() { return squareFootage; }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
