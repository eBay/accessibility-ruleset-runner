# Java with Selenium/TestNG Examples
These examples show how to run the rulesets using a Selenium/TestNG framework:

<ul>
<li>selenium-java</li>
<li>testng</li>
</ul>

## Pre-Requisites:

We assume the following are installed:

<ul>
<li><a href='http://chromedriver.chromium.org/'>ChromeDriver</a></li>
<li><a href='https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html'>Java 8</a></li>
</ul>

### ChromeDriver

<a href='https://www.seleniumhq.org/'>Selenium</a> is a tool that automates browsers and <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is the Chrome implementation of WebDriver, which is an open source tool for automated testing of webapps across many browsers.

See <a href='../CHROMEDRIVERHELP.md'>Chrome Driver Help</a> for more information about installation.

### Java 8

<a href='https://docs.oracle.com/javase/8/docs/technotes/guides/language/index.html'>Java</a> is a general-purpose class-based object-oriented high-level programming language.  This example was created using the <a href='https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html'>Java 8 JDK</a>.

<a href='https://maven.apache.org/'>Maven</a> is a project management tool.  Mac/Unix/Windows users may install Maven by following the steps listed on the <a href='https://maven.apache.org/download.cgi'>Maven Download Page</a>.  This example was created using Maven 3.1.1.

<b>Note:</b> Windows users are recommended to use the Git Bash console to run <a href='https://maven.apache.org/'>Maven</a>.  An Installer may be found on the <a href='https://git-scm.com/download'>Git Downloads</a>.

<a href='https://maven.apache.org/'>Maven</a> is used to download the following libraries, which are needed to run this example:

<ul>
<li><a href='https://mvnrepository.com/artifact/org.testng/testng/6.8.8'>testng 6.8.8</a></li>
<li><a href='https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java/3.4.0'>selenium-java 3.4.0</a></li>
<li><a href='https://mvnrepository.com/artifact/org.json/json/20160810'>json 20160810</a></li>
</ul>

## Running Ruleset Runners To Test a Website

To invoke the ruleset runner, use the following command (in the examples/java folder):

```sh
mvn test -e -s settings_apache_maven_repo.xml -Dtest=arr.AccessibilityRulesetRunnerTest
```

The output should match the <a href='output/HomePage_URLS_TO_TEST_was_not_set.ruleset.runner.output.txt'>Ruleset Runner Output</a> and an <a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/examples/java/output/HomePage_URLS_TO_TEST_was_not_set_ARR_Report.html'>HTML Report</a> should be created.

Actually, the use of the settings file and location of the test to run are explicit and simply running the following should work.

```sh
mvn test
```

## Modifications

### Test Another Website

The examples are setup to be run without any configuration necessary.  However, users can test a different url by modifying the following line:

```sh
mvn test -e -DURLS_TO_TEST="[Google] http://www.google.com"
```

In addition, sometimes users need to sign in, load urls, click buttons, etc before testing a View.  Consider making the appopriate modifications necessary for your use case.

### Include in Your Project

The Test NG Test follows the steps outlined in <a href='../../topics/GENERALSTEPSFORRUNNINGRULESETS.md'>General Steps for Running Rulesets</a>.  For each of these steps, we add some additional comments.

#### Configure Parameters

Various parameters/properties were included to give examples of possible parameters that would be needed in a test environment.  In addition, code is included to ensure that default properties are overridden by system properties and Jenkins parameters.

#### Run Rulesets

Results from the Custom Ruleset and the aXe Ruleset are combined into a single JSON object.  This step can be modified to change how the rulesets are executed.

#### Process Results

This example includes a simple example of how to create an HTML report from the results JSON object.

## Additional Information

### Multiple Views

In practice, multiple Views are tested simultaneously using a <a href='#selenium-grid">Selenium Grid</a>.  To run multiple Views, try something like:

```sh
mvn test -e -DURLS_TO_TEST="[EBay] http://www.ebay.com, [PayPal] http://www.paypal.com"
```

Results from multiple Views may be also be combined into a single JSON object.  Then, an HTML report could be created which includes these Views, by modifying the JSONToHTMLConverter class and HTMLReportTemplate.html file appropriately.

### Integrated Development Environments

This <a href='https://maven.apache.org/'>Maven</a> project can be imported into an Integrated Development Environment (IDE) like <a href='https://www.eclipse.org/'>Eclipse</a>.

### Selenium Grid

Building a selenium grid provides a lot more capability in terms of testing.  You will see some additional utility files included (need modification to work) that help with the following:

<ul>
<li>Loading Jenkins Job, System and Default Properties</li>
<li>Configuring Chrome, Firefox and Remote Web Driver</li>
<li>Downloading ChromeDriver, GeckoDriver, InternetExplorerDriver</li>
</ul>

These utility files work in various environments:

<ul>
<li>Developer Desktop/Laptop</li>
<li>Jenkins Server</li>
<li>Selenium Grid</li>
</ul>




