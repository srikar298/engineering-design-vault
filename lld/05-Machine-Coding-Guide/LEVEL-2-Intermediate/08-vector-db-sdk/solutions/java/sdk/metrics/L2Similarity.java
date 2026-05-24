package sdk.metrics;

public class L2Similarity implements ISimilarityCalculator {
    @Override
    public double calculate(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vector lengths do not match.");
        }
        double sumSquare = 0.0;
        for (int i = 0; i < v1.length; i++) {
            sumSquare += Math.pow(v1[i] - v2[i], 2);
        }
        double distance = Math.sqrt(sumSquare);
        // Map distance to a similarity score between 0 and 1
        return 1.0 / (1.0 + distance);
    }
}
