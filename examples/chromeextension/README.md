# Chrome Extension Examples
These examples show how to run the rulesets using a Chrome Extension.

## Prerequisites
We assume the Chrome Extension is installed.  Visit the <a href='https://chrome.google.com/webstore'>Chrome Web Store</a>, to download the Accessibility Ruleset Runner Chrome Extension.

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

Follow these steps:

<ul>
<li>Right click, select "Accessibility", select "Ruleset Runner", select "Add Overlays".</li>
<li>On the Ruleset Runner Overlay, click Run.</li>
</ul>

The output should match the <a href='output/ChromeExtension.ruleset.runner.output.txt'>Custom Ruleset Runner Output</a>.

## Modifications

### Include in Your Project

The Chrome Extension follows these steps:
<ol>
<li>Configure Parameters</li>
<li>Run Rulesets</li>
<li>Highlight Element Failures</li>
<li>Upload Results</li>
</ol>

#### Configure Parameters

The Ruleset Runner Overlay can be modified to include additional parameters.  However, some base parameters might be useful:

<ul>
<li><b>View Name:</b> A name should be given to the view that was setup.  This name should be specific and descriptive enough to help scale (ie working with hundreds of views).  See below for the definition of a view.</li>
<li><b>JIRA Project Name:</b> Many companies use JIRA and bugs can be filed appropriately using this parameter.</li>
<li><b>Report Title:</b> A report title is helpful if a report will be created with the results.  In some case, multiple views might be aggregated into a single report to help make the results more digestable.</li>
</ul>

A View is a set of webpages that all share certain characteristics.  For example, many companies have a search capability for items to purchase.  A View might be defined based on this search results page including the following characteristics: H1 contains Laptop, at least 5 items appear in the search results section, the header/footer landmark are present, a nav for the breadcrumb and left navigation are present, etc.  Notice that a particular view may not have the same exact html code each time it is loaded.

#### Highlight Element Failures

Some default code is included to highlight various element failures.  The default code will add an overlay along with a red outline for elements that are visible.  This code can be expanded with additional features.  For instance, one might add links to rule descriptions, <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> guidelines or even other reports.

#### Upload Results

After the results are created, they can be uploaded to a server.  The server might store the results in a database for tracking and building various dashboards.  The server also might convert the JSON results to an HTML report and upload it to a central location for other people to view.

## Headings Parser

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