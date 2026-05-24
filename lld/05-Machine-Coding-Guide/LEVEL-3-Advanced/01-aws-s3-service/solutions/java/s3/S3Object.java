package s3;

import java.util.ArrayList;
import java.util.List;

public class S3Object {
    private final String key;
    private final List<ObjectVersion> versions = new ArrayList<>();
    private final AccessControlList acl;

    public S3Object(String key, User owner) {
        this.key = key;
        this.acl = new AccessControlList(owner);
    }

    public String getKey() {
        return key;
    }

    public AccessControlList getAcl() {
        return acl;
    }

    public synchronized void addVersion(String content, StorageClass storageClass, boolean versioningEnabled) {
        if (!versioningEnabled) {
            // Overwrite latest version in-place, keeping single version history
            versions.clear();
            versions.add(new ObjectVersion("null", content, storageClass));
        } else {
            String newVersionId = "v" + (versions.size() + 1);
            versions.add(0, new ObjectVersion(newVersionId, content, storageClass)); // Put latest at the beginning
        }
    }

    public synchronized ObjectVersion getLatestVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.get(0);
    }

    public synchronized ObjectVersion getVersion(String versionId) {
        for (ObjectVersion ver : versions) {
            if (ver.getVersionId().equals(versionId)) {
                return ver;
            }
        }
        return null;
    }

    public synchronized List<ObjectVersion> getVersions() {
        return new ArrayList<>(versions);
    }
}
