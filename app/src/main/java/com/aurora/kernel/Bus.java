package com.aurora.kernel;

import com.aurora.kernel.event.Event;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Communication bus that is used in the kernel
 */
class Bus {
    // bus subject is observable and observer
    // of events at the same time
    private final Subject<Event> mBusSubject = PublishSubject.create();


    /**
     * Register for a particular type of events
     *
     * @param eventClass the class of the events you want to subscribe to
     * @return an Observable of events
     */
    <T extends Event>
    Observable<T> register(final Class<T> eventClass) {
        // Filter events based on class
        return mBusSubject
                .filter((Event event) -> event.getClass().equals(eventClass))
                .map((Event obj) -> {
                    if (eventClass.isInstance(obj)) {
                        // This is an 'unsafe type cast' but it is because of how generics work
                        // in Java. In reality, it will always be possible to cast an
                        // Event to the type T (which is a sub class of the event)
                        @SuppressWarnings("unchecked")
                        T res = (T) obj;
                        return res;
                    }

                    // This should not happen
                    return null;
                });
    }


    /**
     * Posts a new event on the bus
     *
     * @param event the event to post
     */
    void post(Event event) {
        mBusSubject.onNext(event);
    }
}
