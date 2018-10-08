package mooctest.FEAT.Monkey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import mooctest.FEAT.Interface.AutomatorToolsDriver;
import mooctest.FEAT.Monkey.MonkeyTest;
import mooctest.FEAT.Util.GetDeviceList;


public class MonkeyDriver extends AutomatorToolsDriver{
	String batPath;
	
	public MonkeyDriver(String bp,String appPath,String outputdir) {
		super(appPath,outputdir);
		batPath=bp;
	}
	
	
	public MonkeyTest startTestTask(String device) throws IOException {
		MonkeyTest MonkeyTest;
		MonkeyTest = new MonkeyTest(device ,getAppPackage(), getAppPath(), getOutputDir(),batPath);
		MonkeyTest.start();
		return MonkeyTest;
	}

	public int getEventsInjected(File log) throws IOException {
		if(!log.exists()) return 0;
		BufferedReader br=new BufferedReader(new FileReader(log));
		String line;
		while(!((line=br.readLine())==null)) {
			if(line.contains("Events injected")) {
				br.close();
				return(Integer.parseInt(line.split(":")[1]));
			}
		}
		br.close();
		return 0;
	}
	public double getMutationRatio() {
		try {
			File f=new File(this.getOutputDir()+File.separator+"Monkey"+File.separator+"mutationResult");
			File mutantResultFile=new File(this.getOutputDir()+File.separator+"Monkey"+File.separator+"mutationResult"+File.separator+"mutationResult.txt");
			BufferedWriter bw=new BufferedWriter(new FileWriter(mutantResultFile));
			double score=0;
			File []mutantResults=f.listFiles();
			int mutationNumber=mutantResults.length;
			bw.write("mutantNumber: "+mutationNumber+"\n");
			bw.flush();
			List<String> deviceList=GetDeviceList.getDeviceList();
			for(int i=0;i<deviceList.size();i++) {
				int kill=0,originEvents=0;
				File originalMonkeylog=new File(this.getOutputDir()+File.separator+"Monkey"+File.separator+deviceList.get(i)+File.separator+"Monkey.log");
				originEvents=getEventsInjected(originalMonkeylog);
				if(originEvents<=0) {
					bw.write("UDID: "+deviceList.get(i)+"\n");
					bw.write("killed: "+"0"+"\n");
					bw.write("score: "+"0"+"\n");
					bw.flush();
				}else {
					for(int j=1;j<=mutationNumber;j++) {
						File MonkeyLog=new File(this.getOutputDir()+File.separator+"Monkey"+File.separator+"mutationResult"+File.separator+j+File.separator+"Monkey"+File.separator+deviceList.get(i)+File.separator+"Monkey.log");
						int mutationEvents=getEventsInjected(MonkeyLog);
						if(mutationEvents!=originEvents) kill++;
					}
					bw.write("UDID: "+deviceList.get(i)+"\n");
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
