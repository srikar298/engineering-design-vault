package template;

public class CsvMiner extends DataMiner {

    @Override
    protected byte[] extractData() {
        System.out.println("   -> CSV Custom Action: Reading comma-separated lines.");
        return new byte[]{0x05, 0x06};
    }

    @Override
    protected String parseData(byte[] rawData) {
        System.out.println("   -> CSV Custom Action: Mapping data into relational Array format.");
        return "Csv_Grid_Content";
    }
    
    // Notice: We don't override the analyzeData() hook. 
    // It will fall back to the default Database storage mechanism.
}
