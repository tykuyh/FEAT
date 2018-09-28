package mooctest.FEAT;

import java.io.IOException;
import mooctest.FEAT.AppCrawler.AppCrawlerTest;
import mooctest.FEAT.Appium.AppiumManager;
import mooctest.FEAT.Interface.AutomatorToolsDriver;
class LinearAppCrawlerDriver extends AutomatorToolsDriver{
	String batPath;
	String toolPath;
	
	public LinearAppCrawlerDriver(String bp,String appPath,String outputdir,String toolPath) {
		super(appPath,outputdir);
		batPath=bp;
		this.toolPath=toolPath;
	}
	
	public void start() throws InterruptedException, IOException {
		System.out.println("AppCrawlerTest start");
		for (int i = 0 ; i < getDeviceList().size() ; i++) {
				try {
					String deviceID = getDeviceList().get(i);
					AppCrawlerTest task = startTestTask(deviceID);
					taskList.add(task);
					observeTest();
					AppiumManager.stopAppium("");
					System.out.println("close appium");
				}catch(Exception e){
					taskList.clear();
				}
		}
	}
	public AppCrawlerTest startTestTask(String device) throws IOException {
		AppCrawlerTest AppCrawlerTest;
		AppCrawlerTest = new AppCrawlerTest(device ,getAppPackage(), getAppPath(), getOutputDir(),batPath,toolPath);
		AppCrawlerTest.start();
		return AppCrawlerTest;
	}

	@Override
	public void dispatchTest() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMutationRatio() {
		// TODO Auto-generated method stub
		return 0;
	}	
}
public class Test {
	public static void main(String[] args) throws InterruptedException, IOException {
		
		
	}
}
