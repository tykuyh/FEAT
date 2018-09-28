
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by 36202 on 2017/10/20.
 */

public class jacocotest {

    //ec
    private static String DEFAULT_COVERAGE_FILE_PATH ="/sdcard" + "/coverage.ec";

    //project
    private static String PROJECT_PATH;

    /**
     * init
     * @param projectPath  'projectPath' + '/app/build/outputs/code-coverage/'
     * @param isDebug log
     */
    public static void init(String projectPath, boolean isDebug){
        PROJECT_PATH = projectPath + "/app/build/outputs/code-coverage";
        LogUtils.setDebug(isDebug);
    }

    /**
     * create ec
     *
     * @param isNew 
     */
    public static void generateEcFile(boolean isNew) {
        if (!LogUtils.isDebug())return;
        OutputStream out = null;
        File mCoverageFilePath = new File(DEFAULT_COVERAGE_FILE_PATH);
        try {
            if (isNew && mCoverageFilePath.exists()) {
                LogUtils.d("JacocoHelper_generateEcFile: remove old ec file");
                mCoverageFilePath.delete();
            }
            if (!mCoverageFilePath.exists()) {
                mCoverageFilePath.createNewFile();
            }
            out = new FileOutputStream(mCoverageFilePath.getPath(), true);
            Object agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null);
            out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class)
                    .invoke(agent, false));
        } catch (Exception e) {
            LogUtils.d("JacocoHelper_generateEcFile: " + e.getMessage());
        } finally {
            if (out == null)
                return;
            try {
                out.close();
                LogUtils.d("JacocoHelper_generateEcFile: "+mCoverageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.d(getAdbPullCmd());
    }


    /**
     * output ec
     * @return adb 
     */
    public static String getAdbPullCmd(){
        String adb = "adb pull " + DEFAULT_COVERAGE_FILE_PATH + " " + PROJECT_PATH;
        LogUtils.d("output ec command: "+adb);
        return adb;
    }
}
