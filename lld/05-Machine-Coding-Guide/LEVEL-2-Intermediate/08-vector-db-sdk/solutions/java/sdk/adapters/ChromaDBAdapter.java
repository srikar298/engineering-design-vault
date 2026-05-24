package sdk.adapters;

import sdk.IVectorDatabase;
import sdk.Vector;
import sdk.metrics.ISimilarityCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChromaDBAdapter implements IVectorDatabase {
    private final List<Vector> storage = new ArrayList<>();
    private final ISimilarityCalculator calculator;

    public ChromaDBAdapter(ISimilarityCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void upsert(List<Vector> vectors) {
        for (Vector v : vectors) {
            storage.removeIf(existing -> existing.getId().equals(v.getId()));
            storage.add(v);
        }
        System.out.printf("[ChromaDB] Upserted %d vectors. Total database size: %d\n", vectors.size(), storage.size());
    }

    @Override
    public List<Vector> query(float[] queryVector, int topK) {
        System.out.printf("[ChromaDB] Querying top %d matches...\n", topK);
        return storage.stream()
                .sorted((v1, v2) -> Double.compare(
                        calculator.calculate(v2.getEmbedding(), queryVector),
                        calculator.calculate(v1.getEmbedding(), queryVector)
                ))
                .limit(topK)
                .collect(Collectors.toList());
    }
}
