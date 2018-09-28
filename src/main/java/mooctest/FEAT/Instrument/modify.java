package mooctest.FEAT.Instrument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import mooctest.FEAT.Util.OSUtil;

public class modify {
	public static void putJacocoGradle(String codedir,String appdir,String packageName) throws IOException, InterruptedException  {
		//put the jacoco instrumentation files into the app project
		BufferedReader br = null;
		BufferedWriter bw = null;
		String line = null;
		String filename = null;
		StringBuffer buf = new StringBuffer();
		File directory = new File("");
        String courseFile = directory.getCanonicalPath();
        System.out.println(courseFile);
		Runtime r=Runtime.getRuntime();	
        if (OSUtil.isWin()) {
            r.exec("Commands\\win\\putGradle.bat " + courseFile + " " + appdir + " " + codedir);
            Thread.sleep(5000);
            System.out.println("pull success");
        } else {  
        	System.out.println("bash " + "Commands/linux/putGradle.sh " + courseFile + " " + appdir + " " + codedir);
        	r.exec("bash " + "Commands/linux/putGradle.sh " + courseFile + " " + appdir + " " + codedir);
            Thread.sleep(5000);
            System.out.println("pull success");
        }
        filename=codedir+File.separator+"jacocotest.java";
        br = new BufferedReader(new FileReader(filename));
        buf.append("package "+packageName+";");
        while ((line = br.readLine()) != null) {
        	buf.append(line).append("\n");
		}
		br.close();
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
		buf = new StringBuffer();
		
        filename=codedir+File.separator+"LogUtils.java";
        br = new BufferedReader(new FileReader(filename));
        buf.append("package "+packageName+";");
        while ((line = br.readLine()) != null) {
        	buf.append(line).append("\n");
		}
		br.close();
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
		buf = new StringBuffer();
	}
	public static void modifyFile(String type, String filename) throws IOException {
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		StringBuffer buf=null;
		if(type.equals("build")){
			//read and modify
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				if(line.contains("testCoverageEnabled = true")) {
					br.close();
					return;
				}
			}
			/*debug
			boolean findDebug=false;
			while ((line = br.readLine()) != null) {
				if(line.contains("debug")&&line.contains("{")){
					findDebug=true;
					buf.append(line).append("\n");
					buf.append();
				}
				else buf.append(line).append("\n");
			}
			*/
			br.close();
			br = new BufferedReader(new FileReader(filename));
			buf=new StringBuffer();
			buf.append("apply from: 'jacoco.gradle'\n");
			while ((line = br.readLine()) != null) {
				if(line.contains("buildTypes")&&line.contains("{")){
					buf.append(line).append("\n");
					buf.append("\tdebug {\n\t\ttestCoverageEnabled = true\n\t}\n");
				}
				else buf.append(line).append("\n");
			}
		}else if(type.equals("build2")){
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				if(line.contains("lintOptions")) {
					br.close();
					return;
				}
			}
			br.close();
			br = new BufferedReader(new FileReader(filename));
			buf=new StringBuffer();
			while ((line = br.readLine()) != null) {
				if(line.contains("android")&&line.contains("{")){
					buf.append(line).append("\n");
					buf.append("    lintOptions {\n" + 
							"        checkReleaseBuilds false\n" + 
							"        abortOnError false\n" + 
							"    }\n" + 
							"    allprojects {\n" + 
							"        gradle.projectsEvaluated {\n" + 
							"            tasks.withType(JavaCompile) {\n" + 
							"                options.compilerArgs << \"-Xlint:unchecked\" << \"-Xlint:deprecation\"\n" + 
							"            }\n" + 
							"        }\n" + 
							"    }\n");
				}
				else buf.append(line).append("\n");
			}
		}else if(type.equals("setting")){
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				if(line.contains("include ':jacocoLibrary'")) {
					br.close();
					return;
				}
			}
			br.close();
			br = new BufferedReader(new FileReader(filename));
			buf=new StringBuffer();
			buf.append("include ':jacocoLibrary'\n");
			while ((line = br.readLine()) != null) {
				buf.append(line).append("\n");
			}
		}else{
			return;
		}
		br.close();
		//write
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
	}
	public static void modifyFile(File dir,String pn) throws IOException {
		File[] files=dir.listFiles();
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		StringBuffer buf=null;
		for(int i=0;i<files.length;i++) {
			if(files[i].isDirectory()) {
				modifyFile(files[i],pn);
			}
			else if(files[i].getName().contains("Activity")&&!files[i].getName().startsWith("Activity")){
				br = new BufferedReader(new FileReader(files[i]));
				/*
				boolean od=false,os=false,op=false;
				//judege instrument or not
				while ((line = br.readLine()) != null) {
					if(line.contains("jacocotest")) return;
					if(line.contains("onDestroy()")) od=true;
					if(line.contains("onStop()")) od=true;
					if(line.contains("onPause()")) op=true;
				}
				br.close();
				//have onDestroy()?onStop()?onPause()?
				br = new BufferedReader(new FileReader(files[i]));
				buf=new StringBuffer();
				
				if(!(od==true&&os==true&&op==true)){
					while ((line = br.readLine()) != null) {
						if(line.contains("class")&&line.contains("public")&&line.contains("{")) {
							buf.append(line).append("\n");
							if(od==false) {
								buf.append("public void onDestroy(){\n" + 
										"        super.onDestroy();\n" + 
										"    }\n");
							}
							if(os==false) {
								buf.append("public void onStop(){\n" + 
										"        super.onStop();\n" + 
										"    }\n");
							}
							if(op==false) {
								buf.append("public void onPause(){\n" + 
										"        super.onPause();\n" + 
										"    }\n");
							}
						}
						else buf.append(line).append("\n");
					}
					bw = new BufferedWriter(new FileWriter(files[i]));
					System.out.println(buf);
					bw.write(buf.toString());
					bw.close();
				}
				*/
				//instrument
				br = new BufferedReader(new FileReader(files[i]));
				buf=new StringBuffer();
				//read
				int importflag=0;
				while ((line = br.readLine()) != null) {
					if(importflag==0&&line.contains("import")){
						importflag=1;
						buf.append(line).append("\n");
						buf.append("import "+pn+".jacocotest;\n");
					}
					else if(line.contains(" onCreate(")&&line.contains("{")&&(line.contains("public")||line.contains("protected"))&&line.contains("void")) {
						buf.append(line).append("\n");
						buf.append("jacocotest.init(\"\",true);\n");
					}
					else if(line.contains("{")&&(line.contains("public")||line.contains("protected"))&&line.contains("void")){
						buf.append(line).append("\n");
						buf.append("jacocotest.generateEcFile(false);\n");
					}
					else buf.append(line).append("\n");
				}
				//write
				bw = new BufferedWriter(new FileWriter(files[i]));
				bw.write(buf.toString());
				bw.close();
			}
		}
	}
	public static void main(String args[]) throws IOException, InterruptedException  {
		String codedir,appdir,dir;
		String build1,build2,maindir;
		String packageName;
		//appdir=the path of the 'app' document in your project
		//appCodedir=the path of the 'java' dir in 'app'
		//args:appDir,appCodeDir,packagename
		
		//Example:
		//bihudaily
		//codedir="/home/xyr/eclipse-workspace/BihuDaily-master/app/src/main/java/com/white/bihudaily/";
		//appdir="/home/xyr/eclipse-workspace/BihuDaily-master/app/";
		//setting="/home/xyr/eclipse-workspace/BihuDaily-master/settings.gradle";
		//maindir="/home/xyr/eclipse-workspace/BihuDaily-master/app/src/main/java/";
		//packageName="com.white.bihudaily";
		//
		//seeweather
		//codedir="/home/xyr/eclipse-workspace/sourceCode/SeeWeather/app/src/main/java/com/xiecc/seeWeather/";
		//appdir="/home/xyr/eclipse-workspace/sourceCode/SeeWeather/app/";
		//setting="/home/xyr/eclipse-workspace/sourceCode/SeeWeather/settings.gradle";
		//maindir="/home/xyr/eclipse-workspace/sourceCode/SeeWeather/app/src/main/java/";
		//packageName="com.xiecc.seeWeather";
		//
		//bilibili
		//dir="/home/xyr/eclipse-workspace/sourceCode/bilibili/";
		//codedir=dir+"app/src/main/java/com/hotbitmapgg/bilibili/";
		//appdir=dir+"app/";
		//setting=dir+"settings.gradle";
		//maindir=dir+"app/src/main/java/";
		//packageName="com.hotbitmapgg.bilibili";
		//
		//geeknews
		//dir="/home/xyr/eclipse-workspace/sourceCode/GeekNews/";
		//codedir=dir+"app/src/main/java/com/codeest/geeknews/";
		//appdir=dir+"app/";
		//setting=dir+"settings.gradle";
		//maindir=dir+"app/src/main/java/";
		//packageName="com.codeest.geeknews";
		//
		//jiandou
		//dir="/home/xyr/eclipse-workspace/sourceCode/JianDou/";
		//codedir=dir+"app/src/main/java/com/lhr/jiandou/";
		//appdir=dir+"app/";
		//setting=dir+"settings.gradle";
		//maindir=dir+"app/src/main/java/";
		//packageName="com.lhr.jiandou";
		//
		//leafpic
		//dir="/home/xyr/eclipse-workspace/sourceCode/LeafPic/";
		//codedir=dir+"app/src/main/java/org/horaapps/leafpic/";
		//appdir=dir+"app/";
		//setting=dir+"settings.gradle";
		//maindir=dir+"app/src/main/java/";
		//packageName="org.horaapps.leafpic";
		//
		//testerhome
		//dir="/home/xyr/eclipse-workspace/sourceCode/TesterHome/";
		//codedir=dir+"app/src/main/java/com/testerhome/nativeandroid/";
		//appdir=dir+"app/";
		//setting=dir+"settings.gradle";
		//maindir=dir+"app/src/main/java/";
		//packageName="com.testerhome.nativeandroid";
		
		dir=args[0];
		maindir=args[1];
		packageName=args[2];
		codedir=maindir+File.separator+packageName.replaceAll(".", File.separator)+File.separator;
		appdir=dir+"app/";
		
		putJacocoGradle(codedir,appdir,packageName);
		build1=appdir+"build.gradle";
		build2=appdir+"build.gradle";
		modifyFile("build",build1);
		modifyFile("build2",build2);
		File f=new File(maindir);
		modifyFile(f,packageName);
		
	}
}


