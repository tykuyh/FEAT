package mooctest.FEAT.Instrument;

import java.io.*;

public class modifyManualCode {
	public static void modifyFile(File file) throws IOException {
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		StringBuffer buf=null;
			//read and modify
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if(line.contains("import mooctest.FEAT.Interface.TestCodeIF;")) {
					br.close();
					return;
				}
			}
			br.close();
			br = new BufferedReader(new FileReader(file));
			buf=new StringBuffer();
			while ((line = br.readLine()) != null) {
				if(line.contains("package")){
					buf.append(line).append("\nimport mooctest.FEAT.Interface.TestCodeIF;\n");
				}else if(line.contains("Main")&&line.contains("class")&&line.contains("{")) {
					buf.append("public class Main implements TestCodeIF {\n");
				}
				else buf.append(line).append("\n");
			}
			br.close();
			bw=new BufferedWriter(new FileWriter(file));
            bw.write(buf.toString());
            bw.close();
			
	}
	public static void modifyFiles(File file) throws IOException {
		String n=file.getName();
		if(file.isDirectory()) {
			File[] files=file.listFiles();
			for(int i=0;i<files.length;i++) {
				modifyFiles(files[i]);
			}
		}else if(n.equals("Main.java")){
			modifyFile(file);
		}
	}
	public static void main(String args[]) {
		//will modify all the Main.java files in the input document
		String str=args[0];
		File file=new File(str);
		try {
			modifyFiles(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
