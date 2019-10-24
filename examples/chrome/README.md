# Chrome Developer Console Examples
These examples show how to run the rulesets directly in the Chrome Developer Console.

## Running Rulesets Against a Website

### Step 1: View Setup
Open the Google Chrome browser.  Navigate to the url you wish to test.  For example, go to https://www.google.com.

### Step 2: Open the Chrome Developer Console

Follow these steps:

<ul>
<li>Right click, select Inspect.</li>
<li>Click on the Console Tab.</li>
</ul>

### Step 3: Copy and Paste the Javascript Files 

To run the (<a href='../../rulesets'>Rulesets</a>) directly within the browser console, we need to first copy over the appropriate code. For both rulesets (custom.ruleset.X.X.X.js and aXe.ruleset.X.X.X.js), do follow these steps:

<ul>
<li>Click Raw to get to the raw version of the file.</li>
<li>Copy the entire ruleset javascript file.</li>
<li>Paste the file directly into the browser console.</li>
<li>Hit Enter.</li>
</ul>

### Step 4: Invoke the Ruleset Methods

Run the commands as specified below.

#### Custom Ruleset
The following commands are used to run the <a href="rulesets#custom-ruleset">Custom Ruleset</a>:

```sh
var results = axs.Audit.run();
JSON.stringify(results);
```

The output should match the <a href='output/ChromeDeveloperConsole.custom.ruleset.runner.output.txt'>Custom Ruleset Runner Output</a>.

To run the custom ruleset on one particular element, modify the following command appropriately:
```sh
var results = axs.Audit.run({"XPATH_ROOT":"//div[@id='div_1.2']"});
```
<b>Note:</b> This will ignore page requirements like title, h1, skip to main content, etc

#### aXe Ruleset

The aXe ruleset runner creates an array of which rules to run (each rule included here is associated with a <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> AA guideline).

Assuming the aXe javascript file has already been copied/pasted into the browser console (see above), the following commands are used to run the <a href="rulesets#axe-ruleset">aXe Ruleset</a>:
```sh
var callback = function(results) {
  console.log(JSON.stringify(results));
}

var rulesToRun = ['area-alt','accesskeys','aria-allowed-attr','aria-required-attr','aria-required-children','aria-required-parent','aria-roles','aria-valid-attr-value','aria-valid-attr','audio-caption','blink','button-name','bypass','checkboxgroup','color-contrast','document-title','duplicate-id','empty-heading','heading-order','href-no-hash','html-lang-valid','image-redundant-alt','input-image-alt','label','layout-table','link-name','marquee','meta-refresh','meta-viewport','meta-viewport-large','object-alt','radiogroup','scopr-attr-valid','server-side-image-map','tabindex','table-duplicate-name','td-headers-attr','th-has-data-cells','valid-lang','video-caption','video-description'];

axe.a11yCheck(document, {runOnly: {type: "rule", values: rulesToRun }}, callback );
```

The output should match the <a href='output/ChromeDeveloperConsole.aXe.ruleset.runner.output.txt'>aXe Ruleset Runner Output</a>.



