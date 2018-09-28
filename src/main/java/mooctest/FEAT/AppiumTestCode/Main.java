package mooctest.FEAT.AppiumTestCode;
import mooctest.FEAT.Interface.TestCodeIF;

import io.appium.java_client.AppiumDriver;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.DesiredCapabilities;


public class Main implements TestCodeIF {
	
	/**
	 * "appPackage", "com.white.bihudaily"
	 * "app-launchActivity", "com.white.bihudaily.module.splash.SplashActivity"
	 *  本示例脚本仅作为参考，具体请根据自己的测试机型可能出现的特殊情况进行脚本的编写调整
	 */
	
	/**
	 * 所有和AppiumDriver相关的操作都必须写在该函数中
	 * @param driver
	 */
	public void test(AppiumDriver driver) {
		    	try {
			Thread.sleep(6000);		//等待6s，待应用完全启动
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS); //设置尝试定位控件的最长时间为8s,也就是最多尝试8s
      /*
    	 * 余下的测试逻辑请按照题目要求进行编写
    	 */

		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();

		driver.findElementById("com.white.bihudaily:id/ll_nav_user");
		driver.findElementById("com.white.bihudaily:id/tv_username").click();
		driver.findElementByXPath("//android.widget.TextView[@text='逼乎日报不会未经同意通过你的微博账号发布任何消息']").click();
		driver.findElementByXPath("//android.widget.TextView[@text='使用微博登录']");
		driver.findElementByXPath("//android.widget.TextView[@text='登录']");
		
		driver.findElementByXPath("//android.widget.Button[@text='新浪微博']").click();
		driver.findElementByXPath("//android.widget.Button[@text='腾讯微博']").click();
		
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementById("com.white.bihudaily:id/tv_my_star").click();
		driver.findElementByXPath("//android.widget.TextView[@text='0 条收藏']");
		driver.findElementById("com.white.bihudaily:id/rv_comment");
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='首页']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='开始游戏']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='电影日报']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='设计日报']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='大公司日报']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='财经日报']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='音乐日报']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.findElementByXPath("//android.widget.CheckedTextView[@text='体育日报']").click();
		driver.findElementByXPath("//android.widget.ImageButton[@content-desc='转到上一层级']").click();
		driver.quit();
	}
	
	
	
	/**
	 * AppiumDriver的初始化逻辑必须写在该函数中
	 * @return
	 */
	public AppiumDriver initAppiumTest() {
		
		AppiumDriver driver=null;
        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "apk");
        File app = new File(appDir, "BiHuDaily1.apk");
        
        //设置自动化相关参数
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android Emulator");
        
        //设置安卓系统版本
        capabilities.setCapability("platformVersion", "4.4");
        //设置apk路径
        capabilities.setCapability("app", app.getAbsolutePath()); 
        
        //设置app的主包名和主类名
        capabilities.setCapability("appPackage", "com.white.bihudaily");
        capabilities.setCapability("appActivity", ".module.splash.SplashActivity");
        //设置使用unicode键盘，支持输入中文和特殊字符
        capabilities.setCapability("unicodeKeyboard","true");
        //设置用例执行完成后重置键盘
        capabilities.setCapability("resetKeyboard","true");
        //初始化
        try {
			driver = new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
        return driver;
	}
	
	public void start() {
		test(initAppiumTest());
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}
	

}
