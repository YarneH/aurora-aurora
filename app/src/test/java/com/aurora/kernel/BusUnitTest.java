package com.aurora.kernel;

import com.aurora.kernel.event.Event;

import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

public class BusUnitTest {

    private static Bus mBus;

    @BeforeClass
    public static void initialize() {
        // Allocate bus
        mBus = new Bus(Schedulers.trampoline());
    }

    @Test
    public void Bus_register_shouldRegisterForEvent() {
        // Call register method
        Observable<TestEvent> events = mBus.register(TestEvent.class);

        // Create test event
        String testMessage = "This is a test message";
        TestEvent testEvent = new TestEvent(testMessage);

        // Call subscribe method on events
        events.subscribe(receivedEvent ->
                assertEquals("Not equal", testEvent.testMessage, receivedEvent.testMessage)
        );

        // Post event
        mBus.post(testEvent);
    }


    /**
     * Test event class
     */
    private class TestEvent implements Event {
        private String testMessage;

        public TestEvent(String testMessage) {
            this.testMessage = testMessage;
        }
    }
}
