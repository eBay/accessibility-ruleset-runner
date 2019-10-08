# Include in Your Project

Here, we include a more general discussion about what to consider when running rulesets in your project.  The general steps to run rulesets are:

<ol>
<li>Configure Parameters</li>
<li>Run Rulesets</li>
<li>Process Results</li>
</ol>

## Configure Parameters

Having some base parameters might be useful:

<ul>
<li><b>View Name:</b> A name should be given to the view that was setup.  This name should be specific and descriptive enough to help scale (ie working with hundreds of views).  See below for the definition of a view.</li>
<li><b>JIRA Project Name:</b> Many companies use JIRA and bugs can be filed appropriately using this parameter.</li>
<li><b>Report Title:</b> A report title is helpful if a report will be created with the results.  In some case, multiple views might be aggregated into a single report to help make the results more digestable.</li>
</ul>

<b>View</b> - A View is a set of webpages that all share certain characteristics.  For example, many companies have a search capability for items to purchase.  A View might be defined based on this search results page including the following characteristics: H1 contains Laptop, at least 5 items appear in the search results section, the header/footer landmark are present, a nav for the breadcrumb and left navigation are present, etc.  Notice that a particular view may not have the same exact html code each time it is loaded.

## Run Rulesets

Many of our ruleset runner examples demonstrate how to run one ruleset at a time.  However, in practice, it is often easier to combine the results of multiple rulesets into a single JSON object.

## Process Results

After the results are created, they can be uploaded to a server.  The server might store the results in a database for tracking and building various dashboards.  The server also might convert the JSON results to an HTML report and upload it to a central location for other people to view.

