# NodeJS with Selenium/Mocha/Chai Examples
These examples show how to run the rulesets using a Selenium/Mocha/Chai framework:

<ul>
<li>selenium-webdriver</li>
<li>mocha</li>
<li>chai</li>
</ul>

## Prerequisites
We assume the following are installed:
<ul>
<li><a href='https://nodejs.org/en/'>Node.js</a></li>
<li><a href='http://chromedriver.chromium.org/'>ChromeDriver</a></li>
</ul>

### Node.js

<a href='https://nodejs.org/en/'>Node.js</a> is a JavaScript runtime built on Chrome's V8 JavaScript engine.  Visit the <a href='https://nodejs.org/en/download/'>Node.js Downloads</a> for various intallers.

<b>Note:</b> Windows users are recommended to use the Git Bash console to run Node.js.  An Installer may be found on the <a href='https://git-scm.com/download'>Git Downloads</a>.

Node Version Manager (NVM) is a tool that allows users to swtich between different versions of Node.js.

<ul>
<li>Mac/Unix users may install NVM by following the steps listed on the <a href='https://github.com/nvm-sh/nvm'>NVM Repository</a>.</li>
<li>Windows users may download an NVM installer from <a href='https://github.com/coreybutler/nvm-windows'>NVM Windows Repository</a> (ie <a href='https://github.com/coreybutler/nvm-windows/releases/download/1.1.7/nvm-setup.zip'>NVM Windows 1.1.7</a>).</li>
</ul>

### ChromeDriver

<a href='https://www.seleniumhq.org/'>Selenium</a> is a tool that automates browsers and <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is the Chrome implementation of WebDriver, which is an open source tool for automated testing of webapps across many browsers.

The appropriate version of <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> needs to be downloaded individually.  The ChromeDriver version must be compatible with the Operating System (Mac/Unix/Windows) and the version of Chrome.

For example, when using version 73.0.3683.103 of Chrome, users may download the appopriate version of ChromeDriver (Mac/Unix/Windows) from <a href='https://chromedriver.storage.googleapis.com/index.html?path=73.0.3683.68/'>ChromeDriver 73.0.3683.68</a>.

## Running Ruleset Examples

These steps show how to run the code, after it has been downloaded.

### Step 1: Install Package Dependencies

To install these node modules, from the accessibility-ruleset-runner/examples/nodejs folder, run:

```sh
npm install
```

### Step 2: Invoke Ruleset Runners

#### Custom Ruleset

To invoke the custom ruleset runner, use the following command:

```sh
npm run custom.ruleset.runner
```

The output should match the <a href='output/Google.custom.ruleset.runner.output.txt'>Custom Ruleset Runner Output</a>.

#### aXe Ruleset

To invoke the aXe rulset runner, use the following command:

```sh
npm run aXe.ruleset.runner
```

The output should match the <a href='output/Google.aXe.ruleset.runner.output.txt'>aXe Ruleset Runner Output</a>.

## Modifications

### Test Another Website

The examples are setup to be run without any configuration necessary.  However, users can test a different url by modifying the following line:

```sh
var url = "http://www.google.com";
```

In addition, sometimes users need to sign in, load urls, click buttons, etc before testing a view.  Consider making the appopriate modifications necessary for your use case.

### Include in Your Project

These steps show how to use the published NPM module in your project.

1.  Install accessibility-ruleset-runner to bring in the javascript files for the custom/axe rulesets.
```sh
npm install accessibility-ruleset-runner --save-dev
```

2. Edit the custom.ruleset.runner.js and axe.ruleset.runner.js to point to the installed custom.ruleset.X.X.X.js and axe.ruleset.X.X.X.js javascript files (replace X's appropriately)
```sh
var customRuleset = fs.readFileSync('./node_modules/accessibility-ruleset-runner/rulesets/custom.ruleset.X.X.X.js','utf8');
var aXeRuleset = fs.readFileSync('./node_modules/accessibility-ruleset-runner/rulesets/aXe.ruleset.X.X.X.js','utf8');
```

3. Copy the custom.ruleset.runner.js and axe.ruleset.runner.js into a folder within your project

4.  Configure the scripts section of the package.json to include the runner (replace src/XXX with the folder used in Step 3)
```sh
  "scripts": {
    ...
    "custom.ruleset.runner": "mocha src/XXX/custom.ruleset.runner.js",
    "aXe.ruleset.runner": "mocha src/XXX/aXe.ruleset.runner.js",
  },
```



