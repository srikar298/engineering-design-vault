package sdk.metrics;

public class DotProductSimilarity implements ISimilarityCalculator {
    @Override
    public double calculate(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vector lengths do not match.");
        }
        double dotProduct = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
        }
        return dotProduct;
    }
}
