# NodeJS with Selenium/Mocha/Chai Examples
These examples show how to run the rulesets using a Selenium/Mocha/Chai framework:

<ul>
<li>selenium-webdriver</li>
<li>mocha</li>
<li>chai</li>
</ul>

## Prerequisites

See the <a href='../../rulesets/tests/README.md#prerequisites'>prerequisites</a>.

## Running Rulesets Against a Website

### Step 0: Download Code and Change the Directory

Run the following commands:

```sh
git clone https://github.com/eBay/accessibility-ruleset-runner/
cd accessibility-ruleset-runner/examples/nodejs
```

<b>Note:</b> If you are working from a forked repository, you might use slightly different commands than those given above.  Also, if you have already downloaded the code, you can skip step.

### Step 1: Install Package Dependencies

To install dependencies, run the following command from the examples/nodejs directory:

```sh
npm install
```

### Step 2: Invoke the Ruleset Runners

#### Custom Ruleset

To run the <a href="rulesets#custom-ruleset">Custom Ruleset</a>, run the following command from the examples/nodejs directory:

```sh
npm run custom.ruleset.runner
```

The output should match the <a href='output/Google.custom.ruleset.runner.output.txt'>Custom Ruleset Runner Output</a>.

#### aXe Ruleset

To run the <a href="rulesets#axe-ruleset">aXe Ruleset</a>, run the following command from the examples/nodejs directory:

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
npm install @ebay/accessibility-ruleset-runner --save-dev
```

<b>Note:</b> You may modify the version by editing the package.json file.

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



