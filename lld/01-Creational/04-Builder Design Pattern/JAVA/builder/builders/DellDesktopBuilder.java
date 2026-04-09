package builder.builders;

/**
 * Concrete Builder for Dell Desktops.
 */
public class DellDesktopBuilder extends DesktopBuilder {
    @Override
    public void buildMotherboard() {
        desktop.setMotherboard("Dell Motherboard");
    }

    @Override
    public void buildProcessor() {
        desktop.setProcessor("Intel Core i9-13900K");
    }

    @Override
    public void buildMemory() {
        desktop.setMemory("64GB DDR5 RAM");
    }

    @Override
    public void buildStorage() {
        desktop.setStorage("2TB NVMe SSD");
    }

    @Override
    public void buildGraphicsCard() {
        desktop.setGraphicsCard("NVIDIA RTX 4090");
    }
}
