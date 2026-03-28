package evolution;

/**
 * Stage 1: JavaBeans (Setters)
 * 
 * The Problem: While readable, the object is MULTABLE and can be in an
 * INCONSISTENT state during construction. An object might be used
 * before all its required fields are set.
 */
class ComputerJB {
    private String CPU;
    private String RAM;
    private String Storage;
    private String GPU;

    public ComputerJB() {}

    public void setCPU(String CPU) { this.CPU = CPU; }
    public void setRAM(String RAM) { this.RAM = RAM; }
    public void setStorage(String Storage) { this.Storage = Storage; }
    public void setGPU(String GPU) { this.GPU = GPU; }

    @Override
    public String toString() {
        return "Computer [CPU=" + CPU + ", RAM=" + RAM + ", Storage=" + Storage + ", GPU=" + GPU + "]";
    }
}

public class Stage1JavaBeans {
    public static void main(String[] args) {
        ComputerJB myPc = new ComputerJB();
        myPc.setCPU("Intel i7");
        myPc.setRAM("32GB");
        // What if we forget to set Storage and GPU?
        // The object is currently in an incomplete state.
        
        System.out.println("Stage 1 (JavaBeans): " + myPc);
    }
}
