package job;

public class BackupJob extends BaseJob {

    public BackupJob(int priority) {
        super(priority);
    }

    @Override
    protected boolean preFlightCheck() {
        System.out.println("   -> Custom Action: Checking if AWS S3 Bucket is reachable...");
        return super.preFlightCheck();
    }

    @Override
    protected void runTask() {
        System.out.println("   -> Custom Task: Compressing Database and uploading to S3...");
    }
}
