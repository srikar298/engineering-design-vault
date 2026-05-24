import sdk.IVectorDatabase;
import sdk.Vector;
import sdk.adapters.PineconeAdapter;
import sdk.metrics.CosineSimilarity;
import sdk.metrics.ISimilarityCalculator;
import sdk.proxy.VectorCacheProxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Unified Vector Database SDK Simulation ===");

        // 1. Choose similarity strategy
        ISimilarityCalculator similarity = new CosineSimilarity();

        // 2. Instantiate Database Adapter
        IVectorDatabase pineconeDb = new PineconeAdapter(similarity);

        // 3. Wrap with Semantic Caching Proxy
        // Capacity: 2, Threshold: 0.99 similarity
        IVectorDatabase sdk = new VectorCacheProxy(pineconeDb, similarity, 2, 0.99);

        // 4. Create mock vectors
        Map<String, String> meta1 = new HashMap<>();
        meta1.put("text", "Machine learning is fun.");
        Vector v1 = new Vector("id-1", new float[]{0.1f, 0.2f, 0.9f}, meta1);

        Map<String, String> meta2 = new HashMap<>();
        meta2.put("text", "Deep learning scales well.");
        Vector v2 = new Vector("id-2", new float[]{0.15f, 0.25f, 0.85f}, meta2);

        Map<String, String> meta3 = new HashMap<>();
        meta3.put("text", "Pineapple on pizza is questionable.");
        Vector v3 = new Vector("id-3", new float[]{0.8f, 0.1f, 0.1f}, meta3);

        System.out.println("\n--- Upserting vectors into SDK ---");
        sdk.upsert(Arrays.asList(v1, v2, v3));

        // 5. Query 1: Initial search
        float[] query1 = new float[]{0.1f, 0.2f, 0.9f};
        System.out.println("\n--- Query 1: Initial search ---");
        List<Vector> res1 = sdk.query(query1, 2);
        printResults(res1);

        // 6. Query 2: Exact Match
        System.out.println("\n--- Query 2: Exact same vector ---");
        List<Vector> res2 = sdk.query(query1, 2);
        printResults(res2);

        // 7. Query 3: Highly Similar Match (Similarity should be >= 0.99)
        float[] query2 = new float[]{0.101f, 0.201f, 0.899f};
        System.out.println("\n--- Query 3: Slightly modified vector (semantic cache test) ---");
        List<Vector> res3 = sdk.query(query2, 2);
        printResults(res3);

        // 8. Query 4: Very Different Match (Cache Miss)
        float[] query3 = new float[]{0.85f, 0.05f, 0.1f};
        System.out.println("\n--- Query 4: Completely different query vector ---");
        List<Vector> res4 = sdk.query(query3, 1);
        printResults(res4);

        System.out.println("\n=== SDK Simulation Finished ===");
    }

    private static void printResults(List<Vector> results) {
        for (int i = 0; i < results.size(); i++) {
            Vector v = results.get(i);
            System.out.printf("  [%d] ID: %s, Text: %s\n", i + 1, v.getId(), v.getMetadata().get("text"));
        }
    }
}
