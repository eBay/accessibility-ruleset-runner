# Tests
Tests are run using a Selenium/Mocha/Chai framework:

<ul>
<li>selenium-webdriver</li>
<li>mocha</li>
<li>chai</li>
</ul>

## Prerequisites

We assume the following are installed:
<ul>
<li><a href='http://chromedriver.chromium.org/'>ChromeDriver</a></li>
<li><a href='https://nodejs.org/en/'>Node.js</a></li>
</ul>

### Verify Prerequisites

To check if <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> and <a href='https://nodejs.org/en/'>Node.js</a> have already been installed, type the appropriate commands to print the version.

```sh
$ chromedriver --version
ChromeDriver 2.40.565498 (ea082db3280dd6843ebfb08a625e3eb905c4f5ab)

$ node --version
v10.15.3

$ npm --version
6.4.1
```

<b>Note:</b> Versions may vary.

If the prerequisite has not been installed, first use the information below to install, then run the appropriate commands to print the version (ie verify the installation by running the commands above).

### ChromeDriver

<a href='https://www.seleniumhq.org/'>Selenium</a> is a tool that automates browsers and <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is the Chrome implementation of WebDriver, which is an open source tool for automated testing of webapps across many browsers.

See <a href='../../topics/CHROMEDRIVERHELP.md'>Chrome Driver Help</a> for more information about installation.

<b>Note:</b> Make sure the "$HOME/bin" directory exists, it is on the system path and that <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is placed within the directory.

### Node.js

<a href='https://nodejs.org/en/'>Node.js</a> is a JavaScript runtime built on Chrome's V8 JavaScript engine.  Visit the <a href='https://nodejs.org/en/download/'>Node.js Downloads</a> for various intallers.

<b>Note:</b> Windows users are recommended to use the Git Bash console to run Node.js.  An Installer may be found on the <a href='https://git-scm.com/download'>Git Downloads</a>.

Node Version Manager (NVM) is a tool that allows users to swtich between different versions of Node.js.

<ul>
<li>Mac/Unix users may install NVM by following the steps listed on the <a href='https://github.com/nvm-sh/nvm'>NVM Repository</a>.</li>
<li>Windows users may download an NVM installer from <a href='https://github.com/coreybutler/nvm-windows'>NVM Windows Repository</a> (ie <a href='https://github.com/coreybutler/nvm-windows/releases/download/1.1.7/nvm-setup.zip'>NVM Windows 1.1.7</a>).</li>
</ul>

## Running Verification Tests to Test the Custom Ruleset

The verification tests for the custom ruleset are based on five main categories as listed in the description of the <a href="../../rulesets#custom-ruleset">Custom Ruleset</a>.

### Step 0: Download Code and Change the Directory

Run the following commands:

```sh
git clone https://github.com/eBay/accessibility-ruleset-runner/
cd accessibility-ruleset-runner/rulesets/tests
```

<b>Note:</b> If you are working from a forked repository, you might use slightly different commands than those given above.  Also, if you have already downloaded the code, you can skip step.

### Step 1: Install Package Dependencies

To install dependencies (from the <a href='https://registry.npmjs.org/'>Public NPM Registry</a>), run the following command from the rulesets/tests directory:

```sh
npm install
```

### Step 2: Run Tests

To run the tests, run the following command from the rulesets/tests directory:

```sh
npm run custom.ruleset.verification.tests
```

The output should match the <a href='output/custom.ruleset.verification.tests.output.txt'>Custom Ruleset Verification Tests Output</a>.

## Testing Methodology

The custom ruleset is vetted against a library of good/bad html code snippets as discussed in <a href='../../README.md#creating-a-ruleset'>Creating a Ruleset</a>.

### Test Library

Creating and modifying rules requires careful thought into how variations of code should be treated.  Variations of code can be added to a test library (ie the <a href='input/README.md'>Custom Ruleset Test Library</a>), providing several use cases to verify the rule is working as expected.  This becomes the foundation for which additional use cases can be added as they are discovered.

### Treatment of Incorrectly Classified Code Snippets

Sometimes the ruleset will not classify a specific code variation correctly.  We have the following options:

<ul>
<li>Add a new use case and evolve the ruleset to handle the new use case along with the old use cases</li>
<li>Change our thinking about some old use cases and evolve the ruleset appropriately</li>
<li>Throw out the rule due to complexity and the discovery of false positives</li>
<li>Create an exemption to handle the use case</li>
</ul>

As the number of use cases increases, the complexity of the ruleset also increases.  There becomes a point where a developer simply cannot handle all the use cases without the use of a test bed.

### Exemptions

In some cases, a ruleset may behave as expected but an organization may decide to make an exemption for certain types of code snippets.  This is handled through exemptions.

For example, in the case of ambiguous links, a library website may have two book titles which are exactly the same but lead to different checkout pages, one for each book.  In this case, the library may determine that links may remain ambiguous and require the user to navigate to discover the context of each link (ie author, price, year, etc).  Solutions to append additional information (ie the author) may become too verbose or may not even work (ie appending the author will not work if the authors are also the same).

Another common use case are websites that provide user entered content.

The ruleset should flag exempted use case as a failure within the results.  The results may then be post processed to treat failures which match certain patterns (ie adding a class like 'bookTitle' or 'userContent').  Here are a few options:

<ul>
<li>Remove the exemption from the failed set</li>
<li>Add the exemption to the passed set</li>
<li>Track the exemption in an exempted set</li>
</ul>
