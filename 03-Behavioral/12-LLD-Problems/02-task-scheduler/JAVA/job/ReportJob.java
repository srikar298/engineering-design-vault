package job;

public class ReportJob extends BaseJob {

    public ReportJob(int priority) {
        super(priority);
    }

    @Override
    protected void runTask() {
        System.out.println("   -> Custom Task: Aggregating analytics and emailing PDF Report to CEO...");
    }
}
