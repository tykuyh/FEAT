package mooctest.FEAT.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.transform.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
class LogCatFormat{
	String info;
	String detail;
	LogCatFormat(String i,String d){
		info=i;
		detail=d;
	}
}
class ExceptionType{
	String level;
	String component;
	int PID,TID; //process id, thread id
	ArrayList<String> text;
	int number;
	ExceptionType(String l,String c,int p,int t,String text){
		level=l;
		component=c;
		PID=p;
		TID=t;
		this.text=new ArrayList<String>();
		this.text.add(text);
		number=1;
	}
	ExceptionType(String l,String c,String text){
		level=l;
		component=c;
		PID=0;
		TID=0;
		this.text=new ArrayList<String>();
		this.text.add(text);
		number=1;
	}
	ExceptionType(String l,String c,int p,int t,ArrayList<String> text){
		level=l;
		component=c;
		PID=p;
		TID=t;
		this.text=text;
		number=1;
	}
	public boolean equals(ExceptionType et) {
		if(!et.level.equals(this.level)) return false;
		if(!et.component.equals(this.component)) return false;
		if(!(et.PID==this.PID)) return false;
		if(!(et.TID==this.TID)) return false;
		if(!(et.text.size()==this.text.size())) return false;
		for(int i=0;i<et.text.size();i++) {
			if(!et.text.get(i).equals(this.text.get(i))) return false;
		}
		return true;
	}
	public boolean attributeEquals(ExceptionType et) {
		if(!et.level.equals(this.level)) return false;
		if(!et.component.equals(this.component)) return false;
		if(!(et.PID==this.PID)) return false;
		if(!(et.TID==this.TID)) return false;
		return true;
	}
	public boolean exceptionEquals(ExceptionType et) {
		if(!et.level.equals(this.level)) return false;
		if(!et.component.equals(this.component)) return false;
		if(!(et.text.size()==this.text.size())) return false;
		for(int i=0;i<et.text.size();i++) {
			if(!et.text.get(i).equals(this.text.get(i))) return false;
		}
		return true;
	}
}
public class GetPoint{
	long startTime;
	String outputdir,projectDir;
	double[] coverage=new double[6];
	double coverageRate,exceptionRate,mutationRate=0;
	double coveragePoint,exceptionPoint,mutationPoint=0;
	int exceptionMaxNumber;
	//init
	GetPoint(String od, String projectDir,long t) throws IOException{
		outputdir=od;
		this.projectDir=projectDir;
		startTime=t;
		File f = new File("PointConfig.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		coverageRate=Double.parseDouble(br.readLine().split("=")[1]);
		for(int i=0;i<6;i++){
			coverage[i]=Double.parseDouble(br.readLine().split("=")[1]);
		}
		exceptionRate=Double.parseDouble(br.readLine().split("=")[1]);
		mutationRate=Double.parseDouble(br.readLine().split("=")[1]);
		br.close();
		
	}
	public double getHTMLPoint() throws IOException{
		String filename=outputdir+File.separator+"report"+File.separator+"coveragereport"+File.separator;
		File input = new File(filename+"index.html"); 
		Document doc = Jsoup.parse(input,"UTF-8"); 
		Elements links = doc.getElementsByTag("tfoot"); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputdir+File.separator+"report"+File.separator+"HTMLPoint.txt"));
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
		//point6(branch,cxty,lines,methods,classes,instructions);
		double point=instructions*coverage[0]+branch*coverage[1]+cxty*coverage[2]+lines*coverage[3]+methods*coverage[4]+classes*coverage[5];
		coveragePoint=point;
		return point;
	}
	/*
	@SuppressWarnings({ "unused", "deprecation" })
	private void point6(double b,double cx,double l,double m,double c,double i) throws IOException{
		
		File file = new File("/home/xyr/eclipse-workspace/outputs/result/"+Date.parse(new Date().toString())+".csv");
        
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "GBK");
        String []titles= {"branch","cxty","lines","methods","classes","instructions"};
        //csv
        for(String title : titles){
          ow.write(title);
          ow.write(",");
        }
        
        ow.write("\r\n");
        
        ow.write(Double.toString(b));ow.write(",");
        ow.write(Double.toString(cx));ow.write(",");
        ow.write(Double.toString(l));ow.write(",");
        ow.write(Double.toString(m));ow.write(",");
        ow.write(Double.toString(c));ow.write(",");
        ow.write(Double.toString(i));ow.write(",");
        ow.flush();
        ow.write("\r\n");ow.flush();ow.close();
   
	}
	*/
	public String deleteUselessSpace(String string) {
		StringBuffer sb=new StringBuffer();
		sb.append(string.charAt(0));sb.append(string.charAt(1));
		for(int i=2;i<string.length();i++){ 
			try {
				if(string.charAt(i-1)==':'&&string.charAt(i)==' '){ 
					if(string.charAt(i+1)==']') {
						sb.append(' '); 
					}
				} else if(string.charAt(i-2)==':'&&string.charAt(i-1)==' '&&string.charAt(i)==' '){ 
					if(string.charAt(i+1)==']') {
						sb.append(' '); 
					}
				} else if(string.charAt(i)!=' '){ 
					sb.append(string.charAt(i));
				} else if(string.charAt(i)==' '&&string.charAt(i+1)!=' '){ 
					sb.append(' '); 
				} 
			}catch(Exception e){ 
				continue; 
			} 
		} 
		return sb.toString();
	}
	public void getExcepetionInfo(String filename,String filename2) throws ParseException {
        File logCat = new File(filename);
        File exceptionBlocks = new File(filename2);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String tempString = null;
        int flag=0;
        try {
            reader = new BufferedReader(new FileReader(logCat));
            writer = new BufferedWriter(new FileWriter(exceptionBlocks));
            //getLog and sort ExceptionType
            while ((tempString = reader.readLine()) != null) {
            	if(tempString.startsWith("[")){
            		tempString=deleteUselessSpace(tempString);
            		String[] logcatInfo=tempString.split(" ");
            		if((logcatInfo.length!=6)||(!tempString.endsWith("]"))) {
            			continue;
            		}
            		String date=logcatInfo[1].trim();
            		String time=logcatInfo[2].trim();
            		
            	    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");  
            		String nowtime= date+" "+time.split("\\.")[0];
            		Date logDate = sdf.parse(nowtime); 
            		logDate.setYear(new Date().getYear());
            		long logTime=logDate.getTime();
            		if(logTime<startTime) continue;
            		
            		
            		String pid=logcatInfo[3].trim().split(":")[0];
            		String tid=logcatInfo[3].trim().split(":")[1];
            		String levelAndcomponent;
            		if(!logcatInfo[5].contains("]")) {
            			levelAndcomponent=logcatInfo[4].trim()+logcatInfo[5].trim();
            		}
            		else levelAndcomponent=logcatInfo[4].trim();
            		String level=levelAndcomponent.split("/")[0];
            		String component;
            		if(levelAndcomponent.endsWith("/")) component="";
            		else component=levelAndcomponent.split("/")[1];
            		if(date.contains("-")&&time.contains(".")) {
	            		tempString = reader.readLine();
	            		String text=tempString;
	            		if(level.equals("E")||level.equals("W")) {
	            			if(flag==0) {
	            				writer.write("\n"+"block"+"\n");
		        				writer.flush();
		        				flag=1;
	            			}
	        				writer.write("[ "+pid+":"+tid+" "+level+"/"+component+" ]"+"\n");
	        				writer.flush();
	        				writer.write(text+"\n");
	        				writer.flush();
	            		}else flag=0;
            		}
            	}
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
	public boolean compareList(ArrayList<String> a,ArrayList<String> b) {
		if(a.size()!=b.size()) return false;
		for(int i=0;i<a.size();i++) {
			if(!a.get(i).equals(b.get(i))) return false;
		}
		return true;
	}
	public ArrayList<ExceptionType> getExcepetionCollect(String filename2,String filename3){
		File exceptionBlocks = new File(filename2);
        File exceptionTypes = new File(filename3);
        BufferedReader reader = null;
        BufferedWriter writer = null;

        ArrayList<ExceptionType> result=new ArrayList<ExceptionType>();
        try {
            reader = new BufferedReader(new FileReader(exceptionBlocks));
            writer = new BufferedWriter(new FileWriter(exceptionTypes));
            String tempString = null;
            ArrayList<ExceptionType> tempET=new ArrayList<ExceptionType>();
            while ((tempString = reader.readLine()) != null) {
            	if(tempString.equals("block")) {
            		for(int i=0;i<tempET.size();i++) {
            			if(result.size()==0) result.add(tempET.get(i));
            			for(int j=0;j<result.size();j++) {
            				if(tempET.get(i).equals(result.get(j))) {
            					result.get(j).number++;
            					break;
            				}else if(j==result.size()-1) {
            					result.add(tempET.get(i));
            				}
            			}
            		}
            		tempET=new ArrayList<ExceptionType>();
            	}else if(tempString.startsWith("[")) {
            		String[] infos=tempString.split(" ");
            		int pid=Integer.parseInt(infos[1].split(":")[0]);
            		int tid=Integer.parseInt(infos[1].split(":")[1]);
            		String level=infos[2].split("/")[0];
            		String component;
            		if(infos[2].endsWith("/")) component="";
            		else component=infos[2].split("/")[1];
            		tempString = reader.readLine();
            		String text=tempString;
            		
            		ExceptionType ET=new ExceptionType(level,component,pid,tid,text);
            		if(tempET.size()==0) tempET.add(ET);
            		for(int i=0;i<tempET.size();i++) {
            			if(pid==tempET.get(i).PID&&
            				tid==tempET.get(i).TID&&
            				level.equals(tempET.get(i).level)&&
            				component.equals(tempET.get(i).component)) {
            				if(tempET.get(i).text.size()==0) {
            					tempET.get(i).text.add(text);
                				break;
            				}
            				for(int j=0;j<tempET.get(i).text.size();j++) {
            					if(tempET.get(i).text.get(j).equals(text)) {
            						break;
            					}else if(j==tempET.get(i).text.size()-1) {
            						tempET.get(i).text.add(text);
            					}
            				}
            				break;
            			}else if(i==tempET.size()-1) {
                    		tempET.add(ET);
            			}
            		}
            	}
            }
            reader.close();
            for(int i=0;i<result.size();i++){
            	writer.write("[ ExCollect: "+result.get(i).PID+":"+result.get(i).TID+" "+result.get(i).level+"/"+result.get(i).component+" number:"+result.get(i).number+" ]"+"\n");
            	writer.flush();
            	for(int j=0;j<result.get(i).text.size();j++) {
            		writer.write(result.get(i).text.get(j)+"\n");
                	writer.flush();
            	}
            	writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
		return result;
	}
	public ArrayList<ExceptionType>  getExcepetionTypes(ArrayList<ExceptionType> result, String out) throws IOException{

		ArrayList<ExceptionType> lastResult=new ArrayList<ExceptionType>();
		File f=new File(out);
		FileWriter writer=new FileWriter(f);
		for(int i=0;i<result.size();i++){
			for(int j=0;j<result.get(i).text.size();j++) {
				if(result.get(i).text.get(j).contains("Exception")){
					lastResult.add(new ExceptionType(result.get(i).level, result.get(i).component, result.get(i).PID, result.get(i).TID, result.get(i).text));
	            	break;
				}
			}
		}

		for(int i=0;i<lastResult.size();i++) {
			writer.write("[ ExType: "+result.get(i).PID+":"+result.get(i).TID+" "+lastResult.get(i).level+"/"+lastResult.get(i).component+" ]"+"\n");
        	writer.flush();
        	for(int k=0;k<lastResult.get(i).text.size();k++) {
        		writer.write(lastResult.get(i).text.get(k)+"\n");
            	writer.flush();
        	}
		}
		writer.close();
		return lastResult;
	}
	
	public double integraterExceptionTypes(ArrayList<ExceptionType> lastResult) throws IOException {
		double res;
		double last,old;
		
		//integrater itself
		for(int i=0;i<lastResult.size();i++) {
			for(int j=i+1;j<lastResult.size();j++) {
				if(lastResult.get(i).exceptionEquals(lastResult.get(j))) {
					lastResult.remove(j);
					j--;
				}
			}
		}		
		last=lastResult.size();
		
		ArrayList<ExceptionType> oldResult=new ArrayList<ExceptionType>();
		File f=new File(projectDir+File.separator+"ExceptionTypes.log");
		if(f.exists()) {
			//read old result
			BufferedReader br=new BufferedReader(new FileReader(f));
			String tempString;
			ExceptionType ET=null;
			while ((tempString = br.readLine()) != null) {
				if(tempString.startsWith("[")&&tempString.endsWith("]")&&tempString.contains(":")&&tempString.contains("/")) {
					if(ET!=null) oldResult.add(ET);
					String[] tempStrings=tempString.split(" ");
					tempString=br.readLine();
					ET=new ExceptionType(tempStrings[2].split("/")[0],tempStrings[2].split("/")[1],tempString);
				}else {
					if(ET!=null) ET.text.add(tempString);
				}
			}
			oldResult.add(ET);
			br.close();
			//integrater result
			for(int i=0;i<lastResult.size();i++) {
				for(int j=0;j<oldResult.size();j++) {
					if(lastResult.get(i).exceptionEquals(oldResult.get(j))) {
						break;
					}
					else if(j==oldResult.size()-1) {
						oldResult.add(lastResult.get(i));
					}
				}
			}
		}else {
			oldResult=lastResult;
		}
		old=oldResult.size();
		//rewrite
		BufferedWriter bw=new BufferedWriter(new FileWriter(f));
		for(int i=0;i<oldResult.size();i++) {
			bw.write("[ ExType: "+oldResult.get(i).level+"/"+oldResult.get(i).component+" ]"+"\n");
			bw.flush();
        	for(int k=0;k<oldResult.get(i).text.size();k++) {
        		bw.write(oldResult.get(i).text.get(k)+"\n");
        		bw.flush();
        	}
		}
		bw.close();
		res=last/old;
		return res;
	}
	public ArrayList<ExceptionType> removeDuplicates(ArrayList<ExceptionType> result){
		//wait for add
		
		return result;
	}
	public double getExceptionPoint(String log) throws IOException, ParseException{
		String logDir=log.substring(0, log.lastIndexOf("/"));
		getExcepetionInfo(log,logDir+File.separator+"1-ExceptionInfo.txt");
		ArrayList<ExceptionType> result=getExcepetionCollect(logDir+File.separator+"1-ExceptionInfo.txt",logDir+File.separator+"2-ExceptionCollect.txt");
		ArrayList<ExceptionType> lastresult = getExcepetionTypes(result,logDir+File.separator+"3-ExceptionTypes.txt");
		lastresult= removeDuplicates(lastresult);

		//compare with rootCode Exceptions
		exceptionPoint=integraterExceptionTypes(lastresult);
		return exceptionPoint;
	}
	public double getMutationPoint(String log) throws IOException, ParseException{
		String logDir=log.substring(0, log.lastIndexOf("/"));
		getExcepetionInfo(log,logDir+File.separator+"1-ExceptionInfo.txt");
		ArrayList<ExceptionType> result=getExcepetionCollect(logDir+File.separator+"1-ExceptionInfo.txt",logDir+File.separator+"2-ExceptionCollect.txt");
		double number = 0;
		
		
		return number;
	}
	public double getLastPoint(){
		return coverageRate*coveragePoint
				+exceptionRate*exceptionPoint
				+mutationRate*mutationPoint;
	}
	public static void main(String []args) throws IOException, ParseException {
		GetPoint gp=new GetPoint("/home/xyr/eclipse-workspace/outputs/memetastic","/home/xyr/eclipse-workspace/sourceCode/BihuDaily", 10000);
		double ExceptionPoint = gp.getExceptionPoint("");//calculate point from exception
	}
}
