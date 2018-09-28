package mooctest.FEAT.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import mooctest.FEAT.Util.OSUtil;
import mooctest.FEAT.mdpAutoPull.autoExecution;
import mooctest.FEAT.mdpAutoPull.entrance;
import mooctest.FEAT.AppCrawler.AppCrawlerDriver;
import mooctest.FEAT.Appium.ManualTestDriver;
import mooctest.FEAT.Instrument.modify;
import mooctest.FEAT.Monkey.MonkeyDriver;
import mooctest.FEAT.Util.GetDeviceList;

public class Entrance {
	public static String[] getConst() throws IOException {
		String res[]=new String[10];
		File f = new File("ExecutionConfig.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		res[1]=br.readLine().split("=")[1]+File.separator;
		res[2]=br.readLine().split("=")[1]+File.separator;
		res[3]=br.readLine().split("=")[1]+File.separator;
		res[4]=br.readLine().split("=")[1]+File.separator;
		res[5]=br.readLine().split("=")[1];
		br.close();
		return res;
	}
	public static void delete(String path) {
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
	public static void deleteLastInfo(String projectDir) {
		String covec=projectDir+File.separator+"coverage.ec";
		String repo=projectDir+File.separator+"report";
		File file;
		file=new File(covec);
		if(file.exists()) {
			delete(covec);
		}
		file=new File(repo);
		if(file.exists()) {
			delete(repo);
		}		
	}
	public static void getReport(String appcodedir,String outputdir,long startTime) throws InterruptedException {
		List<String> deviceList=GetDeviceList.getDeviceList();
		for(int i=0;i<deviceList.size();i++) {
			String deviceId=deviceList.get(i);
			//feedback
			Feedback feedback=new Feedback(appcodedir,deviceId,outputdir);
			feedback.getEC();
			//generateDetailedFiles
			String logpath=outputdir+File.separator+deviceId+File.separator+"LogCat.log";
			File ec=new File(appcodedir+File.separator+"coverage.ec");
			File ec2=new File(outputdir+File.separator+deviceId+File.separator+"coverage.ec");
			if(ec.exists()&&ec2.exists()) {
				try {
					CulculatePoint.generate(appcodedir, File.separator + "coverage.ec");//generate HTML files
					Thread.sleep(10000);
					//get the HTMLFile
					feedback.copyHTML();
					//getPoint
					GetPoint gp = new GetPoint(outputdir+File.separator+deviceId,appcodedir,startTime);
					
					double coveragePoint = gp.getHTMLPoint();//calculate point from coverage
					double ExceptionPoint = gp.getExceptionPoint(logpath);//calculate point from exception
					File result = new File(outputdir + File.separator + deviceId + File.separator + "result.log");
					FileWriter fw = new FileWriter(result);
					String str = "";
					str += "deviceID=" + deviceId + "\n" + "HTMLPoint=" + coveragePoint + " ExceptionPoint=" + ExceptionPoint + "\n";
					System.out.println("deviceID=" + deviceId);
					System.out.println("HTMLPoint=" + coveragePoint + " ExceptionPoint=" + ExceptionPoint);
					fw.write(str);
					fw.close();

				}catch(Exception e){
					System.out.println("parse fail:"+e.getMessage());
				}
			}
		}
	}

	public static void entrance(String args[])throws Exception  {
		String s;
		//delete old coverage file
		List<String> deviceList=GetDeviceList.getDeviceList();
		for(int i=0;i<deviceList.size();i++) {
			String _s=deviceList.get(i);
			s = "adb -s "+_s+" shell rm " + "sdcard" + "/coverage.ec";
			OSUtil.runCommand(s);
		}
		long startTime = new Date().getTime();

		if(args[0].equals("T")){
			//automated tools
			String projectDir=args[1]+File.separator;
			String appPath=args[2];
			String outputDir=args[3]+File.separator;
			String toolName=args[4];
			
			outputDir+=toolName+File.separator;
			deleteLastInfo(projectDir);
			
			//test
			if(toolName.equals("Appium")) {
				//automated scripts
				deleteLastInfo(projectDir);
				//test
				ManualTestDriver mt=new ManualTestDriver(appPath,outputDir);
				mt.start();
			}
			else if(toolName.equals("Monkey")){
				String runFile=args[5];
				MonkeyDriver monkeyDriver = new MonkeyDriver(runFile,appPath,outputDir);
				monkeyDriver.prepareApk();
				try{
					monkeyDriver.start();
				}catch(Exception e){}
			}
			else if(toolName.equals("AppCrawler")){
				String runFile=args[5];
				String toolsPath=args[6]+File.separator;
				AppCrawlerDriver appcrawlerDriver = new AppCrawlerDriver(runFile,appPath,outputDir,toolsPath);
				appcrawlerDriver.prepareApk();
				try{
					appcrawlerDriver.start();
				}catch(Exception e){}
			}
			//feedback
			getReport(projectDir,outputDir,startTime);
			deleteLastInfo(projectDir);
			
		}else if(args[0].equals("MC")){
			
			String []Const=Entrance.getConst();
			String appDir=Const[1];
			String SourceCodeDir=Const[2];
			String MainCodeDir=SourceCodeDir.substring(0, SourceCodeDir.lastIndexOf("/"));
			String apkCreatePath=Const[5];
			
			//mutation
			String projectDir = args[1];
			File directory = new File("");//参数为空 
			String courseFile = directory.getCanonicalPath()+File.separator; 
			
			String mdpDir = courseFile+"MDroidPlus"+File.separator;
			String appPackage = args[2];
			String outputDir = args[3]+File.separator+"Mutant";
			deleteLastInfo(projectDir);
			
			String mutantDir = outputDir+File.separator+"mutationCode";
			String apkOutDir = outputDir+File.separator+"mutationApk";
			File f=new File(mutantDir);
			if(!f.exists()) f.mkdirs();
			f=new File(apkOutDir);
			if(!f.exists()) f.mkdirs();
			
			mooctest.FEAT.mdpAutoPull.entrance e = new entrance();
			
			
			//1.copy RootCode
			e.copyRootCode(projectDir);
			//2.create mutant
			e.createMutant(projectDir, mdpDir, appPackage, mutantDir, MainCodeDir);
			System.out.println("mutant create success");
			//3.create mutant Apk
				//1.apk->codedir,rename apk
				//2.execute codedir
				//3.pull log+appiumlog
				//4.changname
				//5.loop
			e.createApk(mutantDir, appPackage, projectDir, SourceCodeDir,apkOutDir,projectDir+"-copy"+File.separator+apkCreatePath);	
			System.out.println("apk create success");
			e.deleteApk(projectDir+"-copy");
		}else if(args[0].equals("ME")) {
			String projectDir=args[1];
			String outputDir=args[2];
			String apkOutDir = outputDir+File.separator+"Mutant"+File.separator+"mutationApk";
			String toolName=args[3];
			String resultDir = outputDir+File.separator+toolName+File.separator+"mutationResult";
			deleteLastInfo(projectDir);
			File f=new File(apkOutDir);
			File[] fs=f.listFiles();
			int length=fs.length;
			
			
			String []args_tmp=new String[10];
			args_tmp[0]="T";
			args_tmp[1]=projectDir;
			args_tmp[4]=toolName;
			if(toolName.equals("Appium")) {
			}
			else if(toolName.equals("Monkey")){
				args_tmp[5]=args[4];
			}
			else if(toolName.equals("AppCrawler")){
				args_tmp[5]=args[4];
				args_tmp[6]=args[5]+File.separator;
			}
			
			for(int i=1;i<=length;i++) {
				deleteLastInfo(projectDir);
				//for each mutation apk
				//auto excution and create log
				args_tmp[2]=apkOutDir+File.separator+"mutant"+i+".apk";
				args_tmp[3]=resultDir+File.separator+i+File.separator;
				Entrance.entrance(args_tmp);
				deleteLastInfo(projectDir);
			}
			
			if(toolName.equals("Appium")) {
				ManualTestDriver mt=new ManualTestDriver(projectDir,outputDir);
				mt.getMutationRatio();
				//autoExecution.collectMutationLog(resultDir,deviceList);
			}
			else if(toolName.equals("Monkey")){
				MonkeyDriver monkeyDriver = new MonkeyDriver("",projectDir,outputDir);
				monkeyDriver.getMutationRatio();
			}
			else if(toolName.equals("AppCrawler")){
				AppCrawlerDriver appcrawlerDriver = new AppCrawlerDriver("",projectDir,outputDir,"");
				appcrawlerDriver.getMutationRatio();
			}
		}else if(args[0].equals("I")) {

			String []Const=Entrance.getConst();
			String appDir=Const[1];
			String sourceCodeDir=Const[2];
			
			//instrumentation
			String codedir,appdir,projectDir;
			String build;
			String packageName;
			
			projectDir=args[1];
			packageName=args[2];
			codedir=projectDir+File.separator+sourceCodeDir+File.separator+packageName.replaceAll(".", File.separator)+File.separator;
			appdir=projectDir+File.separator+appDir;
			
			deleteLastInfo(projectDir);
			
			modify.putJacocoGradle(codedir,appdir,packageName);
			build=appdir+"build.gradle";
			modify.modifyFile("build",build);
			modify.modifyFile("build2",build);
			File f=new File(projectDir+File.separator+sourceCodeDir);
			modify.modifyFile(f,packageName);
		}
		else if(args[0].equals("S")) {
			String outputdir=args[1];
			String toolName=args[2];
			outputdir+=File.separator+toolName;
			GetPoint gp=new GetPoint("","",0);
			int deviceNum=deviceList.size();
			for(int i=0;i<deviceNum;i++) {
				File f = new File(outputdir+File.separator+deviceList.get(i)+File.separator+"result.log");
				if(f.exists()) {
					BufferedReader br=new BufferedReader(new FileReader(f));
					br.readLine();
					String line=br.readLine();
					gp.coveragePoint+=Double.parseDouble(line.split(" ")[0].split("=")[1]);
					gp.exceptionPoint+=Double.parseDouble(line.split(" ")[1].split("=")[1]);
					br.close();
				}
				f = new File(outputdir+File.separator+"mutationResult"+File.separator+"mutationResult.txt");
				BufferedReader br=new BufferedReader(new FileReader(f));
				String line;
				while(!((line=br.readLine())==null)) {
					if(line.contains(deviceList.get(i))) {
						br.readLine();
						line=br.readLine();
						gp.mutationPoint+=Double.parseDouble(line.split(":")[1]);
					}
				}
				br.close();
			}
			gp.coveragePoint/=deviceNum;
			gp.exceptionPoint/=deviceNum;
			gp.mutationPoint/=deviceNum;
			
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outputdir+File.separator+"finalResult.txt")));
			double finalScore=gp.getLastPoint();
			bw.write("toolName= "+toolName+"\n");
			bw.write("coverageRatio= "+gp.coverageRate+"\n");
			bw.write("coverageScore= "+gp.coveragePoint+"\n");
			bw.write("exceptionRatio= "+gp.exceptionRate+"\n");
			bw.write("exceptionScore= "+gp.exceptionPoint+"\n");
			bw.write("mutationRatio= "+gp.mutationRate+"\n");
			bw.write("mutationScore= "+gp.mutationPoint+"\n");
			bw.write("finalScore= "+finalScore+"\n");
			bw.flush();
			bw.close();
			System.out.println("finalScore= "+finalScore);
			
		}else {/*do nothing~*/}
		
	}
	public static void main(String[] args) throws Exception {
		//Example:
		//win
		//Hand/Manual
		//args[0]="H";
		//args[1]="D:\\testCode";
		//args[2]="D:\\BihuDaily";
		//args[3]="Appium";
		//Computer/Automation tools
		//Monkey
		//args[0]="C";
		//args[1]="Monkey";
		//args[2]="Commands\\win\\monkey.bat";
		//args[3]="apk\\bihu.apk";
		//args[4]="Monkey";
		//args[5]="pvctool\\BihuDaily";
		//AppCrawler
		//args[0]="C";
		//args[1]="AppCrawler";
		//args[2]="Commands\\win\\appcrawler.bat";
		//args[3]="apk\\bihu.apk";
		//args[4]="AppCrawler";
		//args[5]="pvctool\\BihuDaily";

		//——————————————————important————————————————————
		//You should write the argument in PVC/AppdirConfig.txt to tell us the app source code structure
		//Manual code should be put in PVC/ManualTestCode/Main.java
		//PVC/PointConfig to change the rate of the testing point
		//——————————————————important————————————————————

        //String evn="/home/ise/env/";
		//linux
		//H <apk path> <app source code path> <output dir>
//		args[0]="H";
//		args[1]=evn+"apk-debug/BihuDaily.apk";
//		args[2]=evn+"sourceCode/BihuDaily";
//		args[3]=evn+"outputs/BihuDaily/Manual/12449";


		//C <tool name> <execution file> <apk path> <output dir> <app source code path> [tools folder]
        //args[0]="C";
        //Monkey
        //args[1]="Monkey";
        //args[2]="Commands/linux/monkey.sh";
//        args[3]=evn+"apk-debug/BihuDaily.apk";
//        args[4]=evn+"outputs/BihuDaily/Monkey";
//        args[5]=evn+"sourceCode/BihuDaily";

		
		//Appcrawler
		//args[1]="AppCrawler";
		//args[2]="Commands/linux/appcrawler.sh";
        //args[3]=evn+"apk-debug/BihuDaily.apk";
        //args[4]=evn+"outputs/BihuDaily/AppCrawler";
        //args[5]=evn+"sourceCode/BihuDaily";
		//args[6]=evn+"automatorTools/";
		
	    //Mutation create
//		args=new String[10];
//	    args[0]="MC";
//	    args[1]="/home/xyr/eclipse-workspace/sourceCode/memetastic";
//	    args[2]="/app/src/main/";
//	    args[3]="/home/xyr/eclipse-workspace/MDroidPlus";
//	    args[4]="/home/xyr/eclipse-workspace/mutantCode";
//	    args[5]="io.github.gsantner.memetastic";
//	    args[6]="/home/xyr/eclipse-workspace/mutantApk";
//		args[7]="/home/xyr/eclipse-workspace/sourceCode/memetastic-copy/app/build/outputs/apk/flavorDefault/debug/app-flavorDefault-debug.apk";
	    //Mutation execute
//	    args=new String[10];
//	    args[0]="ME";
//	    args[1]="mutantApkDir";
//		args[2]="appCodeDir";
//		args[3]="outputDir";
	    
	    
	    
		//Instrumentation
		//appdir=the path of the 'app' document in your project
		//appCodedir=the path of the 'java' dir in 'app'
		//args:appDir,appCodeDir,packagename
			
		//Example:
		//bihudaily
		//appdir="/home/xyr/eclipse-workspace/BihuDaily-master/app/";
		//appCodedir="/home/xyr/eclipse-workspace/BihuDaily-master/app/src/main/java/";
		//packageName="com.white.bihudaily";
		entrance(args);
	}

}
