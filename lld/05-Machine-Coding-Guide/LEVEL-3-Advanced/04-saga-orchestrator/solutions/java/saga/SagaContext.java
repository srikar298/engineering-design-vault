package saga;

import java.util.HashMap;
import java.util.Map;

public class SagaContext {
    private final String sagaId;
    private final Map<String, Object> data = new HashMap<>();
    private SagaState state;

    public SagaContext(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getSagaId() { return sagaId; }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public SagaState getState() { return state; }
    public void setState(SagaState state) { this.state = state; }
}
