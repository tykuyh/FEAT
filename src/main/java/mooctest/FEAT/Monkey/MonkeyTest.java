package mooctest.FEAT.Monkey;

import java.io.File;
import java.io.IOException;

import mooctest.FEAT.Interface.AutomatorToolsTest;
import mooctest.FEAT.Util.OSUtil;

public class MonkeyTest extends AutomatorToolsTest{
	String batPath;
	public MonkeyTest(String deviceID, String appPackage,
			String appPath,String outputdir,String bp) throws IOException {
		super("Monkey", deviceID, appPackage, appPath,outputdir);
		batPath=bp;
	}
	
	@Override
	public void run() {
		install();
		startCaptureLog(this.getDeviceID());
		System.out.println(this.getDeviceID() + " "  + "Ready to test with Monkey , stop 10 second");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		String monkeyLogsPath = getOutputDir() + File.separator + getDeviceID() + File.separator + "Monkey.log";
        Runtime r=Runtime.getRuntime();	//Monkey
        Process process = null;
        try {
            String cmd = OSUtil.getCmd();
            if(OSUtil.isWin()) {
            	//"Commands\\win\\monkey.bat"
            	process = r.exec(batPath + " " + this.getDeviceID() + " " + this.getAppPackage() + " " + monkeyLogsPath);
            }else {
            	System.out.println(cmd + " " + batPath + " " + this.getDeviceID() + " " + this.getAppPackage() + " " + monkeyLogsPath);
            	process = r.exec(cmd + " " + batPath + " " + this.getDeviceID() + " " + this.getAppPackage() + " " + monkeyLogsPath);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
			process.waitFor();
			System.out.println(this.getDeviceID() + " "  + " Monkey is done , ready to destroy");
			endCaptureLog();
			process.destroy();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        System.out.println(this.getDeviceID() + " "  + "finish monkey");
	}
	
}
