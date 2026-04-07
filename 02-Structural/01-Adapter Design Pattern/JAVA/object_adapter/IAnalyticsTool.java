package object_adapter;

/**
 * The Target Interface.
 * 
 * This is the interface our modern system expects to work with.
 * The Analytics engine only understands how to process JSON data.
 */
public interface IAnalyticsTool {
    void processJsonData(String jsonData);
}
