package s3;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Bucket {
    private final String name;
    private final User owner;
    private final AccessControlList acl;
    private volatile boolean versioningEnabled = false;
    private final ConcurrentHashMap<String, S3Object> objects = new ConcurrentHashMap<>();

    public Bucket(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.acl = new AccessControlList(owner);
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public AccessControlList getAcl() {
        return acl;
    }

    public boolean isVersioningEnabled() {
        return versioningEnabled;
    }

    public void setVersioningEnabled(boolean versioningEnabled) {
        this.versioningEnabled = versioningEnabled;
    }

    public void putObject(String key, String content, StorageClass storageClass, User uploader) {
        S3Object s3Obj = objects.computeIfAbsent(key, k -> new S3Object(key, uploader));
        s3Obj.addVersion(content, storageClass, versioningEnabled);
    }

    public S3Object getObject(String key) {
        return objects.get(key);
    }

    public void deleteObject(String key) {
        objects.remove(key);
    }

    public Collection<S3Object> getObjects() {
        return objects.values();
    }
}
