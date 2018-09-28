package mooctest.FEAT.Interface;

import io.appium.java_client.AppiumDriver;

public interface TestCodeIF {
	public abstract void test(AppiumDriver driver) throws Exception;
	public abstract AppiumDriver initAppiumTest() throws Exception;
}
