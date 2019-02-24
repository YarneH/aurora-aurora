package com.aurora.kernel;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Communication bus that is used in the kernel
 */
class Bus {
    // bus subject is observable and observer
    // of events at the same time
    private final Subject<Event> busSubject = PublishSubject.create();


    /**
     * Register for a particular type of events
     *
     * @param eventClass the class of the events you want to subscribe to
     * @return an Observable of events
     */
    public <T extends Event>
    Observable<T> register(final Class<T> eventClass) {
        return busSubject
                .filter(event -> event.getClass().equals(eventClass)) // Filter events based on class
                .map(obj -> (T) obj);
    }


    /**
     * Posts a new event on the bus
     *
     * @param event the event to post
     */
    public void post(Event event) {
        busSubject.onNext(event);
    }
}
