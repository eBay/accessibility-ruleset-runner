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

<b>Note:</b> If you are working from a forked repository, you might use slightly different commands than those given above.  Also, if you have already downloaded the code, you can skip step.

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