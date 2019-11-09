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

describe('Run custom ruleset', function () {
  this.timeout(500000);
  var driver;

  afterEach(function () {
    driver.quit();
  });

  
  it('should find no FAIL', function (done) { 
    var url = "http://www.google.com";
    var customRuleset = fs.readFileSync('../../rulesets/custom.ruleset.1.1.33.js','utf8');
    //var customRuleset = fs.readFileSync('./node_modules/accessibility-ruleset-runner/rulesets/custom.ruleset.1.1.32.js','utf8'); // Use this after "npm install accessibility-ruleset-runner"
    customRuleset = customRuleset + ' return JSON.stringify(axs.Audit.run());';
	
    driver = getDriver('chrome');

    driver
    .get(url)
    .then(function() {
      driver.executeScript(customRuleset, 'custom ruleset')
      .then(function (response) {
        var results = JSON.parse(response);
        var failureArray = results.filter(elem => elem.passOrFail === 'FAIL');
        console.log("FailureArrayLength:", failureArray.length);
        console.log("FailureArray:", failureArray);
        done();
      }).catch((err) => {
        console.log(err);
      });
    });
  });
});
