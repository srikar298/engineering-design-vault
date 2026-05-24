package s3;

public class S3SecurityProxy implements IS3Service {
    private final S3ServiceImpl realService;

    public S3SecurityProxy(S3ServiceImpl realService) {
        this.realService = realService;
    }

    private void checkBucketPermission(String bucketName, User user, Permission perm) {
        Bucket bucket = realService.getBucketRaw(bucketName);
        if (bucket == null) {
            throw new IllegalArgumentException("Bucket not found: " + bucketName);
        }
        if (!bucket.getAcl().hasPermission(user, perm)) {
            throw new SecurityException("Access Denied: User " + user.getUserId() + 
                " lacks " + perm + " permission on bucket: " + bucketName);
        }
    }

    private void checkObjectPermission(String bucketName, String key, User user, Permission perm) {
        Bucket bucket = realService.getBucketRaw(bucketName);
        if (bucket == null) {
            throw new IllegalArgumentException("Bucket not found: " + bucketName);
        }
        // If user has bucket-level permission, that cascades
        if (bucket.getAcl().hasPermission(user, perm)) {
            return;
        }
        // Otherwise check object-level ACL
        S3Object obj = bucket.getObject(key);
        if (obj == null) {
            throw new IllegalArgumentException("Object not found: " + key);
        }
        if (!obj.getAcl().hasPermission(user, perm)) {
            throw new SecurityException("Access Denied: User " + user.getUserId() + 
                " lacks " + perm + " permission on object: " + bucketName + "/" + key);
        }
    }

    private void verifyBucketOwner(String bucketName, User user) {
        Bucket bucket = realService.getBucketRaw(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");
        if (!bucket.getAcl().getOwner().equals(user)) {
            throw new SecurityException("Access Denied: User " + user.getUserId() + 
                " is not the owner of bucket: " + bucketName);
        }
    }

    @Override
    public void createBucket(String bucketName, User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        realService.createBucket(bucketName, user);
    }

    @Override
    public void setBucketVersioning(String bucketName, boolean enabled, User user) {
        verifyBucketOwner(bucketName, user);
        realService.setBucketVersioning(bucketName, enabled, user);
    }

    @Override
    public void putObject(String bucketName, String key, String content, StorageClass storageClass, User user) {
        checkBucketPermission(bucketName, user, Permission.WRITE);
        realService.putObject(bucketName, key, content, storageClass, user);
    }

    @Override
    public String getObject(String bucketName, String key, User user) {
        checkObjectPermission(bucketName, key, user, Permission.READ);
        return realService.getObject(bucketName, key, user);
    }

    @Override
    public String getObjectVersion(String bucketName, String key, String versionId, User user) {
        checkObjectPermission(bucketName, key, user, Permission.READ);
        return realService.getObjectVersion(bucketName, key, versionId, user);
    }

    @Override
    public void deleteObject(String bucketName, String key, User user) {
        checkBucketPermission(bucketName, user, Permission.WRITE);
        realService.deleteObject(bucketName, key, user);
    }

    @Override
    public void grantBucketPermission(String bucketName, User targetUser, Permission perm, User user) {
        verifyBucketOwner(bucketName, user);
        realService.grantBucketPermission(bucketName, targetUser, perm, user);
    }

    @Override
    public void grantObjectPermission(String bucketName, String key, User targetUser, Permission perm, User user) {
        Bucket bucket = realService.getBucketRaw(bucketName);
        if (bucket == null) throw new IllegalArgumentException("Bucket not found");
        S3Object obj = bucket.getObject(key);
        if (obj == null) throw new IllegalArgumentException("Object not found");

        // Object owner or bucket owner can modify object permissions
        boolean isBucketOwner = bucket.getAcl().getOwner().equals(user);
        boolean isObjOwner = obj.getAcl().getOwner().equals(user);

        if (!isBucketOwner && !isObjOwner) {
            throw new SecurityException("Access Denied: Only bucket owner or object owner can modify ACLs.");
        }

        realService.grantObjectPermission(bucketName, key, targetUser, perm, user);
    }
}
