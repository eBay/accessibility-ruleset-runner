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

var customRuleset = fs.readFileSync('custom.ruleset.1.1.33.js','utf8');
	  
// In Chrome, The Images do not load.  This can wait until images load in async call.
// runner must be defined, see createRunner
var checkImagesLoaded = ""
  +"var checkImagesLoaded = function() {"
  +"  var images = document.evaluate('//img', document, null, XPathResult.ANY_TYPE, null);"
  +"  var image = images.iterateNext();"
  +"  if(image.width > 0){"
  +"    runner();"
  +"  } else {"
  +"    setTimeout(checkImagesLoaded, 100);"
  +"  }"
  +"};"
  +"checkImagesLoaded();";

// Used to define runner for async call
var createRunner = ""
  +"function createRunner(callback, rulesToRun) {"
  +"  return function() {"
  +"    results = JSON.stringify(axs.Audit.run({rulesToRun: rulesToRun}));"
  +"    callback(results);"
  +"  };"
  +"}";

var getDriver = function (browserName, options) {
  return options ? 
    new webDriver.Builder().forBrowser(browserName).setChromeOptions(options).build() : 
    new webDriver.Builder().forBrowser(browserName).build();
};

var modifyHTML = function (html) {
  return html.replace(/\t/g, ' ').replace(/\r/g, ' ').replace(/\n/g, ' ').replace(/&/g, '&amp;');
}

// Certain elements the custom ruleset should flag but can removed based on a certain pattern
var processExemptions = function (results) {
  for(var i=0; i<results.length; i++) {
    var rule = results[i];
    var passed = rule["passed"];
    var failedElements = rule["elements"];
    for(var j=failedElements.length - 1; j > -1; j--) {
      var failedElement = failedElements[j];
      var ruleName = rule["rule"];
      var elementID = failedElement["elementID"];
      var elementClass = failedElement["elementClass"];
      var elementXPATH = failedElement["elementXPATH"];
      if(ruleName === "H30 Links Repeated" && elementClass && elementClass === "vip") {
        failedElements.splice(j,1); // remove failedElement at index j
        passed = passed + 1;
      } else if (ruleName === "H30 Links Repeated" && elementClass && elementClass === "s-item__link") {
        failedElements.splice(j,1); // remove failedElement at index j
        passed = passed + 1;
      } else if (ruleName === "H30 Links Repeated" && elementID && elementID.startsWith("ttl_") && elementClass && elementClass.includes("g-asm")) {
        failedElements.splice(j,1); // remove failedElement at index j
        passed = passed + 1;
      } else if (ruleName === "H30 Links Repeated" && elementXPATH && elementXPATH.includes("class='item-title'")) {
        failedElements.splice(j,1); // remove failedElement at index j
        passed = passed + 1;
      }
    }
    rule["passed"] = passed;
    rule["elements"] = failedElements;
  }
}

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

var verifyFailureErrorCodeEquals = function (results, ruleNumber, failureNumber, failureCode) {
  var elementFailureCode = results[ruleNumber]["elements"][failureNumber]["elementFailureCode"];
  assert.equal(elementFailureCode, failureCode);
};

var verifyFailureElementActionEquals = function (results, ruleNumber, failureNumber, action) {
  var elementAction = results[ruleNumber]["elements"][failureNumber]["elementAction"];
  assert.equal(elementAction, action);
};

var verifyFailureElementTextEquals = function (results, ruleNumber, failureNumber, textToCompare) {
  var elementText = results[ruleNumber]["elements"][failureNumber]["elementText"];
  assert.equal(elementText, textToCompare);
};

var verifyFailureElementOtherH1TextsContains = function (results, ruleNumber, failureNumber, elementOtherH1Text) {
  var elementOtherH1Texts = results[ruleNumber]["elements"][failureNumber]["elementOtherH1Texts"];
  assert(elementOtherH1Texts.includes(elementOtherH1Text));
};

