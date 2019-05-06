package android.util;

// This class mocks the logging methods of android.util
// This class is needed for testing purposes
public class Log {
    public static int d(String tag, String msg) {
        System.out.println("DEBUG: " + tag + ": " + msg);
        return 0;
    }

    public static int d(String tag, String msg, Throwable throwable) {
        d(tag, msg);
        throwable.printStackTrace();
        return 0;
    }

    public static int i(String tag, String msg) {
        System.out.println("INFO: " + tag + ": " + msg);
        return 0;
    }

    public static int i(String tag, String msg, Throwable throwable) {
        i(tag, msg);
        throwable.printStackTrace();
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.println("WARN: " + tag + ": " + msg);
        return 0;
    }

    public static int w(String tag, String msg, Throwable throwable) {
        w(tag, msg);
        throwable.printStackTrace();
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.println("ERROR: " + tag + ": " + msg);
        return 0;
    }

    public static int e(String tag, String msg, Throwable throwable) {
        e(tag, msg);
        throwable.printStackTrace();
        return 0;
    }

    public static boolean isLoggable(String tag, int level ){
        return false;

    }

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;
}

