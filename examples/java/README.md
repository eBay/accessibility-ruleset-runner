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

### Verify Prerequisites

To check if <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> and <a href='https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html'>Java 8</a> have already been installed, type the appropriate commands to print the version.

```sh
$ chromedriver --version
ChromeDriver 2.40.565498 (ea082db3280dd6843ebfb08a625e3eb905c4f5ab)

$ java -version
java version "1.8.0_144"
Java(TM) SE Runtime Environment (build 1.8.0_144-b01)
Java HotSpot(TM) 64-Bit Server VM (build 25.144-b01, mixed mode)

$ mvn --version
Apache Maven 3.1.1 (0728685237757ffbf44136acec0402957f723d9a; 2013-09-17 08:22:22-0700)
Maven home: C:\apache-maven-3.1.1
Java version: 1.8.0_144, vendor: Oracle Corporation
Java home: C:\Program Files\Java\jdk1.8.0_144\jre
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "dos"
```

<b>Note:</b> Java 8 and Maven 3 are required but other than that, versions may vary.

If the prerequisite has not been installed, first use the information below to install, then run the appropriate commands to print the version (ie verify the installation by running the commands above).

### ChromeDriver

<a href='https://www.seleniumhq.org/'>Selenium</a> is a tool that automates browsers and <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is the Chrome implementation of WebDriver, which is an open source tool for automated testing of webapps across many browsers.

See <a href='../../topics/CHROMEDRIVERHELP.md'>Chrome Driver Help</a> for more information about installation.

<b>Note:</b> Make sure the "$HOME/bin" directory exists, it is on the system path and that <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is placed within the directory.

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

## Running Rulesets Against a Website

### Step 0: Download Code and Change the Directory

Run the following commands:

```sh
git clone https://github.com/eBay/accessibility-ruleset-runner/
cd accessibility-ruleset-runner/examples/java
```

<b>Note:</b> If you are working from a forked repository, you might use slightly different commands than those given above.  Also, if you have already downloaded the code, you can skip step.

### Step 1: Install Package Dependencies

To install dependencies, run the following command from the examples/java directory:

```sh
mvn install -e -DskipTests=true -s settings_apache_maven_repo.xml
```

### Step 2: Invoke the Ruleset Runner

To run the <a href='../../rulesets'>Rulesets</a>, run the following command from the examples/java directory:

```sh
mvn test -e -Dtest=arr.AccessibilityRulesetRunnerTest -s settings_apache_maven_repo.xml
```

The output should match the <a href='output/HomePage_URLS_TO_TEST_was_not_set.ruleset.runner.output.txt'>Ruleset Runner Output</a> and an <a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/examples/java/output/HomePage_URLS_TO_TEST_was_not_set_ARR_Report.html'>HTML Report</a> should be created.

The above command explicitly defines the location of the settings file and test but the following command should also work:

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

Results from the <a href="rulesets#custom-ruleset">Custom Ruleset</a> and the <a href="rulesets#axe-ruleset">aXe Ruleset</a> are combined into a single JSON object.  This step can be modified to change how the rulesets are executed.

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




