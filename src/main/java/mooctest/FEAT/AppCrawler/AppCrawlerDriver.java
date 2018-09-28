package mooctest.FEAT.AppCrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import mooctest.FEAT.Interface.AutomatorToolsDriver;
import mooctest.FEAT.Util.GetDeviceList;
import mooctest.FEAT.Appium.AppiumManager;

public class AppCrawlerDriver extends AutomatorToolsDriver{
	String batPath;
	String toolPath;
	
	public AppCrawlerDriver(String bp,String appPath,String outputdir,String toolPath) {
		super(appPath,outputdir);
		batPath=bp;
		this.toolPath=toolPath;
	}
	
	public void start() throws InterruptedException, IOException {
		System.out.println("AppCrawlerDriver start");
		dispatchTest();
		observeTest();
		//In Linux, we kill all the appium after the execution
		AppiumManager.stopAppium("");
		System.out.println("close appium");
	}
	
	public AppCrawlerTest startTestTask(String device) throws IOException {
		AppCrawlerTest AppCrawlerTest;
		AppCrawlerTest = new AppCrawlerTest(device ,getAppPackage(), getAppPath(), getOutputDir(),batPath,toolPath);
		AppCrawlerTest.start();
		return AppCrawlerTest;
	}

	public double getEventsInjected(File log) throws IOException {
		if(!log.exists()) return 0;
		BufferedReader br=new BufferedReader(new FileReader(log));
		String line;
		br.readLine();
		line=br.readLine();
		return Double.parseDouble(line.split(" ")[0].split("=")[1]);
	}
	public double getMutationRatio() {
		try {
			File f=new File(this.getOutputDir()+File.separator+"AppCrawler"+File.separator+"mutationResult");
			File mutantResultFile=new File(this.getOutputDir()+File.separator+"AppCrawler"+File.separator+"mutationResult"+File.separator+"mutationResult.txt");
			BufferedWriter bw=new BufferedWriter(new FileWriter(mutantResultFile));
			double score=0;
			File []mutantResults=f.listFiles();
			int mutationNumber=mutantResults.length;
			bw.write("mutantNumber: "+mutationNumber+"\n");
			bw.flush();
			List<String> deviceList=GetDeviceList.getDeviceList();
			for(int i=0;i<deviceList.size();i++) {
				int kill=0;
				double originEvents;
				File originalMonkeylog=new File(this.getOutputDir()+File.separator+"AppCrawler"+File.separator+deviceList.get(i)+File.separator+"result.log");
				originEvents=getEventsInjected(originalMonkeylog);
				if(originEvents==0) {
					bw.write("UDID: "+deviceList.get(i)+"\n");
					bw.write("killed: "+"0"+"\n");
					bw.write("score: "+"0"+"\n");
					bw.flush();
				}else {
					for(int j=1;j<=mutationNumber;j++) {
						File MonkeyLog=new File(this.getOutputDir()+File.separator+"AppCrawler"+File.separator+"mutationResult"+File.separator+j+File.separator+"AppCrawler"+File.separator+deviceList.get(i)+File.separator+"result.log");
						double mutationEvents=getEventsInjected(MonkeyLog);
						if(mutationEvents!=originEvents) kill++;
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
}
