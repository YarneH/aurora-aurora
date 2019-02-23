package kernel;

import android.util.Log;

class AuroraCommunicator extends Communicator {
    private static AuroraCommunicator communicator = new AuroraCommunicator();

    private AuroraCommunicator() {}

    public static AuroraCommunicator getCommunicator() {
        return communicator;
    }

    @Override
    public void handleEvent(Event event) {
        Log.d("AuroraCommunicator", "Not implemented yet");
    }
}
