# Chrome Extension Examples
These examples show how to run the rulesets using a Chrome Extension.

## Prerequisites
We assume the Chrome Extension is installed.  Visit the <a href='https://chrome.google.com/webstore/detail/accessibility-ruleset-run/cfakapffmdipciggkeefnpjjbobbnefi'>Accessibility Ruleset Runner Chrome Extension</a> in the <a href='https://chrome.google.com/webstore'>Chrome Web Store</a> and click "Add to Chrome" to install.

The Chrome Extension can also be manually installed.  First, download the Github code.  Then, follow the instructions listed in <a href='README.md#chrome-extension-development'>Chrome Extension Development</a>.

## Running Rulesets Against a Website

### Step 1: View Setup
Open the Google Chrome browser.  Navigate to the url you wish to test.  For example, go to https://www.google.com.

### Step 2: Open the Chrome Developer Console

Follow these steps:

<ul>
<li>Right click, select Inspect.</li>
<li>Click on the Console Tab.</li>
</ul>

### Step 3: Invoke the Ruleset Runner

The following steps are used to run the <a href='../../rulesets'>Rulesets</a>:

<ul>
<li>Right click, select "Accessibility", select "Ruleset Runner", select "Add Overlays".</li>
<li>In the Ruleset Runner Dialog, click Run.</li>
</ul>

The output should match the <a href='output/ChromeExtension.ruleset.runner.output.txt'>Ruleset Runner Output</a>.

## Modifications

### Include in Your Project

The Chrome Extension follows the <a href='../../topics/GENERALSTEPSFORRUNNINGRULESETS.md'>General Steps for Running Rulesets</a>.  For each of these steps, we add some additional comments.

#### Configure Parameters

The Ruleset Runner Dialog can be modified to include additional parameters.  It includes some default code to highlight the fact that some parameters values might change based on other parameter values.  For instance, the report location might automatically be updated based on the Report Title or JIRA Project Name.

#### Run Rulesets

Results from the <a href="rulesets#custom-ruleset">Custom Ruleset</a> and the <a href="rulesets#axe-ruleset">aXe Ruleset</a> are combined into a single JSON object.  Here, the aXe results are filtered in preparation for a POST call.  This step can be modified to change how the rulesets are executed.

#### Process Results

Some default code is included to highlight various element failures.  The default code will add an overlay along with a red outline for elements that are visible.  This code can be expanded with additional features.  For instance, one might add links to rule descriptions, <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> guidelines or even other reports.

To remove the Accessibility Ruleset Runner overlays, right click, select "Accessibility", select "Ruleset Runner", select "Remove Overlays".

Also, some default code is included to demonstrate how to upload the results to a server via a POST call.

## Additional Information

### Firefox Extension

A Firefox Extension can be created by modifying the manifest file.  In the manifest file, modify the JSON to add an "applications" object as a sibling to the "background" and "icons" objects.  Include an appropriate email address for the Firefox Extension.

```sh
,
  "applications": {
    "gecko": {
      "id": "sizu@ebay.com",
      "strict_min_version": "42.0"
    }
  }
```

### Chrome Extension Development

Chrome Extensions may be actively developed and then manually installed by:

<ul>
<li>Open the Google Chrome Browser</li>
<li>Navigate to chrome://extensions</li>
<li>With Developer mode selected, click Load unpacked</li>
<li>Navigate to the src folder which should contain the manifest.json file</li>
<li>Click select folder</li>
</ul>

When a Chrome Extension is ready to be published:

<ul>
<li>Select the content and manifest.json files</li>
<li>Create a zip file containing these two files (in Windows, right click, Send to, Compressed zipped folder)</li>
<li>Upload this zip file using the <a href='https://chrome.google.com/webstore/developer/dashboard'>Chrome Web Store Developer Dashboard</a></li>
</ul>

### Headings Parser

The chrome extension also features a Headings Parser to help build consistency in the page layout across multiple webpages.  Here are some things to consider requiring for standardization:

<ul>
<li>title</li>
<li>language attribute</li>
<li>H1</li>
<li>header</li>
<li>footer</li>
<li>Skip to main content anchor</li>
<li>Additional landmarks and headings</li>
</ul>

To run the Headings Parser, right click, select "Accessibility", select "Headings Parser", select "Add Overlays".

To remove the Headings Parser overlays, right click, select "Accessibility", select "Headings Parser", select "Remove Overlays".

### MIND Patterns

The Ruleset Runner Dialog was created using the <a href='https://ebay.gitbook.io/mindpatterns/'>MIND Patterns</a>.  More specifically, several patterns are used:

<ul>
<li><a href='https://ebay.gitbook.io/mindpatterns/disclosure/dialog'>Dialog</a></li>
<li><a href='https://ebay.gitbook.io/mindpatterns/input/autocomplete'>Autocomplete</a></li>
<li><a href='https://ebay.gitbook.io/mindpatterns/disclosure/infotip'>Infotip</a></li>
</ul>
