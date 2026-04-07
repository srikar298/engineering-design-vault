package cloud;

public class File implements IFileSystemNode {
    private final String name;
    private final long sizeBytes;

    public File(String name, long sizeBytes) {
        this.name = name;
        this.sizeBytes = sizeBytes;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📄 " + name + " (" + sizeBytes + " bytes)");
    }

    @Override
    public long getSize() {
        return sizeBytes;
    }
}
