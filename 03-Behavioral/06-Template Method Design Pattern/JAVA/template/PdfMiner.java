package template;

public class PdfMiner extends DataMiner {

    @Override
    protected byte[] extractData() {
        System.out.println("   -> PDF Custom Action: Executing complex OCR byte extraction.");
        return new byte[]{0x01, 0x02};
    }

    @Override
    protected String parseData(byte[] rawData) {
        System.out.println("   -> PDF Custom Action: Parsing extracted bytes into plain text string.");
        return "Pdf_Content";
    }

    /**
     * PDF overrides the default Hook!
     */
    @Override
    protected void analyzeData(String parsedData) {
        System.out.println("   -> PDF Hook Override: Archiving text and applying NLP sentiment analysis.");
    }
}
