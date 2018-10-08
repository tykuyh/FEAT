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
	//TODO auto acquire these argument in future
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
	//delete a file
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
	//delete the old data
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
	//analysis data to report after the exection
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
	//Main
	public static void entrance(String args[])throws Exception  {
		String s;
		//delete old coverage file in devices
		List<String> deviceList=GetDeviceList.getDeviceList();
		for(int i=0;i<deviceList.size();i++) {
			String _s=deviceList.get(i);
			s = "adb -s "+_s+" shell rm " + "sdcard" + "/coverage.ec";
			OSUtil.runCommand(s);
		}
		//get start time
		long startTime = new Date().getTime();
		//get argument
		if(args[0].equals("T")){
			//automated tools
			String projectDir=args[1]+File.separator;
			String appPath=args[2];
			String outputDir=args[3]+File.separator;
			String toolName=args[4];
			String apkOutDir = outputDir+File.separator+"Mutant"+File.separator+"mutationApk";
			String resultDir = outputDir+File.separator+toolName+File.separator+"mutationResult";
			//find mutation
			File f = new File("PointConfig.txt");
			BufferedReader br = new BufferedReader(new FileReader(f));
			Double.parseDouble(br.readLine().split("=")[1]);
			for(int i=0;i<6;i++){
				Double.parseDouble(br.readLine().split("=")[1]);
			}
			Double.parseDouble(br.readLine().split("=")[1]);
			double mutationRate=Double.parseDouble(br.readLine().split("=")[1]);
			br.close();
			boolean mutationFlag=false;
			if(mutationRate!=0) mutationFlag=true;
			
			
			//search is the mutation apk exist or not
			if(mutationFlag==true) {
				f = new File(outputDir+File.separator+"Mutant"+File.separator+"mutationApk");
				if(f.exists()) {
					File mutantApk[]=f.listFiles();
					if(mutantApk.length==0) {
						mutationFlag=false;
						System.out.println("No mutation apk exists, does not execute mutation testing this time.");
					}
				}else {
					mutationFlag=false;
					System.out.println("No mutation apk exists, does not execute mutation testing this time.");
				}
			}
			
			
			//delete old data
			outputDir+=toolName+File.separator;
			deleteLastInfo(projectDir);
			//mutation preparation
			String []args_tmp=new String[10];
			int length = 0;
			if(mutationFlag==true) {
				f=new File(apkOutDir);
				File[] fs=f.listFiles();
				length=fs.length;
				args_tmp[0]="T";
				args_tmp[1]=projectDir;
				args_tmp[4]=toolName;
			}	
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
				args_tmp[5]=args[4];
				MonkeyDriver monkeyDriver = new MonkeyDriver(runFile,appPath,outputDir);
				monkeyDriver.prepareApk();
				try{
					monkeyDriver.start();
				}catch(Exception e){}
			}
			else if(toolName.equals("AppCrawler")){
				String runFile=args[5];
				String toolsPath=args[6]+File.separator;
				args_tmp[5]=args[4];
				args_tmp[6]=args[5]+File.separator;
				AppCrawlerDriver appcrawlerDriver = new AppCrawlerDriver(runFile,appPath,outputDir,toolsPath);
				appcrawlerDriver.prepareApk();
				try{
					appcrawlerDriver.start();
				}catch(Exception e){}
			}
			//feedback
			getReport(projectDir,outputDir,startTime);
			deleteLastInfo(projectDir);
			
			
			
			
			//mutation testing
			if(mutationFlag==true) {		
				for(int i=1;i<=length;i++) {
					deleteLastInfo(projectDir);
					//for each mutation apk
					//auto excution and create log
					args_tmp[2]=apkOutDir+File.separator+"mutant"+i+".apk";
					args_tmp[3]=resultDir+File.separator+i+File.separator;
					try {
						Entrance.entrance(args_tmp);
					}catch(Exception e) {
						System.out.println("mutation"+i+" killed");
					}
					deleteLastInfo(projectDir);
				}
				autoExecution.getMutationRatio(outputDir,toolName);
			}
			
			
			
			
			
			//Score
			GetPoint gp=new GetPoint("","",0);
			int deviceNum=deviceList.size();
			for(int i=0;i<deviceNum;i++) {
				f = new File(outputDir+File.separator+deviceList.get(i)+File.separator+"result.log");
				if(f.exists()) {
					br=new BufferedReader(new FileReader(f));
					br.readLine();
					String line=br.readLine();
					gp.coveragePoint+=Double.parseDouble(line.split(" ")[0].split("=")[1]);
					gp.exceptionPoint+=Double.parseDouble(line.split(" ")[1].split("=")[1]);
					br.close();
				}
				f = new File(outputDir+File.separator+"mutationResult"+File.separator+"mutationResult.txt");
				br=new BufferedReader(new FileReader(f));
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
			
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outputDir+File.separator+"finalResult.txt")));
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
			
			
		}else if(args[0].equals("MC")){
			
			String []Const=Entrance.getConst();
			String SourceCodeDir=Const[2];
			String MainCodeDir=SourceCodeDir.substring(0, SourceCodeDir.lastIndexOf("/"));
			String apkCreatePath=Const[5];
			
			//mutation
			String projectDir = args[1];
			File directory = new File("");
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
			codedir=projectDir+File.separator+sourceCodeDir+File.separator+packageName.replaceAll("\\.", File.separator)+File.separator;
			appdir=projectDir+File.separator+appDir;
			
			deleteLastInfo(projectDir);
			
			modify.putJacocoGradle(codedir,appdir,packageName);
			build=appdir+"build.gradle";
			modify.modifyFile("build",build);
			modify.modifyFile("build2",build);
			File f=new File(projectDir+File.separator+sourceCodeDir);
			modify.modifyFile(f,packageName);
		}else {/*do nothing~*/}
		
	}
	public static void main(String[] args) throws Exception {
		entrance(args);
	}

}
