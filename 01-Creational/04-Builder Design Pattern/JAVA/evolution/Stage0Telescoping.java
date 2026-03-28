package evolution;

/**
 * Stage 0: Telescoping Constructor (The Anti-pattern)
 * 
 * The Problem: As the number of parameters grows, we end up with multiple
 * constructors. This is hard to read, easy to mix up parameters (especially
 * multiple Strings), and difficult to maintain.
 */
class Computer {
    private String CPU;
    private String RAM;
    private String Storage;
    private String GPU;

    public Computer(String CPU) {
        this(CPU, "8GB");
    }

    public Computer(String CPU, String RAM) {
        this(CPU, RAM, "256GB SSD");
    }

    public Computer(String CPU, String RAM, String Storage) {
        this(CPU, RAM, Storage, "Integrated GPU");
    }

    public Computer(String CPU, String RAM, String Storage, String GPU) {
        this.CPU = CPU;
        this.RAM = RAM;
        this.Storage = Storage;
        this.GPU = GPU;
    }

    @Override
    public String toString() {
        return "Computer [CPU=" + CPU + ", RAM=" + RAM + ", Storage=" + Storage + ", GPU=" + GPU + "]";
    }
}

public class Stage0Telescoping {
    public static void main(String[] args) {
        // Hard to tell which parameter is which if they are all Strings
        Computer myPc = new Computer("Intel i5", "16GB", "512GB", "NVIDIA RTX 3060");
        System.out.println("Stage 0 (Telescoping): " + myPc);
    }
}
