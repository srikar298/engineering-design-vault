package element;

import visitor.IVisitor;

public class Factory implements IPropertyElement {
    private final int numberOfMachines;

    public Factory(int numberOfMachines) {
        this.numberOfMachines = numberOfMachines;
    }

    public int getNumberOfMachines() { return numberOfMachines; }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
