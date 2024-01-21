import java.util.EventListener;

public interface TriggerListener extends EventListener {
    void onEventOccurred(TriggerEvent event);
}
