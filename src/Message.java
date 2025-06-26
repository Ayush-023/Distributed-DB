import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
    public String type;
    public String key;
    public String value;
    public Map<String, String> storeSnapshot;

    public Message(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }
}