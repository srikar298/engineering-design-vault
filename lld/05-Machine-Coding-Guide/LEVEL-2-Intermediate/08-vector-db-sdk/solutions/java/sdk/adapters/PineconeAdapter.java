package sdk.adapters;

import sdk.IVectorDatabase;
import sdk.Vector;
import sdk.metrics.ISimilarityCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PineconeAdapter implements IVectorDatabase {
    private final List<Vector> storage = new ArrayList<>();
    private final ISimilarityCalculator calculator;

    public PineconeAdapter(ISimilarityCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void upsert(List<Vector> vectors) {
        // Simple mock upsert: replace if exists, else add
        for (Vector v : vectors) {
            storage.removeIf(existing -> existing.getId().equals(v.getId()));
            storage.add(v);
        }
        System.out.printf("[Pinecone] Upserted %d vectors. Total database size: %d\n", vectors.size(), storage.size());
    }

    @Override
    public List<Vector> query(float[] queryVector, int topK) {
        System.out.printf("[Pinecone] Querying top %d matches...\n", topK);
        // Calculate similarity score, sort, and slice to topK
        return storage.stream()
                .sorted((v1, v2) -> Double.compare(
                        calculator.calculate(v2.getEmbedding(), queryVector),
                        calculator.calculate(v1.getEmbedding(), queryVector)
                ))
                .limit(topK)
                .collect(Collectors.toList());
    }
}
