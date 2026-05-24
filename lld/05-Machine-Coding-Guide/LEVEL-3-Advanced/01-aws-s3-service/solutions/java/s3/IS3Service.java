package s3;

public interface IS3Service {
    void createBucket(String bucketName, User user);
    void setBucketVersioning(String bucketName, boolean enabled, User user);
    void putObject(String bucketName, String key, String content, StorageClass storageClass, User user);
    String getObject(String bucketName, String key, User user);
    String getObjectVersion(String bucketName, String key, String versionId, User user);
    void deleteObject(String bucketName, String key, User user);
    void grantBucketPermission(String bucketName, User targetUser, Permission perm, User user);
    void grantObjectPermission(String bucketName, String key, User targetUser, Permission perm, User user);
}
