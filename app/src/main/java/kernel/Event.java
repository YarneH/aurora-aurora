package kernel;

/**
 * Event class packing together some common fields every event should have
 */
abstract class Event {
    protected Communicator destination;
    protected Communicator source;
}
