package com.aurora.kernel.event;

import com.aurora.kernel.Communicator;

/**
 * Event class packing together some common fields every event should have
 */
public abstract class Event {
    protected Communicator mDestination;
    protected Communicator mSource;

    public Event() {

    }
}
