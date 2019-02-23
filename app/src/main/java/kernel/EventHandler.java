package kernel;

/**
 * Classes implementing this interface should handle incoming events
 */
public interface EventHandler {
    void handleEvent(Event event);
}
