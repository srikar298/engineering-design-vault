package builder.products;

/**
 * The Product class that we want to build.
 */
public class Desktop {
    private String motherboard;
    private String processor;
    private String memory;
    private String storage;
    private String graphicsCard;

    public void display() {
        System.out.println("Desktop Specs:");
        System.out.println("Motherboard: " + motherboard);
        System.out.println("Processor: " + processor);
        System.out.println("Memory: " + memory);
        System.out.println("Storage: " + storage);
        System.out.println("Graphics Card: " + graphicsCard);
        System.out.println("-----------------------------");
    }

    // Standard Getters and Setters
    public String getMotherboard() { return motherboard; }
    public void setMotherboard(String motherboard) { this.motherboard = motherboard; }

    public String getProcessor() { return processor; }
    public void setProcessor(String processor) { this.processor = processor; }

    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }

    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }

    public String getGraphicsCard() { return graphicsCard; }
    public void setGraphicsCard(String graphicsCard) { this.graphicsCard = graphicsCard; }
}
