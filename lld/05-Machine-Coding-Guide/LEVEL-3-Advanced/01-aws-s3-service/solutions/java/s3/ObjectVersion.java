package s3;

public class ObjectVersion {
    private final String versionId;
    private final String content;
    private final long sizeBytes;
    private final StorageClass storageClass;
    private final long uploadTime;

    public ObjectVersion(String versionId, String content, StorageClass storageClass) {
        this.versionId = versionId;
        this.content = content;
        this.sizeBytes = content.getBytes().length;
        this.storageClass = storageClass;
        this.uploadTime = System.currentTimeMillis();
    }

    public String getVersionId() {
        return versionId;
    }

    public String getContent() {
        return content;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public StorageClass getStorageClass() {
        return storageClass;
    }

    public long getUploadTime() {
        return uploadTime;
    }
}
