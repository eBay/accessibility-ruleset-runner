# Tests
Tests are run using a Selenium/Mocha/Chai framework:

<ul>
<li>selenium-webdriver</li>
<li>mocha</li>
<li>chai</li>
</ul>

## Prerequisites

See the <a href='../examples/nodejs/README.md#prerequisites'>prerequisites</a>.

## Running Verification Tests to Test the Custom Ruleset

These steps show how to run the tests, after the code has been downloaded.

### Step 1: Install Package Dependencies

To install these node modules, from the accessibility-ruleset-runner/tests folder, run:

```sh
npm install
```

### Step 2: Run Tests

To run the tests, use the following command:

```sh
npm run custom.ruleset.test
```

The output should match the <a href='output/custom.ruleset.test.output.txt'>Custom Ruleset Test Output</a>.

## Methodology

The cutsom ruleset is vetted against a library of good/bad html code snippets as discussed in <a href='../README.md#creating-a-ruleset'>Creating a Ruleset</a>.

Each rule written requires careful thought into how various use cases should be treated.  When new use cases are discovered, they can be added to the sample html files to test rule enhancements.  When new rules are introduced, sample html files and tests should be added as well.