# Python with Selenium Examples
These examples show how to run the rulesets using Selenium Webdriver.

## Pre-Requisites:

We assume the following are installed:

<ul>
<li><a href='https://www.python.org/downloads/release/python-2716/'>Python 2.7.16</a></li>
<li><a href='http://chromedriver.chromium.org/'>ChromeDriver</a></li>
</ul>

### Python 2.7.16

<a href='https://www.python.org/doc/essays/blurb/'>Python</a> is an interpreted high-level programming language.  Its built in data structures and dynamic typing make it attractive for rapid development and scripting.  Visit the <a href='https://www.python.org/downloads/release/python-2716/'>Python 2.7.16</a> Release for various installers.

<b>Note:</b> Windows users may need to add the location of Python (ie C:\Python27) to the Path environment variable.

<a href='https://pypi.org/project/pip/'>pip</a> is the package installer for Python.  pip comes preinstalled for Python versions above Python 2.7.9.  If you need to install pip manually, you may visit the <a href='https://pip.pypa.io/en/stable/installing/'>pip installation page</a>.

<b>Note:</b> Windows users may need to add the location of pip (ie C:\Python27\Scripts) to the Path environment variable.

### ChromeDriver

<a href='https://www.seleniumhq.org/'>Selenium</a> is a tool that automates browsers and <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is the Chrome implementation of WebDriver, which is an open source tool for automated testing of webapps across many browsers.

The appropriate version of <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> needs to be downloaded individually.  The ChromeDriver version must be compatible with the Operating System (Mac/Unix/Windows) and the version of Chrome.

For example, when using version 73.0.3683.103 of Chrome, users may download the appopriate version of ChromeDriver (Mac/Unix/Windows) from <a href='https://chromedriver.storage.googleapis.com/index.html?path=73.0.3683.68/'>ChromeDriver 73.0.3683.68</a>.

## Running Ruleset Runners To Test a Website

### Step 1: Install Package Dependencies

You can install Selenium by executing the following command in the terminal.

```sh
pip install selenium
```

Run the following command to verify selenium (ie version 3.141.0) and urllib3 (ie version 1.25.3) were installed successfully.

```sh
pip freeze
```

### Step 2: Invoke the Ruleset Runners

#### Custom Ruleset

To invoke the custom ruleset runner, use the following command:

```sh
python custom.ruleset.runner.py
```

The output should match the <a href='output/eBay.custom.ruleset.runner.output.txt'>Custom Ruleset Runner Output</a>.

#### aXe Ruleset

To invoke the aXe ruleset runner, use the following command:

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

