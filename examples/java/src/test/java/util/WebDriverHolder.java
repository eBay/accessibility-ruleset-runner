//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Aravind Kannan, Scott Izu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//////////////////////////////////////////////////////////////////////////

package util;

import java.net.URL;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * This class supports instantiating multiple WebDriver instances:
 *   Each is on its own thread
 *   Each occupies a node on a Selenium Grid
 *
 */
public class WebDriverHolder {

	/**
	 * If REMOTE_URL == null, this will create a local Firefox run
	 * 
	 * This code builds the driver is being constructed by hand
	 * 
	 * @return
	 */
	private static ThreadLocal<WebDriver> driverThread = new ThreadLocal<WebDriver>();
	
	// For local runs, will create one driver for whole suite
	@BeforeSuite(alwaysRun = true)
	public void startupDriverBeforeSuite() {
		System.out.println("startupDriverBeforeSuite");
		if(createDriverForSuite() && driverThread.get() == null) {
			driverThread.set(new DriverWrapper().startupDriver(ARRProperties.USER_AGENT.getPropertyValue(), ARRProperties.REMOTE_URL.getPropertyValue(), ARRProperties.PROXY.getPropertyValue()));
		}
	}
	
	// For Jenkins runs, will create one drivers per method call
	@BeforeMethod(alwaysRun = true)
	public void startupDriverBeforeMethod() {
		System.out.println("startupDriverBeforeMethod");
		if(!createDriverForSuite() && driverThread.get() == null) {
			driverThread.set(new DriverWrapper().startupDriver(ARRProperties.USER_AGENT.getPropertyValue(), ARRProperties.REMOTE_URL.getPropertyValue(), ARRProperties.PROXY.getPropertyValue()));
		}
	}

	// For Jenkins runs, will shutdown each driver after method
	@AfterMethod(alwaysRun = true)
	public void shutdownDriverAfterMethod(){
		System.out.println("shutdownDriverAfterMethod");
		if(!createDriverForSuite() && driverThread.get() != null) {
			shutdownDriver();
		}
	}
	
	// For local runs, will shutdown driver after whole suite
	@AfterSuite(alwaysRun = true)
	public void shutdownDriverAfterSuite() {
		System.out.println("shutdownDriverAfterSuite");
		if(createDriverForSuite() && driverThread.get() != null) {
			shutdownDriver();
		}
	}
	
	// Can be overridden - For Unit Tests/Generated Report will create one driver for both local and Jenkins runs
	public boolean createDriverForSuite(){
		String remoteURL = ARRProperties.REMOTE_URL.getPropertyValue();
		return (remoteURL == null || remoteURL.isEmpty());
	}
	
	public static WebDriver startupDriver() {
		if(driverThread.get() == null) {
			driverThread.set(new DriverWrapper().startupDriver(ARRProperties.USER_AGENT.getPropertyValue(), ARRProperties.REMOTE_URL.getPropertyValue(), ARRProperties.PROXY.getPropertyValue()));
		}
		return driverThread.get();
	}
	
	public static void shutdownDriver() {
		if(driverThread.get() != null) {
			new DriverWrapper().shutdownDriver(driverThread.get());
		}
		driverThread.set(null);
	}
	
	public static class DriverWrapper {
		public WebDriver startupDriver(String useragent, String remoteURL, String proxy) {
			System.out.println("startupDriver");
			DesiredCapabilities ffCapabilities = DesiredCapabilities.firefox();
			if(useragent != null && !useragent.isEmpty() && !useragent.equals("null") && !useragent.contains("USER_AGENT")) {
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("general.useragent.override", useragent);
				ffCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
			}
			
			// 9/28/2016 - Sometimes even though REMOTE_URL is set, due to full grid, RemoteWebDriver can't be created in Jenkins jobs
			// In such case, this will try to find local Firefox instance to create FirefoxDriver which may not exist
			// For this set the driverThread to null
			if (proxy != null && !proxy.isEmpty() && !proxy.equals("null") && !proxy.contains("PROXY")) {
				// String PROXY = "10.254.138.117:80";
				// DesiredCapabilities capabilities = DesiredCapabilities.firefox();
				org.openqa.selenium.Proxy proxyServer = new org.openqa.selenium.Proxy();
				proxyServer.setHttpProxy(proxy).setFtpProxy(proxy).setSslProxy(proxy);
				ffCapabilities.setCapability(CapabilityType.PROXY, proxyServer);
			}

			if(remoteURL != null && !remoteURL.isEmpty() && !remoteURL.equals("null") && !remoteURL.contains("REMOTE_URL")) {
				try {
					System.out.println("Trying remote driver");
					URL gridUrl = new URL(remoteURL);
					return new RemoteWebDriver(gridUrl, ffCapabilities);
				} catch (Exception ex) { 
					System.out.println("Error: Initializing Remote Driver Failed");
					System.out.println("Error: Initializing Remote Driver Failed stack trace:"+ExceptionUtils.getStackTrace(ex));
					ex.printStackTrace();
				}
			}
			
			try {
				System.out.println("Trying ChromeDriver (may install)");
				
				DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
				if(useragent != null && !useragent.isEmpty() && !useragent.equals("null") && !useragent.contains("USER_AGENT")) {
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--user-agent="+useragent);
					chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
				}
				
//				String chromeDriver = BrowserDriverInstaller.installRequiredExeIfNeeded("CHROME");
//				System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\Git\\usr\\bin\\chromedriver.exe");
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.home")+"/bin/chromedriver.exe");
				return new ChromeDriver(chromeCapabilities);
			} catch (Exception ex) {
				System.out.println("Error: Initializing Driver Failed");
				System.out.println("Error: Initializing Driver Failed stack trace:"+ExceptionUtils.getStackTrace(ex));
				ex.printStackTrace();
			}
			
			try {
				System.out.println("Trying FirefoxDriver (may install)");
//				String ffDriver = BrowserDriverInstaller.installRequiredExeIfNeeded("FF");
//				System.setProperty("webdriver.gecko.driver", ffDriver);
				System.setProperty("webdriver.gecko.driver", System.getProperty("user.home")+"/bin/geckodriver.exe");
				return new FirefoxDriver(ffCapabilities);
			} catch (Exception ex) {
				System.out.println("Error: Initializing Driver Failed");
				System.out.println("Error: Initializing Driver Failed stack trace:"+ExceptionUtils.getStackTrace(ex));
				ex.printStackTrace();
			}
			
			return null;
		}
		
		public void shutdownDriver(WebDriver driver) {
			System.out.println("shutdownDriver");
			driver.quit();
		}
	}
}