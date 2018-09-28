package mooctest.FEAT.Appium;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import mooctest.FEAT.Util.OSUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;



public class AppiumManager {
    private static final int INIT_PORT = 4723;
    private static AppiumManager manager;
    private HashMap<String, String> deviceIDAndPortMap = new HashMap<String, String>();
   //private HashMap<String, String> portAnddeviceIDMap = new HashMap<>();
    private int nextPort;
    
    private AppiumManager() {
        super();
        nextPort = INIT_PORT;
        
    }

    public static AppiumManager getInstance() {
        if (manager == null) {
            manager = new AppiumManager();
        }
        return manager;
    }

    public String checkDevicePort(String deviceID) {
        return deviceIDAndPortMap.get(deviceID);
    }
    
    public static void stopAppium(String port) throws IOException, InterruptedException {
    	if(OSUtil.isWin())
    		Runtime.getRuntime().exec("cmd /c echo off & FOR /F \"usebackq tokens=5\" %a in"
                + " (`netstat -nao ^| findstr /R /C:\"" + port + "\"`) do (FOR /F \"usebackq\" %b in"
                + " (`TASKLIST /FI \"PID eq %a\" ^| findstr /I node.exe`) do taskkill /F /PID %a)");
    	else {
    		//String cmd="ps -A | grep node | grep -v grep | awk 'NR=1 {print $1}' | xargs kill -9";
    		String cmd="pkill -9 node";
    		Runtime.getRuntime().exec(cmd);
    	}
    }

    public void setupAppium(String outputdir ,String deviceID, String logCatPath, String appiumLogsPath, String exceptionLogsPath) throws InterruptedException {

        String port = checkDevicePort(deviceID);
        if (port == null) {
            port = getFreePort();
            deviceIDAndPortMap.put(deviceID, port);
        }

        if(checkAppiumRunning(port) ){
            System.out.println("appium in " + port + " is running now");
           return;
        }
        File log = new File(logCatPath);
        try {
            if(!log.exists()) {
                log.createNewFile();
            }else {
                log.delete();
                log.createNewFile();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        File output = new File(appiumLogsPath);
        try {
            if (!output.exists()) output.createNewFile();
            else{
                output.delete();
                output.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Runtime r=Runtime.getRuntime();
        try {
            String cmd = OSUtil.getCmd();
            if (OSUtil.isWin()) {
                //r.exec("Commands\\win\\appiumWin.bat " + port + " " + deviceID + " " + appiumLogsPath);
            	try {
					startAppium(port, deviceID, appiumLogsPath);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            } else {
            	//Process process = null;
                int dp = (Integer.parseInt(port) + 100);
                String command = cmd + " Commands/linux/appium.sh " + port + " " + deviceID + " " + exceptionLogsPath
                        + " " + appiumLogsPath + " " + dp;
                System.out.println(command);
               r.exec(command);
               //process.waitFor();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
			FileReader fr=new FileReader(new File(outputdir + File.separator + deviceID + File.separator + "Appium.log"));
			BufferedReader br=new BufferedReader(fr);
			String line=br.readLine();
			System.out.println(line);
			boolean judge=false;
			if(line==null){
				judge=true;
			}else if(line.equals("")){
				judge=true;
			}else if(line.split(" ").length<=1){
				judge=true;
			}else if(line.split(" ")[1].equals("-a")){
				judge=true;
			}
			System.out.println("Waiting for server "+deviceID+" openning...");
			while(judge){
				line=br.readLine();
                if(line==null){
					judge=true;
				}else if(line.equals("")){
					judge=true;
				}else if(line.split(" ").length<=1){
					judge=true;
				}else if(line.split(" ")[1].equals("-a")){
					judge=true;
				}else{
					judge=false;
				}
			}
			br.close();
			System.out.println("Server " + port + " " + deviceID + " is open");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public  String getFreePort() {
        boolean found = false;
        String ret = null;
        while (!found) {
            if (!isLocalPortUsing(nextPort)) {
                ret = String.valueOf(nextPort);
                found = true;
            }
            nextPort ++;
        }
        return ret;
    }
    
    public static void startAppium(String port , String deviceID, String appiumLogsPath) throws IOException, InterruptedException { 

       DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler(); 
       String cmd;
       cmd = "cmd.exe /c appium -a 127.0.0.1 -p " + port + " -U " + deviceID + " --log-timestamp --local-timezone --log-no-colors > " + appiumLogsPath;
       CommandLine commandLine = CommandLine.parse(cmd); 

       ExecuteWatchdog dog = new ExecuteWatchdog(60 * 1000);
       DefaultExecutor executor = new DefaultExecutor(); 

       executor.setExitValue(1);
       executor.setWatchdog(dog);
       executor.execute(commandLine, resultHandler);
       resultHandler.waitFor(5000); 
       System.out.println("Appium server start"); 
       }

    public static boolean checkAppiumRunning(String port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", Integer.parseInt(port));

        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isLocalPortUsing(int port){
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isPortUsing(String host,int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        int p=Integer.valueOf(port);
        try {
            @SuppressWarnings("resource")
			Socket socket = new Socket(theAddress,p);
            flag = true;
            System.out.println("port in "+socket.getPort() +" is not using");
        } catch (IOException e) {
        	System.out.println("port is using");
        }
        return flag;
    }
    
    
}
