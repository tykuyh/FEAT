package mooctest.FEAT.mdpAutoPull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import mooctest.FEAT.Util.OSUtil;

public class entrance {
	public void copyRootCode(String rootFolder){
		//Runtime r=Runtime.getRuntime();
		Process process = null;
		ProcessBuilder pb =null;
		String line=null;
		File f = new File(rootFolder+"-copy");
		if(!f.exists()) f.mkdir();
		try {
			String cmd = OSUtil.getCmd();
            if (OSUtil.isWin()) {
				pb = new ProcessBuilder("Commands\\win\\copyFilefold.bat\"",rootFolder,rootFolder+"-copy\\");
				pb.redirectErrorStream(true); 
            	//process = r.exec("bat\\copyFilefold.bat" + " " + rootFolder + " " + rootFolder + "-copy\\");
			} else {
				pb = new ProcessBuilder(cmd,"Commands/linux/copyFilefold.sh",rootFolder+"/*",rootFolder+"-copy" );
				pb.redirectErrorStream(true); 
                //process = r.exec(cmd + " " + "sh/copyFilefold.sh" + " " + rootFolder + "/*" + " " + rootFolder + "-copy" );
            }
			process=pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br.readLine())!= null){
					System.out.println(line);
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void copyFile(String a,String b){
		//Runtime r=Runtime.getRuntime();
		Process process = null;
		ProcessBuilder pb =null;
		String line=null;
		try {
			String cmd = OSUtil.getCmd();
            if (OSUtil.isWin()) {
				pb = new ProcessBuilder("Commands\\win\\copyFilefold.bat",a,b);
				pb.redirectErrorStream(true); 
    			//process = r.exec("bat\\copyFilefold.bat" + " " + a + " " + b);
			} else {
				pb = new ProcessBuilder(cmd,"Commands/linux/copyFilefold.sh",a,b);
				pb.redirectErrorStream(true); 
                //process = r.exec(cmd + " " + "sh/copyFilefold.sh" + " " + a + " " + b);
            }
			process=pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br.readLine())!= null){
					System.out.println(line);
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void createMutant(String rootFolder, String mdpFolder, String appPackage, String mutantFolder,String appFolder){
		//Runtime r=Runtime.getRuntime();
		Process process = null;
		ProcessBuilder pb =null;
		String line=null;
		File f = new File(mutantFolder);
		if(!f.exists()) f.mkdir();
		try {
			String cmd = OSUtil.getCmd();
            if (OSUtil.isWin()) {
				pb = new ProcessBuilder("Commands\\win\\createMutant.bat",mdpFolder,rootFolder,appFolder,appPackage,mutantFolder);
				pb.redirectErrorStream(true); 
    			//rocess = r.exec("bat\\createMutant.bat" + " " + mdpFolder + " " + rootFolder + " " + appFolder + " " + appPackage +" " + mutantFolder);
			} else {
                System.out.println("java -jar MDroidPlus-1.0.0.jar libs4ast/ "+rootFolder+appFolder+" "+appPackage+" "+mutantFolder+" . true");
				pb = new ProcessBuilder(cmd,"Commands/linux/createMutant.sh",mdpFolder,rootFolder,appFolder,appPackage,mutantFolder);
				pb.redirectErrorStream(true); 
                //process = r.exec(cmd + " " + "sh/createMutant.sh" + " " + mdpFolder + " " + rootFolder + " " + appFolder + " " + appPackage +" " + mutantFolder);
            }
			process=pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br.readLine())!= null){
					System.out.println(line);
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void compileApp(String codedir){
		//Runtime r=Runtime.getRuntime();
		Process process = null;
		ProcessBuilder pb =null;
		String line=null;
		try {
			String cmd = OSUtil.getCmd();
			if (OSUtil.isWin()) {
				pb = new ProcessBuilder("Commands\\win\\compileApp.bat",codedir);
				pb.redirectErrorStream(true); 
				//process = r.exec("bat\\compileApp.bat" + " " + codedir);
			} else {
				pb = new ProcessBuilder(cmd,"Commands/linux/compileApp.sh",codedir);
				pb.redirectErrorStream(true); 
                //process = r.exec(cmd + " " + "sh/compileApp.sh" + " " + codedir);
            }
			process=pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br.readLine())!= null){
					System.out.println(line);
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void changename(String apkpath,String apkdir,String newname){
		//Runtime r=Runtime.getRuntime();
		Process process = null;
		ProcessBuilder pb =null;
		String line=null;
		try {
			String cmd = OSUtil.getCmd();
			if (OSUtil.isWin()) {
				pb = new ProcessBuilder("Commands\\win\\changeName.bat",apkdir,apkpath,newname);
				pb.redirectErrorStream(true); 
				//process = r.exec("bat\\changeName.bat" + " " + path+"app-debug.apk" +" "+newname);
			} else {
				//System.out.println(cmd+" sh/changeName.sh "+path+" app-debug.apk "+newname);
				pb = new ProcessBuilder(cmd,"Commands/linux/changeName.sh",apkdir,apkpath,newname);
				pb.redirectErrorStream(true); 
				//process = r.exec(cmd+" "+"sh/changeName.sh" + " " + path+" app-debug.apk" +" "+newname);
            }
			process=pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br.readLine())!= null){
					System.out.println(line);
			}
			br.close();
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//not use now&&doesn't finish
	public void deleteApk(String path){
		//Runtime r=Runtime.getRuntime();
		Process process = null;
		ProcessBuilder pb =null;
		String line=null;
		try {
			String cmd = OSUtil.getCmd();
			if (OSUtil.isWin()) {
				pb = new ProcessBuilder("Commands\\win\\deleteApk.bat",path);
				pb.redirectErrorStream(true); 
			} else {
				pb = new ProcessBuilder(cmd,"Commands/linux/deleteApk.sh",path);
				pb.redirectErrorStream(true); 
            }
			pb.redirectErrorStream(true); 
			process=pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br.readLine())!= null){
					System.out.println(line);
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void createApk(String mutantFolder,String appPackage,String rootFolder,String appFolder,String outFolder,String apkPath) {
		int x=0;
		File f=new File(mutantFolder);
		File[] fl=f.listFiles();
		for(int i=0;i<fl.length;i++){
			if(fl[i].isDirectory()){
				String numbers[]=fl[i].getName().split("mutant");
				int num=Integer.parseInt(numbers[1]);
				if(num>x) x=num;
			}
		}
		System.out.println("create "+x+" mutants");
		File of=new File(outFolder);
		if(!of.exists()) {
			of.mkdirs();
		}
		for(int i=1;i<=x;i++){
			String a=mutantFolder+File.separator+appPackage+"-mutant"+i;
			String b=rootFolder+"-copy"+appFolder;
			//3.delete app source Code
			deleteApk(b);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//a copy to b
			copyFile(a,b);
			//4.compile App
			compileApp(rootFolder+"-copy");
			//5.move apk file
			/*
			String[] appname;
			if(OSUtil.isWin()) appname=appFolder.split("\\\\");
			else appname=appFolder.split("/");
			*/
			System.out.println(apkPath);
			String[] apkDirs;
			if(OSUtil.isWin()) apkDirs=apkPath.split("\\\\");
			else apkDirs=apkPath.split("/");
			int num=apkDirs[apkDirs.length-1].length();
			String apkDir=apkPath.substring(0, apkPath.length()-num);
			System.out.println(apkDir);
			
			changename(apkPath,apkDir, "mutant"+i+".apk");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			copyFile(apkDir+"mutant"+i+".apk", outFolder);
			deleteApk(apkDir+"mutant"+i+".apk");
		}
	}
}
