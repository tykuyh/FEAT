package mooctest.FEAT.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import mooctest.FEAT.Util.OSUtil;
public class CulculatePoint {
	public static void generate(String codedir,String ec) throws IOException{
		
		String []Const=Entrance.getConst();
		String sourcedir=Const[2];
		String classesdir=Const[3];
		String reportdir=Const[4];
		
		File f2=new File(reportdir);
		if(!f2.exists()) f2.mkdirs();
		ReportGenerator generator = new ReportGenerator(ec, classesdir, sourcedir, reportdir, codedir);
		generator.create();
	}
	public static double getHTMLPoint(String codedir,double rate[]) throws IOException{
		String index=codedir+"\\report\\coveragereport\\";
		String filename=index;
		File input = new File(filename+"index.html"); 
		Document doc = Jsoup.parse(input,"UTF-8"); 
		Elements links = doc.getElementsByTag("tfoot"); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(codedir+"\\report\\"+"HTMLPoint.txt"));
		String text = null;
		String[] gets=new String[20];
		for (Element link : links) { 
			text=link.text();
		}
		String[] s=text.split(" ");
		//miss and all
		gets[0]=s[5];gets[1]=s[7];//branch
		gets[2]=s[9];gets[3]=s[10];//cxty
		gets[4]=s[11];gets[5]=s[12];//lines
		gets[6]=s[13];gets[7]=s[14];//methods
		gets[8]=s[15];gets[9]=s[16];//classes
		gets[10]=s[1];gets[11]=s[3];//instructions
		for(int i=0;i<12;i++){
			gets[i]=gets[i].replace(",", "");
		}
		double  branch=1-1.0*Integer.parseInt(gets[0])/Integer.parseInt(gets[1]),
				cxty=1-1.0*Integer.parseInt(gets[2])/Integer.parseInt(gets[3]),
				lines=1-1.0*Integer.parseInt(gets[4])/Integer.parseInt(gets[5]),
				methods=1-1.0*Integer.parseInt(gets[6])/Integer.parseInt(gets[7]),
				classes=1-1.0*Integer.parseInt(gets[8])/Integer.parseInt(gets[9]),
				instructions=1-1.0*Integer.parseInt(gets[10])/Integer.parseInt(gets[11]);
		writer.write(instructions+" "+branch+" "+cxty+" "+lines+" "+methods+" "+classes+"\n"); 
		writer.flush();
		writer.close();
		double point=instructions*rate[0]+branch*rate[1]+cxty*rate[2]+lines*rate[3]+methods*rate[4]+classes*rate[5];
		return point;
	}
	public static void readFileByLines(String filename,String filename2) {
        File file = new File(filename);
        File file2 = new File(filename2);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(file2));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	if(tempString.contains("Exception:")){
            		int n=tempString.indexOf("Exception:");
            		int j=0;
            		for(j=n;j>=0;j--){
            			if(tempString.charAt(j)==' '||tempString.charAt(j)=='"'){
            				break;
            			}
            		}
            		writer.write(tempString.substring(j<=0?0:j+1, n+9)+"\n");
            		writer.flush();
            	}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
	public static double sortException(String filename2,String filename3){
		String[] ex=new String[1000];
		int count=0;
		int[] num=new int[1000];
		File file = new File(filename2);
        File file2 = new File(filename3);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(file2));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	int flag=0;
            	for(int i=0;i<count;i++){
            		if(tempString.equals(ex[i])){
            			num[i]++;flag=1;
            			break;
            		}
            	}
            	if(flag==0){
            		ex[count]=tempString;
            		num[count++]++;
            	}
            }
            reader.close();
            for(int i=0;i<count;i++){
            	//System.out.println(i);
            	writer.write(ex[i]+"\t"+num[i]+"\n");
            	writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    writer.close();
                } catch (IOException e1) {
                }
            }
        }
		return count-1;
	}
	public static double getExceptionPoint(String codedir,String log){
		String filename=log;
		String filename2=codedir+"\\report\\new.txt";
		String filename3=codedir+"\\report\\new2.txt";
		readFileByLines(filename,filename2);
		return sortException(filename2,filename3);
	}
	public static void putRunFile(String type, String courseFile,String name ,String outdir) throws Exception {
		Runtime r=Runtime.getRuntime();	
        try {
            if (OSUtil.isWin()) {
                r.exec("Commands\\win\\"+type+"PullFile.bat " + courseFile + " " + name + " " + outdir + " " + outdir);
                Thread.sleep(10000);
                System.out.println("pull file success");
            } else {  
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
	}
	public static void putJacocoGradle(String codedir,String appdir) throws Exception {
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
		}else if(type.equals("build2")){
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				if(line.contains("compile project(path:':jacocoLibrary')")) {
					br.close();return;
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
		}else if(type.equals("setting")){
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				if(line.contains("include ':jacocoLibrary'")) {br.close();
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
		}else if(type.equals("main")){
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
		}else{
			return;
		}
		br.close();
		//write
		bw = new BufferedWriter(new FileWriter(filename));
		bw.write(buf.toString());
		bw.close();
	}

	public static void main(final String[] args) throws Exception {
		//test
		String appcodedir="/home/xyr/eclipse-workspace/sourceCode/BihuDaily";
		CulculatePoint.generate(appcodedir, File.separator + "coverage.ec");//create HTML

		/*
		//GetPoint
		if(args[0].equals("GetPoint")){
			String codedir=args[1];
			String ec=args[2];
			String log=args[3];
			double[] rate=new double[6];
			for(int i=0;i<6;i++){
				rate[i]=1.0/6;
			}
			double rateY=0.5;
			int allException=30;
			generate(codedir,ec);
			double HTMLPoint=getHTMLPoint(codedir,rate);
			double ExceptionPoint=getExceptionPoint(codedir,log);
			double lastPoint=HTMLPoint*rateY+(ExceptionPoint/allException)*(1-rateY);
			System.out.println("HTMLPoint="+HTMLPoint+" ExceptionPoint="+ExceptionPoint);
			System.out.println("The last Point is "+lastPoint);
		}
		
		else if(args[0].equals("Auto")){
			String type=args[1];//Random/Linear
			String apkdir=args[2];
			String outdir=args[3];
			File directory = new File("");
	        String courseFile = directory.getCanonicalPath();
			String name="emulator-5554";
			
			if(type.equals("Random")){
				//Monkey
				int count=Integer.parseInt(args[4]);
				int throttle=Integer.parseInt(args[5]);
				MonkeyDriver monkeyDriver = new MonkeyDriver();
				monkeyDriver.setEventCount(count);
				monkeyDriver.setThrottle(throttle);
				monkeyDriver.setAppPath(apkdir);
				monkeyDriver.prepareApk();
				monkeyDriver.start();
				
				putRunFile("monkey",courseFile,name,outdir);
			}
			else if(type.equals("Linear")){
				//AppCrawler  
				int time=Integer.parseInt(args[4]);
				AppCrawlerDriver appCrawlerDriver = new AppCrawlerDriver();
				appCrawlerDriver.setAppPath(apkdir);
				appCrawlerDriver.prepareApk();
				appCrawlerDriver.setTime(time);
				appCrawlerDriver.start();
				putRunFile("appCrawler",courseFile,name,outdir);
			}
		}*/
	}
}