describe('Test custom ruleset against altTagsBad', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find failures', function (done) { 
    var html = fs.readFileSync('tests/input/altTagsBad.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H24 Image Map Alt Attribute','H35 Applet Tag Alt Attribute','H53 Object Tag Alt Attribute','H64 IFrame Tag Title Attribute','H46 Embed Tag'];

	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';
	
    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
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
    var html = fs.readFileSync('tests/input/altTagsGood.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    // In Chrome, The Applets do not load.  Chrome Removes Alt tags for Objects with Errors
    var fixAppletAndObject = ""
      +"var applets = document.evaluate('//applet[@id=\"applet_2.1\"]', document, null, XPathResult.ANY_TYPE, null);"
      +"var applet = applets.iterateNext();"
      +"applet.alt='Java Applet';"
      +"var objects = document.evaluate('//object[@id=\"object_3.1\"]', document, null, XPathResult.ANY_TYPE, null);"
      +"var object = objects.iterateNext();"
      +"object.alt='Flash Object 3.1';";

    var rulesToRun = ['H24 Image Map Alt Attribute','H35 Applet Tag Alt Attribute','H53 Object Tag Alt Attribute','H64 IFrame Tag Title Attribute','H46 Embed Tag'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+fixAppletAndObject+runner, 'custom ruleset')
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

describe('Test custom ruleset against anchorBad', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/anchorBad.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H30 Opening New Windows','H30 Links Repeated','H75 Unique Anchor IDs'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyFailureElementIdentificationStringEquals(results,0,0,"a...anchor_A.1.1");
        verifyFailureErrorCodeEquals(results,0,0,"030_AA_1");
        verifyRuleNumberOfAssertionsTracked(results,1,9);
        verifyRuleNumberOfAssertionsFailed(results,1,3);
        verifyFailureElementIdentificationStringEquals(results,1,0,"a...anchor_B.1.2");
        verifyFailureErrorCodeEquals(results,1,0,"130_AA_1");
        verifyFailureElementIdentificationStringEquals(results,1,1,"a...anchor_B.2.2");
        verifyFailureErrorCodeEquals(results,1,1,"130_AA_1");
        verifyFailureElementIdentificationStringEquals(results,1,2,"a...anchor_B.3.2");
        verifyFailureErrorCodeEquals(results,1,2,"130_AA_1");
        verifyRuleNumberOfAssertionsTracked(results,2,9);
        verifyRuleNumberOfAssertionsFailed(results,2,1);
        verifyFailureElementIdentificationStringEquals(results,2,0,"a...anchor_C.1.1");
        verifyFailureErrorCodeEquals(results,2,0,"075_AA_1");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against anchorGood', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/anchorGood.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H30 Opening New Windows','H30 Links Repeated','H75 Unique Anchor IDs'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
		processExemptions(results); // Patterns can be made for various exemptions
        verifyRuleNumberOfAssertionsTracked(results,0,3);
        verifyRuleNumberOfAssertionsFailed(results,0,0);
        verifyRuleNumberOfAssertionsTracked(results,1,56);
        verifyRuleNumberOfAssertionsFailed(results,1,0);
        verifyRuleNumberOfAssertionsTracked(results,2,61);
        verifyRuleNumberOfAssertionsFailed(results,2,0);
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against formBad', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/formBad.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H44 Input Tag Label','H32 Form Submit Button'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,12);
        verifyRuleNumberOfAssertionsFailed(results,0,11);
        verifyFailureElementIdentificationStringEquals(results, 0, 0, "input...text_1.1");
        verifyFailureErrorCodeEquals(results,0,0,"044_A_1");
        verifyFailureElementIdentificationStringEquals(results, 0, 1, "input...text_1.2");
        verifyFailureErrorCodeEquals(results,0,1,"044_A_1");
        verifyFailureElementIdentificationStringEquals(results, 0, 2, "input...checkbox_1.3");
        verifyFailureErrorCodeEquals(results,0,2,"044_A_1");
        verifyFailureElementIdentificationStringEquals(results, 0, 3, "input...text_2.1");
        verifyFailureErrorCodeEquals(results,0,3,"044_A_1");
        verifyFailureElementIdentificationStringEquals(results, 0, 4, "input...checkbox_3.1");
        verifyFailureErrorCodeEquals(results,0,4,"044_A_1");
        verifyFailureElementIdentificationStringEquals(results, 0, 5, "input...checkbox_4.1");
        verifyFailureElementIdentificationStringEquals(results, 0, 6, "input...checkbox_4.2");
        verifyFailureElementIdentificationStringEquals(results, 0, 7, "input...text_4.3");
        verifyFailureElementIdentificationStringEquals(results, 0, 8, "input...text_4.4");
        verifyFailureElementIdentificationStringEquals(results, 0, 9, "input...checkbox_4.5");
        verifyFailureElementIdentificationStringEquals(results, 0, 10, "input...checkbox_5.1");
        verifyFailureErrorCodeEquals(results,0,10,"044_A_1");
        verifyRuleNumberOfAssertionsTracked(results,1,4);
        verifyRuleNumberOfAssertionsFailed(results,1,4);
        verifyFailureElementIdentificationStringEquals(results,1,0,"form...form_1");
        verifyFailureErrorCodeEquals(results,1,0,"032_A_1");
        verifyFailureElementActionEquals(results,1,0,"http://www.ebay.com/1");
        verifyFailureElementIdentificationStringEquals(results,1,1,"form...form_3");
        verifyFailureErrorCodeEquals(results,1,1,"032_A_1");
        verifyFailureElementActionEquals(results,1,1,"http://www.ebay.com/3");
        verifyFailureElementIdentificationStringEquals(results,1,2,"form...form_5");
        verifyFailureErrorCodeEquals(results,1,2,"032_A_1");
        verifyFailureElementActionEquals(results,1,2,"http://www.ebay.com/5");
        verifyFailureElementIdentificationStringEquals(results,1,3,"form...form_6");
        verifyFailureErrorCodeEquals(results,1,3,"032_A_1");
        verifyFailureElementActionEquals(results,1,3,"http://www.ebay.com/6");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against formGood', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/formGood.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    // Used to add a submit button programmatically within an iframe, which is a valid way to provide a submit button in a form
    var addSubmitButton = "var iframe = document.getElementById('form_frame_11');"
      +"iframe = (iframe.contentWindow) ? iframe.contentWindow : (iframe.contentDocument.document) ? iframe.contentDocument.document : iframe.contentDocument;"
      +"iframe.document.open();"
      +"iframe.document.write('<input type=\"submit\" id=\"submit_11.3\" value=\"Submit Button 11.3\" src=\"11.3.jpg\" alt=\"Alt Text Option\" />');"
      +"iframe.document.close();";

    var rulesToRun = ['H44 Input Tag Label','H32 Form Submit Button'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+addSubmitButton+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,39);
        verifyRuleNumberOfAssertionsFailed(results,0,0);
        verifyRuleNumberOfAssertionsTracked(results,1,9);
        verifyRuleNumberOfAssertionsFailed(results,1,0);
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against imageBad', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/imageBad.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H37 Image Tag Alt Attribute'];
    var runner = "var runner = createRunner(arguments[arguments.length - 1], "+JSON.stringify(rulesToRun)+");"

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeAsyncScript(innerHTML+customRuleset+createRunner+runner+checkImagesLoaded, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,12);
        verifyRuleNumberOfAssertionsFailed(results,0,12);
        verifyFailureElementIdentificationStringEquals(results,0,0,"img...image_1.1");
        verifyFailureElementIdentificationStringEquals(results,0,1,"img...image_2.2");
        verifyFailureElementIdentificationStringEquals(results,0,2,"img...image_3.2");
        verifyFailureElementIdentificationStringEquals(results,0,3,"img...image_4.2");
        verifyFailureElementIdentificationStringEquals(results,0,4,"img...image_1C.3");
        verifyFailureElementIdentificationStringEquals(results,0,5,"img...image_1C.6");
        verifyFailureElementIdentificationStringEquals(results,0,6,"img...image_1C.7");
        verifyFailureElementIdentificationStringEquals(results,0,7,"img...image_1C.8");
        verifyFailureElementIdentificationStringEquals(results,0,8,"img...image_1C.10");
        verifyFailureElementIdentificationStringEquals(results,0,9,"img...image_1C.11");
        verifyFailureElementIdentificationStringEquals(results,0,10,"img...image_1C.13");
        verifyFailureElementIdentificationStringEquals(results,0,11,"img...image_1C.14");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against imageGood', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/imageGood.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H37 Image Tag Alt Attribute'];
    var runner = "var runner = createRunner(arguments[arguments.length - 1], "+JSON.stringify(rulesToRun)+");"

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeAsyncScript(innerHTML+customRuleset+createRunner+runner+checkImagesLoaded, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,33);
        verifyRuleNumberOfAssertionsFailed(results,0,0);
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutBad', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutBad.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H25 Title Tag','H57 HTML Tag Lang Attribute','H42 Heading Hierarchy','H42 H1 Heading','Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyFailureElementIdentificationStringEquals(results,0,0,"html...");
        verifyFailureErrorCodeEquals(results,0,0,"025_A_1");
        verifyRuleNumberOfAssertionsTracked(results,1,1);
        verifyRuleNumberOfAssertionsFailed(results,1,1);
        verifyFailureElementIdentificationStringEquals(results,1,0,"html...");
        verifyFailureErrorCodeEquals(results,1,0,"057_A_1");
        verifyRuleNumberOfAssertionsTracked(results,2,11);
        verifyRuleNumberOfAssertionsFailed(results,2,2);
        verifyFailureElementIdentificationStringEquals(results,2,0,"h5...");
        verifyFailureErrorCodeEquals(results,2,0,"042_A_1");
        verifyFailureElementTextEquals(results,2,0,"MYHEADING_1.2.3.3.1");
        verifyFailureElementIdentificationStringEquals(results,2,1,"h5...");
        verifyFailureErrorCodeEquals(results,2,1,"042_A_1");
        verifyFailureElementTextEquals(results,2,1,"MYHEADING_1.3.2.3.1");
        verifyRuleNumberOfAssertionsTracked(results,3,1);
        verifyRuleNumberOfAssertionsFailed(results,3,1);
        verifyFailureElementIdentificationStringEquals(results,3,0,"html...");
        verifyFailureErrorCodeEquals(results,3,0,"142_A_1");
        verifyRuleNumberOfAssertionsTracked(results,4,1);
        verifyRuleNumberOfAssertionsFailed(results,4,1);
        verifyFailureElementIdentificationStringEquals(results,4,0,"html...");
        verifyFailureErrorCodeEquals(results,4,0,"0Skip_AA_3");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutBad (Skip to Main Source Invalid Text)', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutBad_SkipToMainSourceInvalidText.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyFailureElementIdentificationStringEquals(results,0,0,"html...");
        verifyFailureErrorCodeEquals(results,0,0,"0Skip_AA_2");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutBad (Skip to Main Source Missing)', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutBad_SkipToMainSourceMissing.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyFailureElementIdentificationStringEquals(results,0,0,"html...");
        verifyFailureErrorCodeEquals(results,0,0,"0Skip_AA_1");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutBad (Skip to Main Source Target Mismatch)', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutBad_SkipToMainSourceTargetMismatch.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyFailureElementIdentificationStringEquals(results,0,0,"html...");
        verifyFailureErrorCodeEquals(results,0,0,"0Skip_AA_3");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutBad (Skip to Main Target Duplicated)', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutBad_SkipToMainTargetDuplicated.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H42 H1 Heading','Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyFailureElementIdentificationStringEquals(results,0,0,"h1...");
        verifyFailureErrorCodeEquals(results,0,0,"142_A_2");
        verifyFailureElementOtherH1TextsContains(results,0,0,"MYHEADING_1");
        verifyFailureElementOtherH1TextsContains(results,0,0,"MYHEADING_2");
        verifyRuleNumberOfAssertionsTracked(results,1,1);
        verifyRuleNumberOfAssertionsFailed(results,1,1);
        verifyFailureElementIdentificationStringEquals(results,1,0,"html...");
        verifyFailureErrorCodeEquals(results,1,0,"0Skip_AA_4");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutBad (Skip to Main Target is H Tag)', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutBad_SkipToMainTargetIsHTag.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyFailureElementIdentificationStringEquals(results,0,0,"html...");
        verifyFailureErrorCodeEquals(results,0,0,"0Skip_AA_3");
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutGood', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutGood.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H25 Title Tag','H57 HTML Tag Lang Attribute','H42 Heading Hierarchy','H42 H1 Heading','Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyRuleNumberOfAssertionsTracked(results,1,1);
        verifyRuleNumberOfAssertionsFailed(results,1,1);
        verifyRuleNumberOfAssertionsTracked(results,2,12);
        verifyRuleNumberOfAssertionsFailed(results,2,0);
        verifyRuleNumberOfAssertionsTracked(results,3,1);
        verifyRuleNumberOfAssertionsFailed(results,3,0);
        verifyRuleNumberOfAssertionsTracked(results,4,1);
        verifyRuleNumberOfAssertionsFailed(results,4,0);
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});

