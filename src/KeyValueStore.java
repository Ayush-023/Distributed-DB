import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class KeyValueStore {
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        return store.getOrDefault(key, "NOT_FOUND");
    }

    public void setAll(Map<String, String> data) {
        store.clear();
        store.putAll(data);
    }

    public Map<String, String> getAll() {
        return store;
    }

    public void printAll() {
        store.forEach((k, v) -> System.out.println(k + " = " + v));
    }
}