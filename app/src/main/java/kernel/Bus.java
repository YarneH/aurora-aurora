package kernel;

public class Bus {
    private static final Bus bus = new Bus();

    private final Subject<Object, Object> busSubject = new SerializedSubject(PublishSubject.create());

    private Bus() {}

    public static Bus getBus() {
        return bus;
    }
}
