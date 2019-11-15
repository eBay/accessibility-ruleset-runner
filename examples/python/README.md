# Python with Selenium Examples
These examples show how to run the rulesets using Selenium Webdriver.

## Prerequisites

We assume the following are installed:

<ul>
<li><a href='http://chromedriver.chromium.org/'>ChromeDriver</a></li>
<li><a href='https://www.python.org/downloads/release/python-2716/'>Python 2.7.16</a></li>
</ul>

### Verify Prerequisites

To check if <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> and <a href='https://www.python.org/downloads/release/python-2716/'>Python 2.7.16</a> have already been installed, type the appropriate commands to print the version.

```sh
$ chromedriver --version
ChromeDriver 2.40.565498 (ea082db3280dd6843ebfb08a625e3eb905c4f5ab)

$ python --version
Python 2.7.16

$ pip --version
pip 18.1 from c:\python27\lib\site-packages\pip (python 2.7)
```

<b>Note:</b> Python 2.7 is required but other than that, versions may vary.

If the prerequisite has not been installed, first use the information below to install, then run the appropriate commands to print the version (ie verify the installation by running the commands above).

### ChromeDriver

<a href='https://www.seleniumhq.org/'>Selenium</a> is a tool that automates browsers and <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is the Chrome implementation of WebDriver, which is an open source tool for automated testing of webapps across many browsers.

See <a href='../../topics/CHROMEDRIVERHELP.md'>Chrome Driver Help</a> for more information about installation.

### Python 2.7.16

<a href='https://www.python.org/doc/essays/blurb/'>Python</a> is an interpreted high-level programming language.  Its built in data structures and dynamic typing make it attractive for rapid development and scripting.  Visit the <a href='https://www.python.org/downloads/release/python-2716/'>Python 2.7.16</a> Release for various installers.

<b>Note:</b> Windows users are recommended to use the Git Bash console to run Node.js.  An Installer may be found on the <a href='https://git-scm.com/download'>Git Downloads</a>.

<b>Note:</b> Windows users may need to add the location of Python (ie C:\Python27) to the Path environment variable.

<a href='https://pypi.org/project/pip/'>pip</a> is the package installer for Python.  pip comes preinstalled for Python versions above Python 2.7.9.  If you need to install pip manually, you may visit the <a href='https://pip.pypa.io/en/stable/installing/'>pip installation page</a>.

<b>Note:</b> Windows users may need to add the location of pip (ie C:\Python27\Scripts) to the Path environment variable.

## Running Rulesets Against a Website

### Step 0: Download Code and Change the Directory

Run the following commands:

```sh
git clone https://github.com/eBay/accessibility-ruleset-runner/
cd accessibility-ruleset-runner/examples/python
```

<b>Note:</b> If you are running examples from a forked repository, you might use slightly different commands than those given above.  Also, if you have already downloaded the code, you do not need to run this step.

### Step 1: Install Package Dependencies

To install the selenium dependency, run the following command from the examples/python directory:

```sh
pip install selenium
```

To verify selenium (ie version 3.141.0) and urllib3 (ie version 1.25.3) were installed successfully, run the following command from the examples/python directory:

```sh
pip freeze
```

### Step 2: Invoke the Ruleset Runners

#### Custom Ruleset

To run the <a href="rulesets#custom-ruleset">Custom Ruleset</a>, run the following command from the examples/python directory:

```sh
python custom.ruleset.runner.py
```

The output should match the <a href='output/eBay.custom.ruleset.runner.output.txt'>Custom Ruleset Runner Output</a>.

#### aXe Ruleset

To run the <a href="rulesets#axe-ruleset">aXe Ruleset</a>, run the following command from the examples/python directory:

```sh
python aXe.ruleset.runner.py
```

The output should match the <a href='output/eBay.aXe.ruleset.runner.output.txt'>aXe Ruleset Runner Output</a>.

## Modifications

### Test Another Website

The examples are setup to be run without any configuration necessary.  However, users can test a different url by modifying the following line:

```sh
driver.get("https://www.ebay.com")
```

In addition, sometimes users need to sign in, load urls, click buttons, etc before testing a view.  Consider making the appopriate modifications necessary for your use case.

## More Sample Code and Output

### All Ruleset Runner

The all.ruleset.runner.py file has 4 tests to demonstrate different injections for accessibility ruleset runner. You can run those individually as follows:

```sh
python all.ruleset.runner.py AllRulesetSeleniumTest.test_ebay_accesibility_axe_ruleset
```

#### Custom Ruleset

The following commands are used to run the custom rulset and output the results:

```sh
self.inject_accessiblity_ruleset_runner(mode='Custom')
```

The output should match the <a href='output/SeleniumPythonExample.custom.ruleset.runner.output.txt'>custom Ruleset Runner Output</a>.

To run the custom ruleset on one particular element, modify the following command appropriately:
```sh
self.inject_accessiblity_ruleset_runner(mode='Custom', customElement='{\"XPATH_ROOT\":\"//input[@id=\'gh-ac\']\"}')
```
<b>Note:</b> This will ignore page requirements like title, h1, skip to main content, etc

#### aXe Ruleset

The aXe ruleset runner creates an array of which rules to run (each rule included here is associated with a <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> AA guideline).

You can specify the rules to run as follows:

```
Supported Rules = ['area-alt','accesskeys','aria-allowed-attr','aria-required-attr','aria-required-children','aria-required-parent','aria-roles','aria-valid-attr-value','aria-valid-attr','audio-caption','blink','button-name','bypass','checkboxgroup','color-contrast','document-title','duplicate-id','empty-heading','heading-order','href-no-hash','html-lang-valid','image-redundant-alt','input-image-alt','label','layout-table','link-name','marquee','meta-refresh','meta-viewport','meta-viewport-large','object-alt','radiogroup','scopr-attr-valid','server-side-image-map','tabindex','table-duplicate-name','td-headers-attr','th-has-data-cells','valid-lang','video-caption','video-description']
```

```sh
rules_to_run = '[\'area-alt\']'
self.inject_accessiblity_ruleset_runner(mode='Axe', rules_to_run=rules_to_run)
```

The output should match the <a href='output/SeleniumPythonExample.axe.ruleset.runner.output.txt'>aXe Ruleset Runner Output</a>.

#### All Ruleset - Default

You can run both the axe and custom ruleset by default by calling the method below

```sh
self.inject_accessiblity_ruleset_runner()
```

The output should match the <a href='output/SeleniumPythonExample.all.ruleset.runner.output.txt'>All Ruleset Runner Output</a>.

