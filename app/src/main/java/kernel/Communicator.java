package kernel;

import android.util.Log;

/**
 * Common supertype of all communicators in the kernel
 */
abstract class Communicator implements EventHandler {
    protected Bus bus;
}
