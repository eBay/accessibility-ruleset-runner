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

var getDriver = (browserName, options) => {
  return options ? 
    new webDriver.Builder().forBrowser(browserName).setChromeOptions(options).build() : 
    new webDriver.Builder().forBrowser(browserName).build();
};

var createRuleResultsCallback = function(rulesToRun, done) {
  return function(results) {	  
    var violationRules = [];
    let violations = getViolations(results, violationRules);   
    if (violations.length > 0) {
      console.log("FailedRuleNames:" + JSON.stringify(violationRules));
      console.log(violations);
    }

    done();
  };
};

var getViolations = function(results, violationRules) {
  if(!results || !results.violations) return [];
  let violations = results.violations;
  for (let item of violations) {
    violationRules.push(item.id);
    delete item.tags;
    if (item.nodes.length) {
      let simpleNodes = [];
      for (let node of item.nodes) {
        let simpleNode = `Elem: ${node.target}          Source: ${node.html}`;
        simpleNodes.push(simpleNode);
      }
      item['nodes'] = simpleNodes; 
    }
  }
  return violations;
};

describe('Run aXe ruleset', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });
  
  it.only('should find no violations', (done) => { 
    var url = "http://www.google.com";
    var aXeRuleset = fs.readFileSync('../../rulesets/aXe.ruleset.3.4.0.js','utf8');
    //var aXeRuleset = fs.readFileSync('./node_modules/accessibility-ruleset-runner/rulesets/aXe.ruleset.3.4.0.js','utf8'); // Use this after "npm install accessibility-ruleset-runner"

    driver = getDriver('chrome');

    driver
    .get(url)
    .then(function() {
      // Configure which rules to run.  Each of these rules corresponds to a WCAG 2.0 AA guideline
      var rulesToRun = ['area-alt','accesskeys','aria-allowed-attr','aria-required-attr','aria-required-children','aria-required-parent','aria-roles','aria-valid-attr-value','aria-valid-attr','audio-caption','blink','button-name','bypass','checkboxgroup','color-contrast','document-title','duplicate-id','empty-heading','heading-order','html-lang-valid','image-redundant-alt','input-image-alt','label','layout-table','link-name','marquee','meta-refresh','meta-viewport','meta-viewport-large','object-alt','radiogroup','scope-attr-valid','server-side-image-map','tabindex','table-duplicate-name','td-headers-attr','th-has-data-cells','valid-lang','video-caption','video-description'];
      driver.executeAsyncScript((aXeRuleset + '; sendResults = arguments[arguments.length - 1]; axe.run({runOnly: {type: "rule", values: ' + JSON.stringify(rulesToRun) + '}}, function (err, results) { sendResults(err || results); });'),'aXe 3.4.0 ruleset')
      .then(createRuleResultsCallback(rulesToRun, done)
      ).catch((err) => {
        console.log(err);
      });
    });
  });
});
