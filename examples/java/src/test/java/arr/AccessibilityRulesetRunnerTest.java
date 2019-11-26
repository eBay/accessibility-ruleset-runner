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

package arr;

import java.io.File;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import report.JSONToHTMLConverter;
import util.ARRProperties;
import util.FileDownloader;
import util.ScreenshotsProcessor;
import util.ScreenshotsProcessor.ScreenShotElementRectangle;
import util.UrlProvider;
import util.WebDriverHolder;

/**
 * People can test their URLs against accessibility rules, by running this test.
 * 1. With CommandLine: mvn test -e
 * 2. With CommandLine parameters:
 *    mvn test -e -DURLS_TO_TEST="[Google] http://www.google.com"
 * 3. With TestNGEclipsePlugin: Right Click->Run As->TestNG Test
 * 4. With TestNGEclipsePlugin parameters: Right Click->Run As->Run Configurations
 *    Right Click TestNG->New
 *    Name: AccessibilityRulesetRunnerTest
 *    Class: arr.AccessibilityRulesetRunner
 *    Arguments Tab->VM arguments:
 *    -DURLS_TO_TEST="[Google] http://www.google.com"
 * 
 * Note: Class name must end with Test suffix to be run by mvn test from the command line
 */
public class AccessibilityRulesetRunnerTest {	
	private static final String CUSTOM_RULESET_LOCATION = "https://raw.githubusercontent.com/eBay/accessibility-ruleset-runner/master/rulesets/custom.ruleset.1.1.33.js";
	private static final String AXE_RULESET_LOCATION = "https://raw.githubusercontent.com/eBay/accessibility-ruleset-runner/master/rulesets/aXe.ruleset.3.4.0.js";
	
	@Test(dataProvider = "urlsToTest", dataProviderClass = UrlProvider.class)
	public void accessibilityRulesetRunnerTest(String url, String viewName) throws Exception {

		/** Should trigger an error when running in Jenkins
		 * If catch Exception, will trigger an error in report validation, stack trace will print
		 * If don't catch Exception, will trigger an error from this run, stack trace won't print
		 */
		try { // Should trigger an error when running in Jenkins, will fail report validation			
			WebDriver driver = WebDriverHolder.startupDriver();

			if(driver != null) { // Allow for driver to be null.  We require each view to have a test result.  This way, if one view fails to be tested for any reason, we can not upload the ARR report.
				driver.get(url);
			} else {
				System.out.println("!!!!! WARNING Driver was null at beginning of test");
			}
			
			System.out.println("PRE PROCESSING STAGE: View Configuration Setup");
			ARRProperties.viewConfigurationsSetup(driver, viewName);

			System.out.println("PRE PROCESSING STAGE: Resize Browser");
			resizeBrowser(driver);
			
			System.out.println("PRE PROCESSING STAGE: Setup Results");
			JSONObject results = new JSONObject();
			results.put("viewName", viewName);
			results.put("url", url);
			results.put("reportTitle", ARRProperties.REPORT_TITLE.getPropertyValue());
			results.put("xpathRoot", ARRProperties.XPATH_ROOT.getPropertyValue());

			System.out.println("PRE PROCESSING STAGE: Take View Screenshots");
			ScreenshotsProcessor sp = new ScreenshotsProcessor();
			takeViewScreenshots(driver, results, sp);

			System.out.println("PROCESSING STAGE: Running Custom Ruleset");
			runCustomRuleset(driver, results);

			System.out.println("PROCESSING STAGE: Running aXe Ruleset");
			runAXERuleset(driver, results);

			System.out.println("PROCESSING COMPLETE: Results:"+results);

			System.out.println("POST PROCESSING STAGE: Take Element Screenshots");
			takeElementScreenshots(driver, results, sp);

			WebDriverHolder.shutdownDriver();
			
			// Convert JSON to HTML
			System.out.println("POSTPROCESSING STAGE: Creating HTML Report");
			new JSONToHTMLConverter().convert(results);
			
		} catch (Exception ex) { // This should never be hit
			ex.printStackTrace();
			throw ex; // To make Jenkins job fail
		}
	}

