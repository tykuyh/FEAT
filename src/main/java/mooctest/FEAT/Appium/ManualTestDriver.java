package mooctest.FEAT.Appium;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sinaapp.msdxblog.apkUtil.entity.ApkInfo;
import com.sinaapp.msdxblog.apkUtil.utils.ApkUtil;

import mooctest.FEAT.Util.GetDeviceList;
import mooctest.FEAT.Util.OSUtil;

public class ManualTestDriver {
	private String appPath;
	private String appPackage;
	private String appActivity;
	private String outputdir;
	private List<String> deviceList;
	private List<ManualTest> taskList;
	public ManualTestDriver(String appPath,String outputdir) {
		this.appPath=appPath;
		this.outputdir=outputdir;
		this.taskList = new ArrayList<ManualTest>();
		this.deviceList = GetDeviceList.getDeviceList();
	}
	
	public void start() throws InterruptedException, IOException {
		System.out.println("Manual Testing start");
		dispatchTest();
		//openJacoco();//6.0
		observeTest();
		//In Linux, we kill all the appium after the execution
		AppiumManager.stopAppium("");
		System.out.println("close appium");
	}
	
	public ManualTest startTestTask(String device) throws IOException {
		ManualTest ManualTest;
		prepareApk();
		ManualTest = new ManualTest(device ,getAppPackage(), getAppActivity(),appPath, outputdir);
		ManualTest.start();
		return ManualTest;
	}
	
	public void dispatchTest() throws IOException{
		for (int i = 0 ; i < getDeviceList().size() ; i++) {
			String deviceID = getDeviceList().get(i);
			ManualTest task = startTestTask(deviceID);
			taskList.add(task);
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
		appActivity=apkInfo.getLaunchableActivity();
	}
	public void observeTest(){
		int time = 1000 * 60 * 180 ; 	
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
	public boolean getEventsInjected(File log) throws IOException {
		if(!log.exists()) return false;
		else return true;
	}
	public double getMutationRatio() {
		try {
			File f=new File(this.getOutputDir()+File.separator+"Appium"+File.separator+"mutationResult");
			File mutantResultFile=new File(this.getOutputDir()+File.separator+"Appium"+File.separator+"mutationResult"+File.separator+"mutationResult.txt");
			BufferedWriter bw=new BufferedWriter(new FileWriter(mutantResultFile));
			double score=0;
			File []mutantResults=f.listFiles();
			int mutationNumber=mutantResults.length;
			bw.write("mutantNumber: "+mutationNumber+"\n");
			bw.flush();
			List<String> deviceList=GetDeviceList.getDeviceList();
			for(int i=0;i<deviceList.size();i++) {
				int kill=0;
				boolean originEvents;
				File originalMonkeylog=new File(this.getOutputDir()+File.separator+"Appium"+File.separator+deviceList.get(i)+File.separator+"exceptionLog.log");
				originEvents=getEventsInjected(originalMonkeylog);
				if(originEvents==true) {
					bw.write("UDID: "+deviceList.get(i)+"\n");
					bw.write("killed: "+"0"+"\n");
					bw.write("score: "+"0"+"\n");
					bw.flush();
				}else {
					for(int j=1;j<=mutationNumber;j++) {
						File MonkeyLog=new File(this.getOutputDir()+File.separator+"Appium"+File.separator+"mutationResult"+File.separator+j+File.separator+"Appium"+File.separator+deviceList.get(i)+File.separator+"exceptionLog.log");
						boolean mutationEvents=getEventsInjected(MonkeyLog);
						if(mutationEvents==false) kill++;
					}
					bw.write("UIID: "+deviceList.get(i)+"\n");
					bw.write("killed: "+kill+"\n");
					bw.write("score: "+(double)kill/(double)mutationNumber+"\n");
					score+=(double)kill/(double)mutationNumber;
					bw.flush();
				}
			}
			bw.close();
			return score/(double)deviceList.size();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public String getAppPath() {
		return appPath;
	}
	public String getAppActivity() {
		return appActivity;
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
