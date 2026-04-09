package evolution;

/**
 * Stage 2: Modern Fluent Builder (Static Inner Class)
 * 
 * This is the gold standard for object creation in modern Java.
 * It ensures IMMUTABILITY and a clean, FLUENT API.
 */
class ComputerFluent {
    // Fields are FINAL for immutability
    private final String CPU;
    private final String RAM;
    private final String Storage;
    private final String GPU;

    private ComputerFluent(Builder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.Storage = builder.Storage;
        this.GPU = builder.GPU;
    }

    @Override
    public String toString() {
        return "Computer [CPU=" + CPU + ", RAM=" + RAM + ", Storage=" + Storage + ", GPU=" + GPU + "]";
    }

    // Static Inner Builder Class
    public static class Builder {
        private String CPU;
        private String RAM;
        private String Storage;
        private String GPU;

        public Builder setCPU(String CPU) {
            this.CPU = CPU;
            return this; // Return this for chaining
        }

        public Builder setRAM(String RAM) {
            this.RAM = RAM;
            return this;
        }

        public Builder setStorage(String Storage) {
            this.Storage = Storage;
            return this;
        }

        public Builder setGPU(String GPU) {
            this.GPU = GPU;
            return this;
        }

        public ComputerFluent build() {
            // Validation can happen here
            if (CPU == null || RAM == null) {
                throw new IllegalStateException("CPU and RAM are mandatory!");
            }
            return new ComputerFluent(this);
        }
    }
}

public class Stage2FluentBuilder {
    public static void main(String[] args) {
        ComputerFluent myPc = new ComputerFluent.Builder()
                .setCPU("AMD Ryzen 9")
                .setRAM("64GB")
                .setStorage("4TB SSD")
                .setGPU("NVIDIA RTX 4090")
                .build();

        System.out.println("Stage 2 (Fluent Builder): " + myPc);
    }
}
