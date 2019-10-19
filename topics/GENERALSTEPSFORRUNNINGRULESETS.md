# General Steps for Running Rulesets

Here, we include a more general discussion about what to consider when running rulesets in your project.  The general steps to run rulesets are:

<ol>
<li>Configure Parameters</li>
<li>View Setup</li>
<li>Run Rulesets</li>
<li>Process Results</li>
</ol>

## Configure Parameters

Having some base parameters might be useful:

<ul>
<li><b>View Name:</b> A name should be given to the view being tested.  This name should be specific and descriptive enough to help scale (ie working with hundreds of views).  See <a href='#view-setup'>View Setup</a> for the definition of a view.</li>
<li><b>JIRA Project Name:</b> Many companies use JIRA and bugs can be filed appropriately using this parameter.</li>
<li><b>Report Title:</b> A report title is helpful if a report will be created with the results.  In some case, multiple views might be aggregated into a single report to help make the results more digestable.</li>
</ul>

## View Setup

<b>View:</b> A View is a set of webpages that all share certain characteristics.  For example, many companies have a search capability for items to purchase.  A View might be defined based on this search results page including the following characteristics: H1 contains Laptop, at least 5 items appear in the search results section, the header/footer landmark are present, a nav for the breadcrumb and left navigation are present, etc.  Notice that a particular view may not have the same exact html code each time it is loaded.

View setup may include any of the following:

<ul>
<li>Using a specific user (ie sign in).</li>
<li>Assuming certain transactions have been made.</li>
<li>Interacting with multiple webpages by entering input, clicking buttons, etc.</li>
<li>Browser interactions like resize, scroll, etc.</li>
<li>Triggering special flows (ie modifying configuration settings or opting into an experiment).</li>
</ul>

## Run Rulesets

Many of our ruleset runner examples demonstrate how to run one ruleset at a time.  However, in practice, it is often easier to combine the results of multiple rulesets into a single JSON object.

## Process Results

After the results are created, they can be used to do the following:

<ul>
<li>Highlight errors and take screenshots</li>
<li>Store results in a database</li>
<li>Convert JSON results to an HTML Report</li>
</ul>

Highlighting errors and taking screenshots can help people quickly find, file and fix issues to improve quality.  Results can be stored in a database for tracking, which can also be surfaced via a quality dashboard.  Converting the JSON results to an HTML report is another way to help people digest the information.  If these HTML reports are uploaded to a central location, then quality dashboards can also link directly to these HTML reports.

<ul>
<li>The <a href='../examples/chromeextension'>Chrome Extension</a> example demonstrates how to highlight errors.  Some hooks are in place to upload the results via a POST call, after which the results might be stored.</li>
<li>The <a href='../examples/java'>Java with Selenium/TestNG</a> example demonstrates how to highlight errors, take screenshots and create an HTML Report.</li>
</ul>