package com.aurora.kernel;

import com.aurora.kernel.event.Event;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Communication bus that is used in the kernel
 */
class Bus {
    // bus subject is observable and observer
    // of events at the same time
    private final Subject<Event> mBusSubject = PublishSubject.create();
    private Scheduler mScheduler;

    /**
     * Constructs a new bus instance
     *
     * @param scheduler a scheduler (from the Schedulers class like e.g. computation or trampoline for testing purposes)
     */
    public Bus(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    /**
     * Register for a particular type of events
     *
     * @param eventClass the class of the events you want to subscribe to
     * @return an Observable of events
     */
    <T extends Event>
    Observable<T> register(final Class<T> eventClass) {
        // observeOn function is used to set the thread on which the result is observed
        // Filter events based on class
        // Finally, a map is used to cast it to the right type
        return mBusSubject
                .observeOn(mScheduler)
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
