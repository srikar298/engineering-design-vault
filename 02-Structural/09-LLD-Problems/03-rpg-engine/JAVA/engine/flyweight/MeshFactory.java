package engine.flyweight;

import java.util.HashMap;
import java.util.Map;

public class MeshFactory {
    
    private static final Map<String, ParticleMesh> cache = new HashMap<>();

    public static ParticleMesh getMesh(String meshType) {
        if (!cache.containsKey(meshType)) {
            // Simulate reading heavy object from disk based on type
            String geometryData = "<Binary Vertex Data for " + meshType + ">";
            cache.put(meshType, new ParticleMesh(meshType, geometryData));
        }
        return cache.get(meshType);
    }
    
    public static int getVRAMFootprint() {
        // Assume each mesh is 5MB for the sake of the demo
        return cache.size() * 5;
    }
}
