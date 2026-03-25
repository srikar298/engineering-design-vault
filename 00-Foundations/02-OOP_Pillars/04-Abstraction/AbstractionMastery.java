/**
 * ============================================================================
 * 🎨 ABSTRACTION MASTERY: "Extension Points & The Template Method Pattern"
 * ============================================================================
 */

// 1. BEHAVIORAL CONTRACT (Interface)
// Focuses purely on "CAN-DO" behavior without any state.
interface Verifiable {
    boolean verifyIntegrity();
}

// 2. PARTIAL IMPLEMENTATION (Abstract Class)
// Holds shared state (bucketName) and provides the overarching Algorithm Skeleton.
abstract class BaseStorage implements Verifiable {
    protected String bucketName;

    public BaseStorage(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * THE TEMPLATE METHOD PATTERN
     * This method acts as a strict, non-overridable skeleton. All storage types MUST 
     * go through this exact audit pipeline. The actual storage mechanic is deferred 
     * to the child classes via the abstract writeBytes() method.
     */
    public final void upload(String fileName) {
        System.out.println("\n[AUDIT] Starting upload pipeline to " + bucketName + "...");
        
        if (verifyIntegrity()) {
            
            // The Abstract hook: The child determines HOW this happens.
            writeBytes(fileName);
            
            System.out.println("[AUDIT] Success: File securely processed.");
        } else {
            System.out.println("[AUDIT] FAILED: Integrity check failed.");
        }
    }

    // The Extension Point: Specific logic intentionally left blank for children to solve.
    protected abstract void writeBytes(String fileName);
}

// ----------------------------------------------------------------------------
// 3. CONCRETE IMPLEMENTATIONS (Filling in the blanks)
// ----------------------------------------------------------------------------
class AwsS3Storage extends BaseStorage {
    public AwsS3Storage() { super("S3-US-EAST-1"); }

    @Override
    public boolean verifyIntegrity() { 
        System.out.println("-> Checking AWS IAM Roles and Bucket Policies...");
        return true; 
    }

    @Override
    protected void writeBytes(String fileName) {
        System.out.println("-> Streaming [" + fileName + "] via AWS S3 Multi-part Upload API...");
    }
}

class LocalDiskStorage extends BaseStorage {
    public LocalDiskStorage() { super("/mnt/secure_data"); }

    @Override
    public boolean verifyIntegrity() { 
        System.out.println("-> Checking Linux OS read/write Permissions...");
        return true; 
    }

    @Override
    protected void writeBytes(String fileName) {
        System.out.println("-> Writing [" + fileName + "] directly to local disk sectors...");
    }
}

// ----------------------------------------------------------------------------
// 🚀 EXECUTION
// ----------------------------------------------------------------------------
public class AbstractionMastery {
    public static void main(String[] args) {
        System.out.println("=== 🎨 Abstraction & The Template Method Pattern ===");

        // The exact same public API (upload) triggers entirely different under-the-hood logic,
        // while maintaining the exact same security audit wrapper.
        
        BaseStorage cloudStorage = new AwsS3Storage();
        cloudStorage.upload("financial_report.pdf");

        BaseStorage localStorage = new LocalDiskStorage();
        localStorage.upload("server_keys.pem");
    }
}
