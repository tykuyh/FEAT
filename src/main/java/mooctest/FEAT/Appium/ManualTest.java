package mooctest.FEAT.Appium;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;

import mooctest.FEAT.Interface.TestCodeIF;
import mooctest.FEAT.Util.OSUtil;
import io.appium.java_client.AppiumDriver;

public class ManualTest extends Thread{
	private String deviceID;
	private String appPackage;
	private String appActivity;
	private String appPath;
	private String outPath;
	private Process logcatProcess;
	ManualTest(String deviceID,String appPackage,String appActivity,String appPath,String op) throws IOException{
		this.deviceID=deviceID;
		this.appPackage=appPackage;
		this.appActivity=appActivity;
		this.appPath=appPath;
		outPath=op+ File.separator;
		String resultPath=outPath + File.separator + deviceID + File.separator;
		File toolDir=new File(resultPath);
		if(!toolDir.exists()) toolDir.mkdirs();
		//logcat
		String toolLogCatPath=resultPath+"LogCat.log";
		File toolLogCat=new File(toolLogCatPath);
		//LOG
		if(toolLogCat.exists()) toolLogCat.delete();
		toolLogCat.createNewFile();
	}
	public void startCaptureLog(String deviceID){
		String logPath = outPath + File.separator + deviceID + File.separator + "LogCat.log";
		String command;
		String cmd = OSUtil.getCmd();
		if(OSUtil.isWin()) command = "Commands\\win\\logcat.bat " + deviceID + " " + logPath;
		else command=cmd+" "+"Commands/linux/logcat.sh "+deviceID + " " + logPath;
		System.out.println("command is: " + command);
		try {
			logcatProcess = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void endCaptureLog() {
		if (logcatProcess != null ) {
			//logcatProcess.destroyForcibly();
			logcatProcess = null;
		}
		System.out.println("end capture log");
	}
	public String startServer(String deviceId) throws InterruptedException {	
		
		System.out.println("startServer");
		AppiumManager manager = AppiumManager.getInstance();
		String dpath = outPath + deviceId + File.separator;
		String logCatPath = dpath + "LogCat.log";
		String appiumPath = dpath + "Appium.log";
		String exceptionLogsPath = dpath + "exceptionLog.log";
		manager.setupAppium(outPath ,deviceId, logCatPath, appiumPath, exceptionLogsPath);
		String port = manager.checkDevicePort(deviceId);
		System.out.println(deviceId + " "  + "setup appium " + deviceId + " " + port);
		return port;
	}
	public void install(){
		//<6.0
		String command = "adb -s " + deviceID + " install " + this.appPath;
		OSUtil.runCommand(command);
		//>=6.0
		command = "adb -s " + deviceID + " install -g " + this.appPath;
		OSUtil.runCommand(command);
	}
	public void run(){
		install();
		//Runtime r=Runtime.getRuntime();	
        //Process process = null;
        //mkdir
        String toolDirPath = outPath + getDeviceID() + File.separator;
		File toolDir=new File(toolDirPath);
		if(!toolDir.exists()) toolDir.mkdirs();
		//start logcatCapture
		startCaptureLog(getDeviceID());
        //start Appium
        String port = null;
		try {
			port = startServer(getDeviceID());
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
        System.out.println("wait 10s");
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println("startTest");
		/*
		String cmd = OSUtil.getCmd();
		process = r.exec("Commands\\win\\copyTestBat.bat "+appPath);
		Thread.sleep(5000);
		if(OSUtil.isWin()) {
		    process = r.exec("Commands\\win\\compileJava.bat "+appPath);
		    process.waitFor();
		    process = r.exec("Commands\\win\\executeJava.bat "+appPath);          
		} else {
			process = r.exec(cmd+" Commands/linux/compileJava.sh "+appPath);
			System.out.println(cmd+" Commands/linux/compileJava.sh "+appPath);
		    process.waitFor();
		    process = r.exec(cmd+" Commands/linux/executeJava.sh "+appPath); 
		    System.out.println(cmd+" Commands/linux/executeJava.sh "+appPath);
		}
		*/
        //run Appium scripts
        try {
			Class<?> c=Class.forName("mooctest.FEAT.AppiumTestCode.Main");
			TestCodeIF IF = (TestCodeIF)c.newInstance();
			IF.test(this.initAppiumTest(appPath,appPackage,appActivity,port));
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())); 
		String line;
		while ((line=bufferedReader.readLine()) != null) {
			System.out.println(line);
		}
		bufferedReader.close();
		*/
		//if(OSUtil.isWin()) process.waitFor();
		System.out.println(getDeviceID() + " "  + " test is done , ready to destroy");
		//System.out.println("close appium");
		//AppiumManager.stopAppium(port);
		endCaptureLog();
		//process.destroy();

	}
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}
	
	public void setOutputDir(String outputdir) {
		this.outPath = outputdir;
	}

	public String getOutputDir() {
		return outPath;
	}
	
	public void setAppPath(String apppath) {
		this.appPath = apppath;
	}

	public String getAppPath() {
		return appPath;
	}
	
	public AppiumDriver initAppiumTest(String appPath,String appPackage,String appActivity,String port) {
		
		AppiumDriver driver=null;
        File app = new File(appPath);
        
        
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("noSign", "true");
		capabilities.setCapability("noReset", "true");
        capabilities.setCapability("appPackage",appPackage);
        //capabilities.setCapability("appActivity", appActivity);//some app don't set the launchedActivity in the appa
		
        capabilities.setCapability("app", app.getAbsolutePath()); 
        

        capabilities.setCapability("unicodeKeyboard","true");

        capabilities.setCapability("resetKeyboard","true");

        try {
			driver = new AppiumDriver(new URL("http://127.0.0.1:"+port+"/wd/hub"), capabilities);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}   
        return driver;
	}
}
