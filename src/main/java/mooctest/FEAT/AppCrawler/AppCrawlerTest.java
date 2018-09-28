package mooctest.FEAT.AppCrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import mooctest.FEAT.Appium.AppiumManager;
import mooctest.FEAT.Interface.AutomatorToolsTest;
import mooctest.FEAT.Util.OSUtil;

public class AppCrawlerTest extends AutomatorToolsTest{
	String batPath;
	String toolPath;
	public AppCrawlerTest(String deviceID, String appPackage,
			String appPath,String outputdir,String bp,String tp) throws IOException {
		super("AppCrawler", deviceID, appPackage, appPath,outputdir);
		batPath=bp;
		toolPath=tp;
	}
	public String startServer(String deviceId) throws InterruptedException {	
		System.out.println("startServer");
		AppiumManager manager = AppiumManager.getInstance();
		String dpath = getOutputDir() + File.separator + deviceId + File.separator;
		File f=new File(dpath);
		if(!f.exists()) f.mkdir();
		String logCatPath = dpath + "LogCat.log";
		String appiumPath = dpath + "Appium.log";
		String exceptionLogsPath = dpath + "exceptionLog.log";
		manager.setupAppium(getOutputDir(),deviceId, logCatPath, appiumPath, exceptionLogsPath);
		String port = manager.checkDevicePort(deviceId);
		System.out.println(deviceId + " "  + "setup appium " + deviceId + " " + port);
		return port;
	}
	@Override
	public void run() {
		install();
		try{
		//open Appium
		String port=startServer(getDeviceID());
		//wait
		System.out.println(getDeviceID() + " "  + "Ready to test with AppCrawler , stop 10 second");
		Thread.sleep(10000);
		String AppCrawlerOutputdir = getOutputDir() + File.separator + getDeviceID() + File.separator;
		Runtime r=Runtime.getRuntime();	
        Process process;
        //get Logcat
		startCaptureLog(getDeviceID());
		//start test
        String cmd = OSUtil.getCmd();
        if(OSUtil.isWin()) {
        	process = r.exec(batPath+" " + getAppPath() + " " + port + " " + AppCrawlerOutputdir);
        	System.out.println(batPath+" " + getAppPath() + " " + port + " " + AppCrawlerOutputdir);
        }else {
        	process = r.exec(cmd+" "+batPath+" " + getAppPath() + " " + port + " " + AppCrawlerOutputdir + " " + toolPath);
        	System.out.println(cmd+" "+batPath+" " + getAppPath() + " " + port + " " + AppCrawlerOutputdir + " " + toolPath);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())); 
        String line;
        File f=new File(getOutputDir() + File.separator + getDeviceID() + File.separator + "AppCrawlerLog.log");
    	BufferedWriter bw=new BufferedWriter(new FileWriter(f));
        while ((line=bufferedReader.readLine()) != null) {
        	bw.write(line+"/n");
        	System.out.println(getDeviceID() + " "  + line);
		}
        bw.close();
		process.waitFor();
		System.out.println(getDeviceID() + " "  + " AppCrawler is done , ready to destroy");
		process.destroy();
		//close get logcat 
        endCaptureLog();
        //close appium
			//kill after all the appium done -> kill in Driver
        //AppiumManager.stopAppium(port);
		System.out.println(getDeviceID() + " "  + "finish appcrawler");
		}catch(Exception e){
			
		}
	}
	
}
