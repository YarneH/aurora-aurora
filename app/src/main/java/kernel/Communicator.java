package kernel;

import android.util.Log;

/**
 * Common supertype of all communicators in the kernel
 */
abstract class Communicator implements EventHandler {
    protected Bus bus;

    void subscribe(Class<? extends Event> eventtype) {
        Log.d("Communicator", "Not implemented");
    }

}
