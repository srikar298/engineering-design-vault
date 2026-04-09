package builder.builders;

/**
 * Concrete Builder for HP Desktops.
 */
public class HpDesktopBuilder extends DesktopBuilder {
    @Override
    public void buildMotherboard() {
        desktop.setMotherboard("HP Motherboard");
    }

    @Override
    public void buildProcessor() {
        desktop.setProcessor("Intel Core i7-13700K");
    }

    @Override
    public void buildMemory() {
        desktop.setMemory("32GB DDR5 RAM");
    }

    @Override
    public void buildStorage() {
        desktop.setStorage("1TB NVMe SSD");
    }

    @Override
    public void buildGraphicsCard() {
        desktop.setGraphicsCard("NVIDIA RTX 4070 Ti");
    }
}
