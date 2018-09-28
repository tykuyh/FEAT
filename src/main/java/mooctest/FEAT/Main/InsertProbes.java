package mooctest.FEAT.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import mooctest.FEAT.Util.OSUtil;


public class InsertProbes {
	
	String codedir;
	String appdir;
	String mainActivity;
	InsertProbes(){
	}
	InsertProbes(String codedir,String appdir,String mainActivity){
		this.codedir=codedir;
		this.appdir=appdir;
		this.mainActivity=mainActivity;
	}
	public void putJacocoGradle(String codedir,String appdir) throws Exception {
		File directory = new File("");
        String courseFile = directory.getCanonicalPath();
		Runtime r=Runtime.getRuntime();	
        if (OSUtil.isWin()) {
            r.exec("Commands\\win\\putGradle.bat " + courseFile + " " + appdir + " " + codedir);
            Thread.sleep(5000);
            System.out.println("pull success");
        } else { 
        }
	}
	public void modifyBuild(String filename) throws IOException {
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		StringBuffer buf=null;
		
		//read and modify
		br = new BufferedReader(new FileReader(filename));
		while ((line = br.readLine()) != null) {
			if(line.contains("testCoverageEnabled = true")) {
				br.close();
				return;
			}
		}
		br.close();
		/*
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
		br.close();
		
		//write
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
	}
	public void modifyLibrary(String filename) throws IOException {
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		StringBuffer buf=null;
		
		br = new BufferedReader(new FileReader(filename));
		while ((line = br.readLine()) != null) {
			if(line.contains("compile project(path:':jacocoLibrary')")) {
				br.close();
				return;
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(filename));
		buf=new StringBuffer();
		while ((line = br.readLine()) != null) {
			if(line.contains("dependencies")&&line.contains("{")){
				buf.append(line).append("\n");
				buf.append("\tcompile project(path:':jacocoLibrary')\n");
			}
			else buf.append(line).append("\n");
		}
		
		br.close();
		//write
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
	}
	public void modifySetting(String filename) throws IOException {
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		StringBuffer buf=null;
		
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
		
		br.close();
		//write
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
	}
	public void modifyMainActivity(String filename) throws IOException {
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		StringBuffer buf=null;
		br = new BufferedReader(new FileReader(filename));
		while ((line = br.readLine()) != null) {
			if(line.contains("JacocoHelper.generateEcFile(true);")) {
				br.close();
				return;
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(filename));
		buf=new StringBuffer();
		//read
		int flag=0,importflag=0;//onDestroy?
		while ((line = br.readLine()) != null) {
			if(importflag==0&&line.contains("import")){
				importflag=1;
				buf.append(line).append("\n");
				buf.append("import jacoco.JacocoHelper;\n");
			}
			if(line.contains("onDestroy()")&&line.contains("void")){
				flag=1;
				buf.append(line).append("\n");
				buf.append("JacocoHelper.generateEcFile(true);\n");
			}
			else buf.append(line).append("\n");
		}
		br.close();
		
		if(flag==0){
			importflag=0;
			br = new BufferedReader(new FileReader(filename));
			buf=new StringBuffer();
			while ((line = br.readLine()) != null) {
				if(importflag==0&&line.contains("import")){
					importflag=1;
					buf.append(line).append("\n");
					buf.append("import jacoco.JacocoHelper;\n");
				}
				if(line.contains("public")&&line.contains("class")&&line.contains("{")){
					buf.append(line).append("\n");
					buf.append("\t@Override\n\tprotected void onDestroy() {\n\t\tJacocoHelper.generateEcFile(true);\n\t}\n");
				}
				else buf.append(line).append("\n");
			}
			br.close();
		}
		//write
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
	}
	public void run() throws Exception{
		putJacocoGradle(codedir,appdir);
		modifyBuild(appdir+"\\build.gradle");
		modifyLibrary(appdir+"\\build.gradle");//library
		modifySetting(codedir+"\\settings.gradle");//setting
		modifyMainActivity(mainActivity);
	}
}
