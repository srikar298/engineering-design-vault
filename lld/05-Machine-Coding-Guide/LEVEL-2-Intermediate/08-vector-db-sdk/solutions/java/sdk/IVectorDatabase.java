package sdk;

import java.util.List;

public interface IVectorDatabase {
    void upsert(List<Vector> vectors);
    List<Vector> query(float[] queryVector, int topK);
}
