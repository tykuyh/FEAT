
import android.util.Log;

/**
 * Created by 36202 on 2017/10/20.
 */

public class LogUtils {

    private final static String TAG = "Test";
    private static boolean isDebug = false;

    public static void setDebug(boolean debug){
        isDebug = debug;
    }

    public static boolean isDebug(){
        return isDebug;
    }

    public static void d(String msg){
        if (isDebug){
            Log.d(TAG,msg);
        }
    }
}
