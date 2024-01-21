import java.util.EventObject;

public class TriggerEvent extends EventObject {
    private final String message;

    public TriggerEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
