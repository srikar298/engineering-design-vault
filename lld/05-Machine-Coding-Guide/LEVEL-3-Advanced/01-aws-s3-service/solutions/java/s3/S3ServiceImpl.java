package s3;

import java.util.concurrent.ConcurrentHashMap;

public class S3ServiceImpl implements IS3Service {
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<StorageClass, IStorageStrategy> strategies = new ConcurrentHashMap<>();

    public S3ServiceImpl() {
        strategies.put(StorageClass.STANDARD, new StandardStorageStrategy());
        strategies.put(StorageClass.INTELLIGENT_TIERING, new StandardStorageStrategy()); // Intelligent-Tiering default
        strategies.put(StorageClass.GLACIER, new GlacierStorageStrategy());
    }

    // Package-private method to allow the Proxy to access buckets directly for validation
    public Bucket getBucketRaw(String bucketName) {
        return buckets.get(bucketName);
    }

    @Override
    public void createBucket(String bucketName, User user) {
        if (buckets.containsKey(bucketName)) {
            throw new IllegalArgumentException("Bucket already exists: " + bucketName);
        }
        buckets.put(bucketName, new Bucket(bucketName, user));
        System.out.printf("[S3Core] Created bucket: %s owned by %s\n", bucketName, user.getUserId());
    }

    @Override
    public void setBucketVersioning(String bucketName, boolean enabled, User user) {
        Bucket bucket = buckets.get(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");
        bucket.setVersioningEnabled(enabled);
        System.out.printf("[S3Core] Set versioning = %b on bucket %s\n", enabled, bucketName);
    }

    @Override
    public void putObject(String bucketName, String key, String content, StorageClass storageClass, User user) {
        Bucket bucket = buckets.get(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");

        bucket.putObject(key, content, storageClass, user);
        S3Object obj = bucket.getObject(key);
        ObjectVersion latest = obj.getLatestVersion();
        
        System.out.printf("[S3Core] Put Object: %s/%s (Version: %s, Size: %d bytes, Class: %s)\n", 
            bucketName, key, latest.getVersionId(), latest.getSizeBytes(), storageClass);
    }

    @Override
    public String getObject(String bucketName, String key, User user) {
        Bucket bucket = buckets.get(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");

        S3Object obj = bucket.getObject(key);
        if (obj == null) throw new IllegalArgumentException("Object not found: " + key);

        ObjectVersion ver = obj.getLatestVersion();
        return retrieveContent(ver);
    }

    @Override
    public String getObjectVersion(String bucketName, String key, String versionId, User user) {
        Bucket bucket = buckets.get(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");

        S3Object obj = bucket.getObject(key);
        if (obj == null) throw new IllegalArgumentException("Object not found: " + key);

        ObjectVersion ver = obj.getVersion(versionId);
        if (ver == null) throw new IllegalArgumentException("Version not found: " + versionId);

        return retrieveContent(ver);
    }

    @Override
    public void deleteObject(String bucketName, String key, User user) {
        Bucket bucket = buckets.get(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");
        
        bucket.deleteObject(key);
        System.out.printf("[S3Core] Deleted Object: %s/%s\n", bucketName, key);
    }

    @Override
    public void grantBucketPermission(String bucketName, User targetUser, Permission perm, User user) {
        Bucket bucket = buckets.get(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");
        bucket.getAcl().grantPermission(targetUser, perm);
        System.out.printf("[S3Core] Granted %s permission to %s on bucket %s\n", 
            perm, targetUser.getUserId(), bucketName);
    }

    @Override
    public void grantObjectPermission(String bucketName, String key, User targetUser, Permission perm, User user) {
        Bucket bucket = buckets.get(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");
        S3Object obj = bucket.getObject(key);
        if (obj == null) throw new IllegalArgumentException("Object not found");
        
        obj.getAcl().grantPermission(targetUser, perm);
        System.out.printf("[S3Core] Granted %s permission to %s on object %s/%s\n", 
            perm, targetUser.getUserId(), bucketName, key);
    }

    private String retrieveContent(ObjectVersion ver) {
        IStorageStrategy strategy = strategies.get(ver.getStorageClass());
        long delay = strategy.getRetrievalDelayMs();
        if (delay > 0) {
            System.out.printf("[S3Core] Object class is %s. Simulating retrieval delay of %d ms...\n", 
                ver.getStorageClass(), delay);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return ver.getContent();
    }
}
