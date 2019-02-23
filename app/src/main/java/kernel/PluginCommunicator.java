package kernel;

public class PluginCommunicator extends Communicator {
    private static final PluginCommunicator communicator = new PluginCommunicator();


    private PluginCommunicator() {}

    public static PluginCommunicator getCommunicator() {
        return communicator;
    }

    @Override
    public void handleEvent(Event event) {

    }
}
