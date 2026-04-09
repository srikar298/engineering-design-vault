import template.CsvMiner;
import template.DataMiner;
import template.PdfMiner;

/**
 * <h1>Template Method Demonstration</h1>
 * 
 * <p>Notice how the Main client calls `mineData()` which is the Template Method.
 * The Client does NOT trigger the individual steps. The superclass handles 
 * the orchestration (Inversion of Control).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Template Method: ETL Data Pipeline Demo        ");
        System.out.println("==================================================\n");

        System.out.println("--- Scenario 1: Processing a PDF Report ---");
        DataMiner pdfPipeline = new PdfMiner();
        pdfPipeline.mineData("annual_report_2024.pdf");

        System.out.println("--- Scenario 2: Processing a CSV Database Dump ---");
        DataMiner csvPipeline = new CsvMiner();
        csvPipeline.mineData("users_export.csv");
    }
}
