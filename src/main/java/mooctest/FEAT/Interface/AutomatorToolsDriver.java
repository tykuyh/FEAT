package mooctest.FEAT.Interface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sinaapp.msdxblog.apkUtil.entity.ApkInfo;
import com.sinaapp.msdxblog.apkUtil.utils.ApkUtil;

import mooctest.FEAT.Util.GetDeviceList;
import mooctest.FEAT.Util.OSUtil;

public abstract class AutomatorToolsDriver {
	private String appPath;
	private String appPackage;
	private String outputdir;
	private List<String> deviceList;
	public List<AutomatorToolsTest> taskList;
	
	
	
	
	public AutomatorToolsDriver(String appPath,String outputdir) {
		this.taskList = new ArrayList<AutomatorToolsTest>();
		this.deviceList = GetDeviceList.getDeviceList();
		this.appPath=appPath;
		this.outputdir=outputdir;
	}
	
	public void start() throws InterruptedException, IOException {
		System.out.println("Driver start");
		dispatchTest();
		observeTest();
	}
	
	public abstract AutomatorToolsTest startTestTask(String deviceID) throws IOException;
	public abstract double getMutationRatio() ;
	public void dispatchTest() throws IOException{
		for (int i = 0 ; i < getDeviceList().size() ; i++) {
			String deviceID = getDeviceList().get(i);
			taskList.add(startTestTask(deviceID));
		}
	}
	
	public void prepareApk(){
		ApkInfo apkInfo = null;
		try {
			 apkInfo = new ApkUtil().getApkInfo(appPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		appPackage=apkInfo.getPackageName();
	}
	
	/*
	//To get the write and read permission automately.Used in the phone which higher than 6.0
	//abort
	public void openJacoco() throws InterruptedException{
		//open jacoco
		System.out.println("open jacoco");
		String s="";
		//s="adb shell rm "+sdcard+"/coverage.ec";//delete
		OSUtil.runCommand(s);
		s="cd "+dir;
		OSUtil.runCommand(s);
		s="adb shell am instrument -w -r   -e debug false -e class "
				+ "location.autojacoco.ApplicationTest "
				+ appPackage
				+ ".test/android.support.test.runner.AndroidJUnitRunner";
		OSUtil.runCommand(s);
	}
	*/
	
	public void observeTest(){
		int time = 1000 * 60 * 60 * 3;
		int checkBreak = 1000 * 5; 
		for(int n = 0; n < time / checkBreak ; n++) {
			System.out.println("observeTest " + n);
			for(int i = 0; i < taskList.size(); i++) {
				
				System.out.println("find uptest " + n);
				if(!taskList.get(i).isAlive()) { 
					System.err.println("find thread is not alive ");
					//AutomatorToolsTest task = taskList.get(i);
					String deviceId = taskList.get(i).getDeviceID();
					String msg = uninstall(deviceId);
					if(msg.equals("Success")) System.out.println("uninstall successful");
					else System.out.println("uninstall failure");
					taskList.remove(i);
					System.out.println("find thread is not alive " + deviceId);
				}else{
					System.out.println("find thread " + taskList.get(i).getDeviceID() +" is alive");
				}
			}

			if(taskList.isEmpty()) {
				break;
			}
			try {
				Thread.sleep(checkBreak); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/* 
		//close adb
		String command;
		if (OSUtil.isWin()) {
			//command = "Commands\\win\\killADB.bat ";
			command = "taskkill /F /IM adb.exe";
		} else {
			command = OSUtil.getCmd() + " Commands/killADB.sh ";
		}
		System.out.println(command);
		
		try {
			//Runtime.getRuntime().exec(command);	
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		*/
		for(int i = 0; i < taskList.size(); i++){
			//AutomatorToolsTest task = taskList.get(i);
			String msg = uninstall(taskList.get(i).getDeviceID());
			if(msg.equals("Success")) System.out.println("uninstall successful");	
			else System.out.println("uninstall failure");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		System.out.println("All devices are finished");
		return;
	}
	
	
	private String uninstall(String udid) {
		String command = "adb -s " + udid + " uninstall " + this.appPackage;
		String msg = OSUtil.runCommand(command);
		if (msg != null && msg.contains("Success")) {
			return "Success";
		} else {
			return "failure";
		}
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setOutputDir(String outputdir) {
		this.outputdir = outputdir;
	}

	public String getOutputDir() {
		return outputdir;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}
	
	public List<String> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<String> deviceList) {
		this.deviceList = deviceList;
	}
	
	
}
