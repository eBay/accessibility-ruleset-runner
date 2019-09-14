# Python Selenium Integration
These examples show how to inject the rulesets directly in selenium webdriver to run it.

## Pre-Requisites:
You will need Selenium and Python 2.7 to run this example. You can install Selenium by executing the following command in the terminal

```sh
pip install selenium
```

## Step 1: View Setup
Write a Selenium Program to Open the Google Chrome browser.  Navigate to the url you wish to test.  For example, go to https://www.ebay.com. As an example, you can download the sample waeCustomRuleset project and run it.

Download the project and run the test as follows:

```sh
bash$>cd waeCustomRuleset
      python web_driver_test.py
```

The Project has 4 tests to demonstrate different injections for accessibility ruleset runner. You can run those individually as follows:

```sh
bash$>cd waeCustomRuleset
      python web_driver_test.py AxeRulesetSeleniumTest.test_ebay_accesibility_axe_ruleset
```

## Step 3: Execute the sample python script - Inject the Rulesets in Webdriver using Selenium's execute_driver method

You can inject the Rulesets in 3 ways:

### Custom Ruleset
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

### aXe Ruleset

The aXe ruleset runner creates an array of which rules to run (each rule included here is associated with a <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> AA guideline).

You can specify the rules to run as follows:

Supported Rules = ['area-alt','accesskeys','aria-allowed-attr','aria-required-attr','aria-required-children','aria-required-parent','aria-roles','aria-valid-attr-value','aria-valid-attr','audio-caption','blink','button-name','bypass','checkboxgroup','color-contrast','document-title','duplicate-id','empty-heading','heading-order','href-no-hash','html-lang-valid','image-redundant-alt','input-image-alt','label','layout-table','link-name','marquee','meta-refresh','meta-viewport','meta-viewport-large','object-alt','radiogroup','scopr-attr-valid','server-side-image-map','tabindex','table-duplicate-name','td-headers-attr','th-has-data-cells','valid-lang','video-caption','video-description']

```sh
rules_to_run = '[\'area-alt\']'
self.inject_accessiblity_ruleset_runner(mode='Axe', rules_to_run=rules_to_run)
```

The output should match the <a href='output/SeleniumPythonExample.axe.ruleset.runner.output.txt'>aXe Ruleset Runner Output</a>.

### All Ruleset - Default

You can run both the axe and custom ruleset by default by calling the method below

```sh
self.inject_accessiblity_ruleset_runner()
```

The output should match the <a href='output/SeleniumPythonExample.all.ruleset.runner.output.txt'>All Ruleset Runner Output</a>.



