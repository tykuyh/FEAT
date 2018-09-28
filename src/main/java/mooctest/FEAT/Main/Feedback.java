package mooctest.FEAT.Main;

import mooctest.FEAT.Util.OSUtil;

public class Feedback {
	//private String dir=Config.dir;
	String appdir;
	String deviceID;
	String outputdir;
	String sdcard="/sdcard";
	Feedback(String ad,String dID,String od){
		appdir=ad;
		deviceID=dID;
		outputdir=od;
	}
	public void getEC() throws InterruptedException{
		String s;
		s="adb -s "+deviceID+" pull "+sdcard+"/coverage.ec "+appdir;
		OSUtil.runCommand(s);
		s="adb -s "+deviceID+" pull "+sdcard+"/coverage.ec "+outputdir+"/"+deviceID;
		OSUtil.runCommand(s);
		System.out.println("pull success");
		String command;
		if (OSUtil.isWin()) {
			//command = "Commands\\win\\killADB.bat ";
			command = "taskkill /F /IM adb.exe";
		} else {
			command = OSUtil.getCmd() + " Commands/killADB.sh "+deviceID;
		}
		System.out.println(command);
		try {
			Runtime.getRuntime().exec(command);	
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	public void copyHTML() {
		String s;
		if (OSUtil.isWin()) {
			s="";
		} else {
			s="cp -R "+appdir+"report"+" "+outputdir+deviceID;
		}
		System.out.println(s);
		OSUtil.runCommand(s);
		
	}
}
