package mooctest.FEAT.mdpAutoPull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mooctest.FEAT.Main.Entrance;
import mooctest.FEAT.Util.GetDeviceList;

public class autoExecution {
	public static void collectMutationLog(String outputDir,List<String> deviceList) throws IOException {
		//get the mutation execution information
		HashMap<String,Integer> killRate=new HashMap<String,Integer>();
		File f = new File(outputDir);
		File mutantApk[]=f.listFiles();
		for(int i=0;i<deviceList.size();i++) {
			String deviceName=deviceList.get(i);
			int count=0;
			for(int j=0;j<mutantApk.length;j++) {
				String filePath=outputDir+File.separator+i+File.separator+deviceName+File.separator+"Appium.log";	
				File Alog=new File(filePath);
				if(Alog.exists()) {
					String line,lastline=null;
					BufferedReader br=new BufferedReader(new FileReader(Alog));
					while((line=br.readLine())!=null) {
						lastline=line;
					}
					if(lastline.contains("200")) {
						count++;
					}
					br.close();
				}
			}
			killRate.put(deviceName, count);
		}
		File merge=new File(outputDir+File.separator+"mutantResult");
		FileWriter fwm=new FileWriter(merge);
		fwm.write("mutant number:"+mutantApk.length+"\n");
		Iterator<Map.Entry<String, Integer>> it = killRate.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry=(Entry<String,Integer>) it.next();
			fwm.write(entry.getKey()+" kill: "+(mutantApk.length-entry.getValue())+"\n");
		}
		fwm.close();
	}

	//not use now
	public static void exec(String []args,String outputFolder, String codeFolder, String testAPKName) throws Exception {
		File f=new File(outputFolder);
		File[] fs=f.listFiles();
		int length=fs.length;
		
		//length=3;
		
		for(int i=1;i<=length;i++) {
			//1
			entrance e=new entrance();
			e.copyFile(outputFolder+File.separator+"mutant"+i+".apk", codeFolder+File.separator+"apk"+File.separator+testAPKName);
			//2
			Entrance.entrance(args);
			//3-4
			List<String> deviceList=GetDeviceList.getDeviceList();
			String deviceId=deviceList.get(0);
			File ff=new File(outputFolder+"allLog");
			if(!ff.exists()) ff.mkdirs();
			e.copyFile("Appium"+File.separator+deviceId+File.separator+"Appium.log",outputFolder+"/allLog"+File.separator+i+"-"+"Appium.log");
			e.copyFile("Appium"+File.separator+deviceId+File.separator+"LogCat.log",outputFolder+"/allLog"+File.separator+i+"-"+"LogCat.log");
		}
	}
	public static void logMerge(String outputFolder,String mutantFolder, String appPackage) throws IOException {
		File f=new File(outputFolder);
		File[] fs=f.listFiles();
		int length=fs.length/2;
		
		//length=3;
		
		BufferedReader br;
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outputFolder+"/allLog"+File.separator+"Conclusion.json")));
		//mutant name;
		br=new BufferedReader(new FileReader(new File(mutantFolder+File.separator+appPackage+"-mutants.log")));
		ArrayList<String> dis=new ArrayList<String>();
		String l;
		while((l=br.readLine())!=null) {
			String []ls=l.split(":");
			dis.add(ls[ls.length-1]);
		}
		//json
		bw.write("{\n" + 
				"    \"categories\": [\n" + 
				"                {\n" + 
				"                    \"name\": \"mutant\"\n" + 
				"                }\n" + 
				"            ],\n");
		bw.write("\"nodes\": [\n");
		//
		boolean first = true;
		for(int i=1;i<=length;i++) {
			br=new BufferedReader(new FileReader(new File(outputFolder+"/allLog"+File.separator+i+"-"+"Appium.log")));
			String line;
			Boolean b=true;
			while((line=br.readLine())!=null) {
				if(line.contains("<--")) {
					if(line.contains("500")) {
						b=false;
					}
				}
			}
			if(b==false) {
				if(first==true) {
					first=false;
					bw.write("{\n" + 
							"                    \"name\": \"mutation-"+i+"\",\n" + 
							"                    \"location\":\""+dis.get(i-1)+"\",\n" + 
							"                    \"description\": \"\",\n" + 
							"                    \"category\": \"mutation\",\n" + 
							"                    \"img\": \"\"\n" + 
							"                }");
				}
				else {
					bw.write(",\n" + 
							"                {\n" + 
							"                    \"name\": \"mutation-"+i+"\",\n" + 
							"                    \"location\":\""+dis.get(i-1)+"\",\n" + 
							"                    \"description\": \"\",\n" + 
							"                    \"category\": \"mutation\",\n" + 
							"                    \"img\": \"\"\n" + 
							"                }");
				}
			}
		}
		bw.write(" ],\n" + 
				"            \"edges\": [\n            ]\n" + 
				"}");
		bw.close();
	}
	public static void main(String args[]) throws IOException {
		List<String> dl=new ArrayList<String>();
		dl.add("560bee95");
		dl.add("99513e58");
		dl.add("U20AVBPA236XP");
		dl.add("Y15CKBP322BY4");
		collectMutationLog("/home/xyr/eclipse-workspace/outputs/BihuDaily/mutantResult",dl);
		
		
	}
}
