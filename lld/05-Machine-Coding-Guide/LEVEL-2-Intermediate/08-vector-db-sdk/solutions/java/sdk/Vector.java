package sdk;

import java.util.Arrays;
import java.util.Map;

public class Vector {
    private final String id;
    private final float[] embedding;
    private final Map<String, String> metadata;

    public Vector(String id, float[] embedding, Map<String, String> metadata) {
        this.id = id;
        this.embedding = embedding;
        this.metadata = metadata;
    }

    public String getId() { return id; }
    public float[] getEmbedding() { return embedding; }
    public Map<String, String> getMetadata() { return metadata; }

    @Override
    public String toString() {
        return String.format("Vector{id='%s', embedding=%s, metadata=%s}", id, Arrays.toString(embedding), metadata);
    }
}
