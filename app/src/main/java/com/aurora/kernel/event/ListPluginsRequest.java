package com.aurora.kernel.event;

/**
 * Event to request a list of installed plugins
 * @see ListPluginsResponse
 */
public class ListPluginsRequest implements Event {

    /**
     * Constructs a new ListPluginsRequest
     */
    public ListPluginsRequest() {
        super();
    }
}
