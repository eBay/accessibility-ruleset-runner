# Tests
Tests are run using a Selenium/Mocha/Chai framework:

<ul>
<li>selenium-webdriver</li>
<li>mocha</li>
<li>chai</li>
</ul>

## Prerequisites

See the <a href='../examples/nodejs/README.md#prerequisites'>prerequisites</a>.

## Running Tests for the Custom Ruleset

The custom ruleset is tested against sample html files which contain examples of good/bad html elements.  Each rule written requires careful thought into how various use cases should be treated.  When new use cases are discovered, they can be added to the sample html files to test rule enhancements.  When new rules are introduced, sample html files and tests should be added as well.

```sh
npm install
npm run custom.ruleset.test
```