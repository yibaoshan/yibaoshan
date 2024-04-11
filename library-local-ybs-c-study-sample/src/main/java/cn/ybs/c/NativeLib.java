package cn.ybs.c;

public class NativeLib {

    // Used to load the 'c' library on application startup.
    static {
        System.loadLibrary("simple-lib");
    }

    /**
     * A native method that is implemented by the 'c' native library,
     * which is packaged with this application.
     */
    public static native String callJNI();

}