package kernel;

public class Kernel {
    // Singleton class
    private static final Kernel kernel = new Kernel();
    private AuroraCommunicator auroraCommunicator;

    /**
     * Starts and creates all communicator, keeping references
     */
    private Kernel() {
        this.auroraCommunicator = AuroraCommunicator.getCommunicator();
    }

    /**
     * gets a reference to the singleton kernel
     * @return a reference to the kernel
     */
    public static Kernel getKernel() {
        return kernel;
    }

    /**
     * gets a reference to the aurora communicator
     * @return AuroraCommunicator
     */
    public AuroraCommunicator getAuroraCommunicator() {
        return auroraCommunicator;
    }
}