	private void resizeBrowser(WebDriver driver) {
		String browserSize = ARRProperties.BROWSER_SIZE.getPropertyValueBasedOnView(driver);
		try {
			if (browserSize.equals("Default")) {

			} else if (browserSize.equals("Maximize")) {
				System.out.println("PRE PROCESSING STAGE: Browser Maximize");
				driver.manage().window().maximize();
			} else {
				System.out.println("PRE PROCESSING STAGE: Browser Resize");
				String values[] = browserSize.split("x");
				Integer windowWidth = Integer.parseInt(values[0].trim());
				Integer windowHeight = Integer.parseInt(values[1].trim());
				driver.manage().window().setPosition(new Point(0, 0));
				driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void runCustomRuleset(WebDriver driver, JSONObject results) {
		String xpathRoot = results.getString("xpathRoot");
		
		// Run Custom Ruleset
		JSONArray customRulesToRun = new JSONArray();
		for (CustomRulesetRules rule : CustomRulesetRules.values()) {
			customRulesToRun.put(rule.getLongName());
		}
		JSONObject jsonParameters = new JSONObject();
		jsonParameters.put("rulesToRun", customRulesToRun);
		if (!xpathRoot.isEmpty()) {
			jsonParameters.put("XPATH_ROOT", xpathRoot);
		}

		String customRuleset = FileDownloader.downloadJS(CUSTOM_RULESET_LOCATION);
		String customResponse = (String) ((JavascriptExecutor) driver)
				.executeScript(customRuleset
						+ "return JSON.stringify(axs.Audit.run(" + jsonParameters.toString() + "));");
		results.put("custom", new JSONArray(customResponse)); // addCustomResponseToResult
//		System.out.println("ValidationRules: customResponse:" + customResponse);
	}

	private void runAXERuleset(WebDriver driver, JSONObject results) {
		// Run aXe Ruleset
		String aXeRulesToRun = "['area-alt','accesskeys','aria-allowed-attr','aria-required-attr','aria-required-children','aria-required-parent','aria-roles','aria-valid-attr-value','aria-valid-attr','audio-caption','blink','button-name','bypass','checkboxgroup','color-contrast','document-title','duplicate-id','empty-heading','heading-order','html-lang-valid','image-redundant-alt','input-image-alt','label','layout-table','link-name','marquee','meta-refresh','meta-viewport','meta-viewport-large','object-alt','radiogroup','scope-attr-valid','server-side-image-map','tabindex','table-duplicate-name','td-headers-attr','th-has-data-cells','valid-lang','video-caption','video-description']";

		String aXeRuleset = FileDownloader.downloadJS(AXE_RULESET_LOCATION);
		
		Map aXeResponse = (Map) ((JavascriptExecutor) driver)
				.executeAsyncScript(aXeRuleset
						+ "; sendResults = arguments[arguments.length - 1]; axe.run({runOnly: {type: 'rule', values: "+aXeRulesToRun+"}}, function (err, results) { sendResults(err || results); });");

		results.put("axe", new JSONObject(aXeResponse));
//		System.out.println("ValidationRules: aXeResponse:" + aXeResponse);
	}

	private void takeViewScreenshots(WebDriver driver, JSONObject results, ScreenshotsProcessor sp)  throws Exception {
		String xpathRoot = results.getString("xpathRoot");
		
		if (!xpathRoot.isEmpty()) {
			System.out.println("PRE PROCESSING STAGE: Page Screenshot Scroll to Root...");
			WebElement rootElement = driver.findElements(By.xpath(xpathRoot)).get(0);
			WebElement htmlElement = driver.findElements(By.xpath("//html")).get(0);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rootElement);
			Thread.sleep(3000);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", htmlElement);
		}
		
		String viewName = results.getString("viewName");

		new File("output").mkdirs();

		try { // View Image
			String imageName = viewName.replaceAll(" ", "_") + "_VIEW.jpg";
			sp.createSnapshotForView(driver, "output/" + imageName);
			results.put("viewImage", imageName); // URLS on different threads were sharing screenshots, so have it in results
			System.out.println("ViewImage: " + imageName);
		} catch (Exception ex) {}
		
		if (!xpathRoot.isEmpty()) {
			WebElement rootElement = driver.findElements(By.xpath(xpathRoot)).get(0);
			ScreenShotElementRectangle sser = new ScreenShotElementRectangle(rootElement.getLocation(), rootElement.getSize());

			try { // XPATH ROOT Image
				String imageName = viewName.replaceAll(" ", "_")+ "_ROOT_.jpg";
				sp.createSnapshot(sser, "output/" + imageName);
				results.put("xpathImage", imageName); // URLS on different threads were sharing screenshots, so have it in results
				System.out.println("RootElementImage: " + imageName);
			} catch (Exception ex) {}
		}
	}
	
	private void takeElementScreenshots(WebDriver driver, JSONObject results,
			ScreenshotsProcessor sp) {
		String viewName = results.getString("viewName");
		Integer imageCounter = 1;

		JSONArray customArray = results.getJSONArray("custom");
		for (int i=0; i<customArray.length(); i++) {
			JSONObject customRule = customArray.getJSONObject(i);
			JSONArray failedElements = customRule.getJSONArray("elements");
			for(int j=0; j<failedElements.length(); j++) {
				JSONObject element = failedElements.getJSONObject(j);
				if(element.has("elementXLeft")
						&& element.has("elementYTop")
						&& element.has("elementXRight")
						&& element.has("elementYBottom")) {
					int xLeft = element.getInt("elementXLeft");
					int yTop = element.getInt("elementYTop");
					int xRight = element.getInt("elementXRight");
					int yBottom = element.getInt("elementYBottom");
					ScreenShotElementRectangle sser = new ScreenShotElementRectangle(xLeft, yTop, xRight, yBottom);
					
					try { // Failed Element Image
						String imageName = viewName.replaceAll(" ", "_")+"_CUSTOM_ELEMENT_"+imageCounter+"_.jpg";
						sp.createSnapshot(sser, "output/" + imageName);
						element.put("elementImage", imageName); // URLS on different threads were sharing screenshots, so have it in element
						System.out.println("FailedElementImage: " + imageName);
						
						imageCounter++;
					} catch (Exception ex) {}
				}
			}
		}

		JSONObject axeResults = results.getJSONObject("axe");
		JSONArray violations = axeResults.getJSONArray("violations");
		for (int i=0; i<violations.length(); i++) {
			JSONObject axeRule = violations.getJSONObject(i);
			JSONArray nodes = axeRule.getJSONArray("nodes");
			for (int j=0; j<nodes.length(); j++) {
				JSONObject node = nodes.getJSONObject(j);
				
				// TODO Modify AXE Ruleset to include xLeft, yTop, xRight, yBottom
				// OR Post process Failures to find these values
//				int xLeft = element.getInt("elementXLeft");
//				int yTop = element.getInt("elementYTop");
//				int xRight = element.getInt("elementXRight");
//				int yBottom = element.getInt("elementYBottom");
//				ScreenShotElementRectangle sser = new ScreenShotElementRectangle(xLeft, yTop, xRight, yBottom);
//				
//				try { // Failed Element Image
//					String imageName = viewName.replaceAll(" ", "_")+"_AXE_ELEMENT_"+imageCounter+"_.jpg";
//					sp.createSnapshot(sser, "output/" + imageName);
//					node.put("elementImage", imageName); // URLS on different threads were sharing screenshots, so have it in element
//					System.out.println("FailedElementImage: " + imageName);
//					
//					imageCounter++;
//				} catch (Exception ex) {}
			}
		}
	}
}
