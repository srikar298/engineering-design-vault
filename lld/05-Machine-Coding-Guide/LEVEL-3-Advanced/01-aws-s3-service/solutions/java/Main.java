import s3.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🪣 Enterprise AWS S3 Storage Service Simulation 🪣");
        System.out.println("=================================================\n");

        // 1. Setup Users
        User admin = new User("admin-user");
        User alice = new User("alice-user");
        User bob = new User("bob-user");

        // 2. Initialize Service & Security Proxy
        S3ServiceImpl coreService = new S3ServiceImpl();
        IS3Service s3 = new S3SecurityProxy(coreService);

        // 3. Create Bucket and Enable Versioning
        String bucket = "company-assets";
        s3.createBucket(bucket, admin);
        s3.setBucketVersioning(bucket, true, admin);

        System.out.println("\n--- 🏁 Test 1: Versioning Put/Get ---");
        // Upload Version 1
        s3.putObject(bucket, "config.txt", "port=8080\nenv=production", StorageClass.STANDARD, admin);
        // Upload Version 2
        s3.putObject(bucket, "config.txt", "port=9000\nenv=production\ndebug=true", StorageClass.STANDARD, admin);

        // Retrieve Latest Version (Should be Version 2)
        String latestContent = s3.getObject(bucket, "config.txt", admin);
        System.out.println("\n[Latest Content]:\n" + latestContent);

        // Retrieve Version 1 specifically
        String v1Content = s3.getObjectVersion(bucket, "config.txt", "v1", admin);
        System.out.println("\n[Version v1 Content]:\n" + v1Content);


        System.out.println("\n--- 🔒 Test 2: Security & ACL Proxy Validation ---");
        // Bob tries to read the config (No permission yet -> Expect SecurityException)
        try {
            System.out.println("Bob attempting to read config.txt...");
            s3.getObject(bucket, "config.txt", bob);
        } catch (SecurityException e) {
            System.out.println("❌ Bob blocked: " + e.getMessage());
        }

        // Admin grants READ permission to Bob
        System.out.println("\nAdmin granting READ permission on bucket to Bob...");
        s3.grantBucketPermission(bucket, bob, Permission.READ, admin);

        // Bob tries again -> Should succeed!
        try {
            System.out.println("Bob attempting to read config.txt after permission grant...");
            String bobRead = s3.getObject(bucket, "config.txt", bob);
            System.out.println("🟢 Bob success! Read content:\n" + bobRead);
        } catch (SecurityException e) {
            System.out.println("❌ Bob blocked: " + e.getMessage());
        }

        // Alice tries to write to the bucket (No write permission -> Expect SecurityException)
        try {
            System.out.println("\nAlice attempting to write logs.txt...");
            s3.putObject(bucket, "logs.txt", "error occurred", StorageClass.STANDARD, alice);
        } catch (SecurityException e) {
            System.out.println("❌ Alice blocked: " + e.getMessage());
        }


        System.out.println("\n--- ❄️ Test 3: Storage Strategies & Glacier Retrieval ---");
        // Upload large file to Glacier tier
        s3.putObject(bucket, "archive-2025.zip", "[Binary Zip Stream]", StorageClass.GLACIER, admin);

        // Retrieve Glacier object (Expect simulated delay)
        long start = System.currentTimeMillis();
        String archiveContent = s3.getObject(bucket, "archive-2025.zip", admin);
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("\n🟢 Retrieved Glacier Object in %d ms: %s\n", elapsed, archiveContent);
    }
}