describe('Test custom ruleset against pageLayoutGood (Hidden H1)', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  it('should find no failures', function (done) { 
    var html = fs.readFileSync('tests/input/pageLayoutGood_HiddenH1.html','utf8');
    var innerHTML = "document.body.innerHTML='"+modifyHTML(html)+"';";

    var rulesToRun = ['H25 Title Tag','H57 HTML Tag Lang Attribute','H42 Heading Hierarchy','H42 H1 Heading','Validate Skip to Main Content'];
	var runner = 'return JSON.stringify(axs.Audit.run({rulesToRun: '+JSON.stringify(rulesToRun)+'}));';

    driver = getDriver('chrome');

    driver
    .then(function() {
      driver.executeScript(innerHTML+customRuleset+runner, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        verifyRuleNumberOfAssertionsTracked(results,0,1);
        verifyRuleNumberOfAssertionsFailed(results,0,1);
        verifyRuleNumberOfAssertionsTracked(results,1,1);
        verifyRuleNumberOfAssertionsFailed(results,1,1);
        verifyRuleNumberOfAssertionsTracked(results,2,4);
        verifyRuleNumberOfAssertionsFailed(results,2,0);
        verifyRuleNumberOfAssertionsTracked(results,3,1);
        verifyRuleNumberOfAssertionsFailed(results,3,0);
        verifyRuleNumberOfAssertionsTracked(results,4,1);
        verifyRuleNumberOfAssertionsFailed(results,4,0);
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});