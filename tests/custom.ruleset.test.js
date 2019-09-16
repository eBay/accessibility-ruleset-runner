/************************************************************************
Copyright 2017-2019 eBay Inc.
Author/Developer(s): Sara Xu, Scott Izu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**************************************************************************/

var webDriver = require('selenium-webdriver');
var chrome = require('selenium-webdriver/chrome');
var assert = require('chai').assert;
var fs = require('fs');

var getDriver = function (browserName, options) {
  return options ? 
    new webDriver.Builder().forBrowser(browserName).setChromeOptions(options).build() : 
    new webDriver.Builder().forBrowser(browserName).build();
};

var verifyRuleNumberOfAssertionsFailed = function (results, ruleNumber, numberOfAssertionsFailed) {
  var failedElementsLength = results[ruleNumber]["elements"].length;
  assert.equal(failedElementsLength, numberOfAssertionsFailed);
};

var verifyRuleNumberOfAssertionsTracked = function (results, ruleNumber, numberOfAssertionsTracked) {
  var rule = results[ruleNumber];
  var passed = rule["passed"];
  var failedElementsLength = rule["elements"].length;
  assert.equal(passed+failedElementsLength, numberOfAssertionsTracked);
};

var verifyFailureElementIdentificationStringEquals = function (results, ruleNumber, failureNumber, identificationString) {
  var failedElement = results[ruleNumber]["elements"][failureNumber];
  var elementTag = failedElement["elementTag"];
  var elementClass = failedElement["elementClass"];
  var elementName = failedElement["elementName"];
  if(!elementName) {
    elementName = "";
  }
  var elementID = failedElement["elementID"];
  var elementIdentificationString = elementTag+"."+elementClass+"."+elementName+"."+elementID;
  assert.equal(elementIdentificationString, identificationString);
};

var verifyFailureErrorCodeEquals = function (results, ruleNumber, failureNumber, errorCode) {
  var elementFailureCode = results[ruleNumber]["elements"][failureNumber]["elementFailureCode"];
  assert.equal(elementFailureCode, errorCode);
};

describe('Test custom ruleset against altTagsBad', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find failures', function (done) { 
    var altTagsBad = fs.readFileSync('../tests/input/altTagsBad.html','utf8');
    altTagsBad = altTagsBad.replace(/\t/g, ' ').replace(/\r/g, ' ').replace(/\n/g, ' ');
    var innerHTML = "document.body.innerHTML='"+altTagsBad+"';";

    var rulesToRun = ['H24 Image Map Alt Attribute','H35 Applet Tag Alt Attribute','H53 Object Tag Alt Attribute','H64 IFrame Tag Title Attribute','H46 Embed Tag'];
	
    var customRuleset = fs.readFileSync('../rulesets/custom.ruleset.1.1.32.js','utf8');
    customRuleset = customRuleset + ' return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';
	
    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,1,1);
        verifyRuleNumberOfAssertionsFailed(results,2,1);
        verifyRuleNumberOfAssertionsFailed(results,3,1);
        verifyRuleNumberOfAssertionsFailed(results,4,2);
        verifyFailureElementIdentificationStringEquals(results,0,0,"area...map_1.3");
        verifyFailureErrorCodeEquals(results,0,0,"024_A_1");
        verifyFailureElementIdentificationStringEquals(results,1,0,"applet...applet_2.1");
        verifyFailureErrorCodeEquals(results,1,0,"035_A_1");
        verifyFailureElementIdentificationStringEquals(results,2,0,"object...object_3.1");
        verifyFailureErrorCodeEquals(results,2,0,"053_A_1");
        verifyFailureElementIdentificationStringEquals(results,3,0,"iframe...iframe_4.1");
        verifyFailureErrorCodeEquals(results,3,0,"064_A_1");
        verifyFailureElementIdentificationStringEquals(results,4,0,"embed...embed_5.1");
        verifyFailureErrorCodeEquals(results,4,0,"046_A_1");
        verifyFailureElementIdentificationStringEquals(results,4,1,"embed...embed_5.2");
        verifyFailureErrorCodeEquals(results,4,1,"046_A_1");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against altTagsGood', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var altTagsGood = fs.readFileSync('../tests/input/altTagsGood.html','utf8');
    altTagsGood = altTagsGood.replace(/\t/g, ' ').replace(/\r/g, ' ').replace(/\n/g, ' ');
    var innerHTML = "document.body.innerHTML='"+altTagsGood+"';";

    // In Chrome, The Applets do not load.  Chrome Removes Alt tags for Objects with Errors
    var chromBugFixApplets = "var applets = document.evaluate('//applet[@id=\"applet_2.1\"]', document, null, XPathResult.ANY_TYPE, null); var applet = applets.iterateNext(); applet.alt='Java Applet';";
    var chromBugFixObjects = "var objects = document.evaluate('//object[@id=\"object_3.1\"]', document, null, XPathResult.ANY_TYPE, null); var object = objects.iterateNext(); object.alt='Flash Object 3.1';";

    var rulesToRun = ['H24 Image Map Alt Attribute','H35 Applet Tag Alt Attribute','H53 Object Tag Alt Attribute','H64 IFrame Tag Title Attribute','H46 Embed Tag'];

    var customRuleset = fs.readFileSync('../rulesets/custom.ruleset.1.1.32.js','utf8');
    customRuleset = customRuleset + ' return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+chromBugFixApplets+chromBugFixObjects+customRuleset, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,3);
        verifyRuleNumberOfAssertionsFailed(results,0,0);
        verifyRuleNumberOfAssertionsTracked(results,1,1);
        verifyRuleNumberOfAssertionsFailed(results,1,0);
        verifyRuleNumberOfAssertionsTracked(results,2,3);
        verifyRuleNumberOfAssertionsFailed(results,2,0);
        verifyRuleNumberOfAssertionsTracked(results,3,2);
        verifyRuleNumberOfAssertionsFailed(results,3,0);
        verifyRuleNumberOfAssertionsTracked(results,4,3);
        verifyRuleNumberOfAssertionsFailed(results,4,0);
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});
