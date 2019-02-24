package com.aurora.kernel;

/**
 * Event class packing together some common fields every event should have
 */
abstract class Event {
    protected Communicator mDestination;
    protected Communicator mSource;
}
