package android.log;

/**
 * @author r
 */
public class Log {
    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;

    public static boolean LOG = false;
    public static int a(Object... args) { return 0; }
    public static int e(Object... args) { return 0; }
    public static int w(Object... args) { return 0; }
    public static int i(Object... args) { return 0; }
    public static int d(Object... args) { return 0; }
    public static int v(Object... args) { return 0; }
    public static int p(Object... args) { return 0; }
    public static int ps(Object... args) { return 0; }
    public static void printStackTrace(Exception e) { }
}
