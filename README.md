# eBay Accessibility Ruleset Runner
The eBay Accessibility Ruleset Runner automates 20% of <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> AA recommendations, saving time on manual testing.

## Summary
Getting started with accessibility testing can be difficult.  Not only are there a variety of tools out there to choose from but testers must be accessibility experts to sort through the large number of false positives identified by these tools.  In addition, accessibility testing requires a lot of time to perform manual acceptance tests and only a small portion of these tests can be automated.

This project demonstrates how accessibility testing is done upstream during the development process.  The project includes two rulesets, which is what we use internally (<a href="rulesets#custom-ruleset">Custom Ruleset</a>, <a href="rulesets#axe-ruleset">aXe Ruleset</a>).  Developers can reuse our custom ruleset, exchange rulesets or add their own.

### What is WCAG 2.0

<a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> recommendations are published by the World Wide Web Consortium.  The <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> Abstract reads as follows:

"Web Content Accessibility Guidelines (WCAG) 2.0 covers a wide range of recommendations for making Web content more accessible. Following these guidelines will make content accessible to a wider range of people with disabilities, including blindness and low vision, deafness and hearing loss, learning disabilities, cognitive limitations, limited movement, speech disabilities, photosensitivity and combinations of these. Following these guidelines will also often make your Web content more usable to users in general."

## Getting Started

There are 3 types of users of this project:
<ul>
  <li>Users that want to explore the benefit of using a ruleset</li>
  <li>Users that want to run the rulesets in their project</li>
  <li>Users that want to modify/create rulesets</li>
</ul>

Some users do not know ahead of time whether they want to use these rulesets and may just be exploring.  They should checkout the examples section.  Our examples were created to guide new users through environment setup (less than 1 hour) and running (less than 5 minutes).

Users that want to run the rulesets in their project can either modify one of our examples or create their own framework.  When creating a framework, it is suggested to check out the examples to get an idea of how we run the rulesets.

Users can also contribute to our custom ruleset or even create their own rulesets.  We give some top level guidance here on creating rulesets.

### Examples

Each example has its own Readme file which includes information about the following:
<ul>
<li>Prerequisites for general environment setup</li>
<li>Running the rulesets against a default website</li>
<li>Modifications to test another website</li>
<li>Modifications to include in your project</li>
</ul>

The ruleset runner examples require zero configuration.  This “one click” setup allows new users to quickly run the examples, to get an idea of what the ruleset runner does.  In other words, the ruleset runners are preconfigured to test a default web page and the examples can typically be run using a single command.

Here are some basic examples:

<ul>
<li><a href='examples/chrome/README.md'>Chrome Developer Console</a></li>
<li><a href='examples/nodejs/README.md'>NodeJS with Selenium/Mocha/Chai</a></li>
<li><a href='examples/python/README.md'>Python with Selenium</a></li>
</ul>

Here are some projects that follow the <a href='topics/GENERALSTEPSFORRUNNINGRULESETS.md'>General Steps for Running Rulesets</a>.

<ul>
<li><a href='examples/chromeextension/README.md'>Chrome Extension</a></li>
<li><a href='examples/java/README.md'>Java with Selenium/TestNG</a></li>
</ul>

## Next Steps

### Creating a Ruleset

We created these general principles after reviewing publically available accessibility tools.  These principles were used to build our <a href="rulesets#custom-ruleset">Custom Ruleset</a>.  Later, we started using the <a href="rulesets#axe-ruleset">aXe Ruleset</a> from <a href='https://www.deque.com/'>Deque Systems</a> due to their alignment with these principles.

<ol>
<li>Rulesets should place an emphasis on 0 false positives.  By having 0 false positives, there is no room for interpretation and teams can be required to have 100% pass rate prior to launching a new feature.
</li>

<li>Rulesets should be written in vanilla javascript and published as a single javascript file.  This makes the rulesets highly portable so they can run in a variety of environments.
</li>
<li>Rulesets should return a well formed JSON.  JSON is also highly portable.  Results can be stored in a database for tracking, aggregated/displayed in dashboards and even converted directly into user friendly HTML Reports.
</li>
<li>Rulesets should be vetted against a library of html code snippets.  There should be examples of good/bad code that pass/fail various rules, as expected.  Covering a large number of code variations tends to make the ruleset more robust.  See also <a href='rulesets/tests/README.md#testing-methodology'>Testing Methodology</a>.
</li>
</ol>

### Contribution
Contributions in terms of patches, features, or comments are always welcome. Refer to the <a href='CONTRIBUTING.md'>Contributing Guidelines</a> for help. Submit Github issues for any feature enhancements, bugs, or documentation problems as well as questions and comments.

# Topic Guide
Several topics are discussed and scattered throughout this repository. Please use this <a href='TOPICGUIDE.md'>Topic Guide</a>.

# License
Copyright (c) 2018-2019 eBay Inc.

Use of this source code is governed by a Apache 2.0 license that can be found in the LICENSE file or at https://opensource.org/licenses/Apache-2.0.
