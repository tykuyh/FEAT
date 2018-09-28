package mooctest.FEAT.Interface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import mooctest.FEAT.Util.OSUtil;


public abstract class AutomatorToolsTest extends Thread{
	private String toolName;
	private String deviceID;
	private String appPackage;
	private String appPath;
	private Process logcatProcess;
	private String outputdir;
	
	public AutomatorToolsTest(String toolName,String deviceID,String appPackage,String appPath,String outputdir) throws IOException{
		this.toolName=toolName;
		this.deviceID=deviceID;
		this.appPackage=appPackage;
		this.appPath=appPath;
		this.outputdir=outputdir;
		
		String toolDirPath=outputdir + File.separator + deviceID + File.separator;
		File toolDir=new File(toolDirPath);
		if(!toolDir.exists()) toolDir.mkdirs();
		//logcat
		String toolLogCatPath=toolDirPath+"LogCat.log";
		File toolLogCat=new File(toolLogCatPath);
		//LOG
		if(toolLogCat.exists()) toolLogCat.delete();
		toolLogCat.createNewFile();
	}
	//install APK
	public void install() {
		//get release
		String cmd="adb shell getprop ro.build.version.sdk";		
		Runtime r=Runtime.getRuntime();	
        Process process;
        try {
			process = r.exec(cmd);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())); 
	        int line=Integer.parseInt(bufferedReader.readLine());
			process.waitFor();
			process.destroy();
			if(line<=22) {
				cmd = "adb -s " + deviceID + " install " + this.appPath;
			}else {
				cmd = "adb -s " + deviceID + " install -g " + this.appPath;
			}
			OSUtil.runCommand(cmd);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	//copy log
	public void startCaptureLog(String deviceID){
		String logPath = outputdir + File.separator + deviceID + File.separator + "LogCat.log";
		String command;
		if(OSUtil.isWin()) {
			command = "Commands\\win\\logcat.bat " + deviceID + " " + logPath;
		}
		else {
			String cmd = OSUtil.getCmd();
			command =cmd + " Commands/linux/logcat.sh " + deviceID + " " + logPath;
		}
		System.out.println("command is " + command);
		try {
			logcatProcess = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//end log
	public void endCaptureLog() {
		if (logcatProcess != null ) {
			//logcatProcess.destroyForcibly();
			logcatProcess = null;
		}
		System.out.println("end capture log");
	}
	//run tools
	public abstract void run();
	
	public String getToolName() {
		return toolName;
	}

	public void getToolName(String name) {
		this.toolName = name;
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
		this.outputdir = outputdir;
	}

	public String getOutputDir() {
		return outputdir;
	}
	
	public void setAppPath(String apppath) {
		this.appPath = apppath;
	}

	public String getAppPath() {
		return appPath;
	}
}
 