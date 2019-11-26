//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Scott Izu
// Last Modified: 9/24/2019
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//////////////////////////////////////////////////////////////////////////

CustomRulesetImportJSURL = "https://raw.githubusercontent.com/eBay/accessibility-ruleset-runner/master/rulesets/custom.ruleset.1.1.33.js";
AXERulesetImportJSURL = "https://raw.githubusercontent.com/eBay/accessibility-ruleset-runner/master/rulesets/aXe.ruleset.3.4.0.js";

RulesetRunner=function() {
  var processTracker = {};
  DownloadCustom(processTracker, DownloadCustomFinished);
}

DownloadCustomFinished=function(processTracker) {
  DownloadAXE(processTracker, CreateModal);
}

CreateModal=function(processTracker) {
  console.log('Step 1: Configure Parameters');
  var defaultPageName = "DefaultPageName";
  try {
    // MODAL PATTERN
    var modalHTML = "";
    modalHTML = modalHTML + "\n    <!-- dialog content goes here -->";
    modalHTML = modalHTML + "\n    <h3>Background</h3>";
    modalHTML = modalHTML + "\n    For more information, visit <a href='https://github.com/ebay/accessibility-ruleset-runner.git'>Github</a>.";
    modalHTML = modalHTML + "\n    <br>";
    modalHTML = modalHTML + "\n    <h3>Parameters</h3>";
    modalHTML = modalHTML + "\n    <div>";
    modalHTML = modalHTML + "\n      <form id='arr_form'>";
    modalHTML = modalHTML + "\n        <label for='viewname'>VIEW_NAME</label>: <input id='viewname' type='text' maxlength='100' size='100'><br>";
    modalHTML = modalHTML + "\n        <div id='jiraprojectname_div'>";
    modalHTML = modalHTML + "\n          <span id='jiraprojectname_span'>";
    modalHTML = modalHTML + "\n            <label for='jiraprojectname'>JIRA_PROJECT_NAME</label>:";
	
	// Configure the Bubble Help - http://ebay.github.io/mindpatterns/disclosure/infotip/
    modalHTML = modalHTML + "\n            <span id='arr_span_bubblehelp_widget_1'>";
    modalHTML = modalHTML + "\n              <a id='arr_a_bubblehelp_button_1' href=''></a>";
    modalHTML = modalHTML + "\n              <span id='arr_span_bubblehelp_live_1'>";
    modalHTML = modalHTML + "\n                <div id='arr_div_bubblehelp_content_1' sytle='left:540px display:none; top: 120px'>";
    modalHTML = modalHTML + "\n                  This field might be auto populated based on reading page metadata.";
    modalHTML = modalHTML + "\n                </div>";
    modalHTML = modalHTML + "\n              </span>";
    modalHTML = modalHTML + "\n            </span>";
	
	// Configure Autocomplete - https://ebay.gitbook.io/mindpatterns/input/autocomplete
    modalHTML = modalHTML + "\n            <span id='arr_span_jiraprojectname_widget'>";
    modalHTML = modalHTML + "\n              <input id='jiraprojectname' type='text' maxlength='50' size='50'>";
    modalHTML = modalHTML + "\n              <span id='arr_jiraprojectname_combobox_instructions'></span>";
    modalHTML = modalHTML + "\n              <ul id='arr_jiraprojectname_combobox_listbox'>";
    modalHTML = modalHTML + "\n              </ul>";
    modalHTML = modalHTML + "\n            </span>";

    modalHTML = modalHTML + "\n          </span>";
    modalHTML = modalHTML + "\n        </div>";
    modalHTML = modalHTML + "\n        <label for='reporttitle'>REPORT_TITLE</label>: <input id='reporttitle' type='text' maxlength='100' size='100'><br>";
    modalHTML = modalHTML + "\n        <br>A report will be stored at: <span id='storagefolder'></span><br>";
    modalHTML = modalHTML + "\n        <br>The url being tested is <span id='url'></span><br>";
    modalHTML = modalHTML + "\n        <input id='xpathRoot' type='hidden' maxlength='100' size='100'>"; // 1/17/2018 - Added for Modal testing 
    modalHTML = modalHTML + "\n        <br>";
    modalHTML = modalHTML + "\n        <input id='ruleset_runner_submit_button' type='submit' value='Run'>";
    modalHTML = modalHTML + "\n      </form>";
    modalHTML = modalHTML + "\n    </div>";
  
    var modal = CFCreateModal(CFDocumentFunction(), "Ruleset Runner", modalHTML);
  
    // Configure the Bubble Help - http://ebay.github.io/mindpatterns/disclosure/infotip/
    configureBubbleHelp('arr_span_bubblehelp_widget_1', 'arr_a_bubblehelp_button_1', 'arr_span_bubblehelp_live_1', 'arr_div_bubblehelp_content_1');

	// This could be passed into the function
    var projectNameToProjectKeyMap = [];
    projectNameToProjectKeyMap['Project 1'] = 'KEY1';
    projectNameToProjectKeyMap['Project 2'] = 'KEY2';
    projectNameToProjectKeyMap['Test Project'] = 'KEY3';
	
    // Configure Autocomplete - https://ebay.gitbook.io/mindpatterns/input/autocomplete
	configureAutoComplete('arr_jiraprojectname_combobox_listbox', 'arr_jiraprojectname_combobox_listbox_activedescendent', 'arr_jiraprojectname_combobox_instructions', 'jiraprojectname', "JIRA_PROJECT_NAME:---", projectNameToProjectKeyMap);
    jiraprojectname.value = "Test Project";
  
    // VIEW NAME
    var viewname = CFDocumentFunction().getElementById('viewname');
    viewname.addEventListener("input", function(event){
      updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap);
    });
    viewname.value = "Custom View With First Variation";
  
    // REPORT TITLE
    var reporttitle = CFDocumentFunction().getElementById('reporttitle');
    reporttitle.addEventListener("input", function(event){
      updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap);
    });
    reporttitle.value = "Test Report";
  
    // STORAGE_FOLDER
    var storagefolder = CFDocumentFunction().getElementById('storagefolder');
    storagefolder.addEventListener("input", function(event){
      updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap);
    });
    updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap);
  
    // URL
    CFDocumentFunction().getElementById('url').innerHTML = CFDocumentFunction().location.href;
  
    // SUBMIT BUTTON
    var submitButton = CFDocumentFunction().getElementById('ruleset_runner_submit_button');
    submitButton.addEventListener("click", function(event){
      event.preventDefault(); // cancel default behavior
	  processTracker.jiraprojectname = CFDocumentFunction().getElementById('jiraprojectname').value;
	  processTracker.reporttitle = CFDocumentFunction().getElementById('reporttitle').value;
	  processTracker.storagefolder = CFDocumentFunction().getElementById('storagefolder').innerHTML;
	  processTracker.xpathRoot = CFDocumentFunction().getElementById('xpathRoot').value;
	  processTracker.url = CFDocumentFunction().getElementById('url').innerHTML;
	  processTracker.reporttitle = CFDocumentFunction().getElementById('reporttitle').value;
	  processTracker.viewname = CFDocumentFunction().getElementById('viewname').value;
	
      CFDocumentFunction().body.removeChild(modal);
	
	  AjaxCallForFormSubmission(processTracker);
    });
  
  } catch (error) {
    console.log('error:'+error);
    console.log('error.stack:'+error.stack);
  }
};

// Configure the Bubble Help - http://ebay.github.io/mindpatterns/disclosure/infotip/
function configureBubbleHelp(bubbleHelpWidgetID, bubbleHelpButtonID, bubbleHelpLiveID, bubbleHelpContentID) {
  var bubbleHelpWidget = CFDocumentFunction().getElementById(bubbleHelpWidgetID);
  var bubbleHelpButton = CFDocumentFunction().getElementById(bubbleHelpButtonID);
  var bubbleHelpLive = CFDocumentFunction().getElementById(bubbleHelpLiveID);
  var bubbleHelpContent = CFDocumentFunction().getElementById(bubbleHelpContentID);
  bubbleHelpWidget.setAttribute("class", "bubblehelp bubblehelp-js");
  bubbleHelpWidget.sticky = function() {
    bubbleHelpContent.style.top = bubbleHelpWidget.offsetTop - CFDocumentFunction().scrollTop - Math.round(bubbleHelpContent.offsetHeight / 3) + 'px';
  };
  bubbleHelpWidget.show = function() {
	bubbleHelpWidget.sticky();
    bubbleHelpButton.setAttribute('aria-expanded', 'true');
    bubbleHelpContent.style.display = 'block';
  };
  bubbleHelpWidget.hide = function() {
	bubbleHelpWidget.sticky();
    bubbleHelpButton.setAttribute('aria-expanded', 'false');
    bubbleHelpContent.style.display = 'none';
  };
  bubbleHelpWidget.toggle = function() {
    if(bubbleHelpButton.getAttribute('aria-expanded') === 'false') {
	  bubbleHelpWidget.show();
	} else {
	  bubbleHelpWidget.hide();
	}
  };
  bubbleHelpWidget.addEventListener("keydown", function(event){
    if (event.keyCode == '27') { // Escape
      bubbleHelpWidget.hide();
	}
  });
  bubbleHelpButton.setAttribute('aria-label', 'Help');
  bubbleHelpButton.setAttribute('aria-controls', 'arr_div_bubblehelp_content_1');
  bubbleHelpButton.setAttribute('aria-expanded', 'false');
  bubbleHelpButton.setAttribute('role', 'button');
  bubbleHelpButton.style.background = 0; // css
  bubbleHelpButton.style.border = 0; // css
  bubbleHelpButton.style.cursor = 'pointer'; // css
  bubbleHelpButton.style.padding = 0; // css
  bubbleHelpButton.innerHTML = '&#xe614;';
  bubbleHelpButton.addEventListener("click", function(event){
    event.preventDefault();
	bubbleHelpWidget.focus();
	bubbleHelpWidget.toggle();
  });
  bubbleHelpButton.addEventListener("keydown", function(event){
    if (event.keyCode == '32') { // Space
      event.preventDefault();
	  bubbleHelpWidget.toggle();
	}
  });
  bubbleHelpLive.setAttribute('aria-live', 'polite');
  bubbleHelpContent.setAttribute("class", "bubblehelp-content");
  bubbleHelpContent.style.background = 'LightYellow'; // css
  bubbleHelpContent.style.border = '1px solid #ccc'; // css
  bubbleHelpContent.style.boxShadow = '2px 2px 1px #ccc'; // css
  bubbleHelpContent.style.display = 'none';
  bubbleHelpContent.style.fontSize = 'small'; // css
  bubbleHelpContent.style.margin = '0 0.25em'; // css
  bubbleHelpContent.style.maxWidth = '500px'; // css
  bubbleHelpContent.style.padding = '0 1em'; // css
  bubbleHelpContent.style.position = 'fixed'; // css
  bubbleHelpContent.style.textAlign='justify'; // css
  bubbleHelpContent.style.zIndex = 1; // css
  bubbleHelpContent.style.left = bubbleHelpWidget.offsetLeft + bubbleHelpButton.offsetWidth;
}

function configureAutoComplete(comboBoxListBoxID, comboBoxListBoxActiveDecendentID, comboBoxInstructionsID, inputElementID, inputLabel, projectNameToProjectKeyMap) {
  var comboBoxListBox = CFDocumentFunction().getElementById(comboBoxListBoxID);
  var comboBoxInstructions = CFDocumentFunction().getElementById(comboBoxInstructionsID);
  var input = CFDocumentFunction().getElementById(inputElementID);
	
  comboBoxListBox.style.background = 'LightYellow'; // css
  comboBoxListBox.style.border = '1px solid #eee'; // css
  comboBoxListBox.style.fontSize = 'small'; // css
  comboBoxListBox.style.listStyleType = 'none'; // css
  comboBoxListBox.style.margin = 0; // css
  comboBoxListBox.style.padding = 0; // css
  comboBoxListBox.style.position = 'fixed'; // css
  comboBoxListBox.style.zIndex = 1; // css
  comboBoxListBox.style.marginLeft = getLabelWidth(inputLabel); // horizontally align with input box
  comboBoxListBox.clientWidth = input.clientWidth; // js
  comboBoxListBox.setAttribute('role', 'listbox'); // js
  
  // Allow Users to click the Auto Complete options
  comboBoxListBox.addEventListener("click", function(event){
    var activeDecendent = CFDocumentFunction().getElementById(comboBoxListBoxActiveDecendentID);
	if(activeDecendent) {
	  configureNormalLI(activeDecendent);
	}
    var nextDecendent = event.target;
	if(nextDecendent) {
	  configureSelectedLI(nextDecendent);
	  var projectName = nextDecendent.innerHTML.split(/ \(/g)[0];
	  input.value = projectName;
	  updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap);
	  // Empty List Box
      populateListBox(projectNameToProjectKeyMap, input, comboBoxListBox, comboBoxInstructions);
	  input.focus();
	}
  });
  
  comboBoxInstructions.style.display = 'none'; // css
  
  input.style.display = 'inline'; // css
  input.setAttribute('role', 'combobox'); // js
  input.setAttribute('aria-expanded', 'false'); // js
  input.setAttribute('autocomplete','off'); // js
  input.setAttribute('aria-owns', comboBoxListBoxID); // js
  input.setAttribute('aria-describedby', comboBoxInstructionsID);
  input.setAttribute('aria-activedescendant', comboBoxListBoxActiveDecendentID);
  
  // Create Auto Complete options as user types
  input.addEventListener("input", function(event){
    populateListBox(projectNameToProjectKeyMap, input, comboBoxListBox, comboBoxInstructions);
	updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap);
  });
  
  // Allow Up/Down Arrow Keys to control the Auto Complete options
  input.addEventListener("keydown", function(event){
    var activeDecendent = CFDocumentFunction().getElementById(comboBoxListBoxActiveDecendentID);
    var nextDecendent;
    if (event.keyCode == '40') { // Down Arrow
	  if(activeDecendent) {
        nextDecendent = activeDecendent.nextSibling;
      } else {
	    nextDecendent = comboBoxListBox.firstChild;
	  }
    } else if (event.keyCode == '38') { // Up Arrow
	  event.preventDefault(); // Prevent carat from moving to beginning of Input Box
	  if(activeDecendent) {
        nextDecendent = activeDecendent.previousSibling;
      } else {
	    nextDecendent = comboBoxListBox.lastChild;
	  }
	} else if (event.keyCode == '13' || event.keyCode == '27' ) { // Enter Key
	  if(comboBoxListBox.firstChild != null) { // Should this be if (activeDecendent) which is if it has an active decendent don't clear form or if firstChild exists
	    event.preventDefault(); // Don't Submit the form on active decendent
        depopulateListBox(input, comboBoxListBox);
	  }
	}
	if(activeDecendent && nextDecendent) {
      configureNormalLI(activeDecendent);
	}
	if(nextDecendent) {
	  configureSelectedLI(nextDecendent);
	  var projectName = nextDecendent.innerHTML.split(/ \(/g)[0];
	  input.value = projectName;
	  updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap);
	}
  });
  
  // Remove the Auto Complete options when focut moves away from input
  input.addEventListener("blur", function(event){ // focus lost
    setTimeout(function(e) {
      depopulateListBox(input, comboBoxListBox);
    }, 100);
  });
}

function getLabelWidth(label) {
  var testLabelWidthDiv = CFDocumentFunction().createElement("div");
  var testLabelWidthLabel = CFDocumentFunction().createElement("label");
  testLabelWidthLabel.innerHTML = label;
  testLabelWidthLabel.style.position = 'absolute'; // css
  testLabelWidthLabel.style.visibility = 'hidden'; // css
  testLabelWidthLabel.style.height = 'auto'; // css
  testLabelWidthLabel.style.width = 'auto'; // css
  testLabelWidthLabel.style.whiteSpace = 'nowrap'; // css
  testLabelWidthDiv.appendChild(testLabelWidthLabel);
  CFDocumentFunction().body.appendChild(testLabelWidthDiv);
  var labelWidth = testLabelWidthLabel.clientWidth + 'px';
  CFDocumentFunction().body.removeChild(testLabelWidthDiv);
  return labelWidth;
}

function depopulateListBox(jiraprojectname, arr_jiraprojectname_combobox_listbox) {
  arr_jiraprojectname_combobox_listbox.innerHTML = '';
  jiraprojectname.setAttribute('aria-expanded', 'false'); // js
}
function populateListBox(projectNameToProjectKeyMap, jiraprojectname, arr_jiraprojectname_combobox_listbox, arr_jiraprojectname_combobox_instructions) {
	depopulateListBox(jiraprojectname, arr_jiraprojectname_combobox_listbox);
	var counter = 0;
    for (var projectName in projectNameToProjectKeyMap) {
      var projectKey = projectNameToProjectKeyMap[projectName];

	  if(projectKey 
	      && ((projectName.toLowerCase().indexOf(jiraprojectname.value.toLowerCase(), 0) != -1) || (projectKey.toLowerCase().indexOf(jiraprojectname.value.toLowerCase(), 0) != -1) )
		  && !(projectName === jiraprojectname.value)
		) {
	    counter = counter + 1;
	    var listBoxLi = createLI(projectName, projectKey);
        configureNormalLI(listBoxLi);
	    arr_jiraprojectname_combobox_listbox.appendChild(listBoxLi);
	  }
    }
	// If ACCI is typed in input box and ACCI and ACCIOS are both options
	// Should this be if counter > 0 or always show
	//if(counter > 0) {
	  var projectKey = projectNameToProjectKeyMap[jiraprojectname.value];
	  if(!projectKey) {
	    if(jiraprojectname.value === '') {
	      projectKey = 'Select a JIRA Project';
		} else {
	      projectKey = 'Project does not exist';
		}
	  }
	  if(projectKey) {
	    counter = counter + 1;
	    var listBoxLi = createLI(jiraprojectname.value, projectKey);
		configureSelectedLI(listBoxLi);
        arr_jiraprojectname_combobox_listbox.insertBefore(listBoxLi, arr_jiraprojectname_combobox_listbox.firstChild);
	  }
	//}
	if(counter > 0) {
	  jiraprojectname.setAttribute('aria-expanded', 'true'); // js
	  clearTimeout();
	}
    arr_jiraprojectname_combobox_instructions.innerHTML = 'Combobox list has '+counter+' options. Use down key to navigate.'; // js 
}
function createLI(projectName, projectKey) {
  var listBoxLi = CFDocumentFunction().createElement("li");
  listBoxLi.setAttribute('role', 'option'); // js
  listBoxLi.style.cursor = 'pointer';
  listBoxLi.style.padding = '0.5em';
  listBoxLi.innerHTML = projectName + ' (' + projectKey+')';
  return listBoxLi;
}

function configureSelectedLI(listBoxLi) {
  listBoxLi.id='arr_jiraprojectname_combobox_listbox_activedescendent';
  listBoxLi.style.backgroundColor='#006699'; // css
  listBoxLi.style.color='white'; // css
}

function configureNormalLI(listBoxLi) {
  listBoxLi.id='';
  listBoxLi.style.backgroundColor='LightYellow'; // css
  listBoxLi.style.color='black'; // css
}

// Could make a helper function - Get StorageSubFolderFromCurrentStorageFolder
function updateStorageFolderBasedOnNewProjectName(projectNameToProjectKeyMap) {
  var currentJIRAProjectNameValue = CFDocumentFunction().getElementById('jiraprojectname').value;
  var currentReportTitleValue = CFDocumentFunction().getElementById('reporttitle').value;
  var currentJIRAProjectFolder = createFolderFromName(currentJIRAProjectNameValue);
  var currentReportTitleFolder = createFolderFromName(currentReportTitleValue);
  CFDocumentFunction().getElementById('storagefolder').innerHTML = currentJIRAProjectFolder + "/ARR/"+currentReportTitleFolder;
}

function createFolderFromName(name) {
  var folder = name;
  if(folder) {
    folder = folder.replace(/\//g, ' ').replace(/\\/g, ' ').replace(/-/g, ' '); // Remove special characters like forward slash, backslash and dash
    folder = folder.replace(/ +(?= )/g,'').trim(); // Remove multiple spaces and trim
  }
  return folder;
}

AjaxCallForFormSubmission=function(processTracker) {
  console.log('Step 2: Run Rulesets');

  // Run Custom Ruleset
  //var results = axs.Audit.run(eval('({"XPATH_ROOT":"//div[@class=\'nuweb-itemtile nuweb-itemtile-small\']"})')); // 
  processTracker.results = {};

  console.log('Running Custom Ruleset...');
  eval(processTracker.customRuleset); // Needs to be in this scope
  var jsonParameters = {};
  if(processTracker.xpathRoot !== '') {
    console.log('using XPATH_ROOT to run WAE :'+processTracker.xpathRoot);
    jsonParameters.XPATH_ROOT = processTracker.xpathRoot;
  }
  processTracker.results.custom = axs.Audit.run(jsonParameters);
  // For circlar JSON comment at https://wiki.vip.corp.ebay.com/display/BUYEXP/Running+Wa11y+through+the+Command+Line+like+Go+Quickly?focusedCommentId=593600405&refresh=1554851093044#comment-593600405
  // Remove element references
  for(var i=0; i<processTracker.results.custom.length; i++) {
    for(var ii=0; ii<processTracker.results.custom[i].elements.length; ii++) {
      processTracker.results.custom[i].elements[ii].element = "";
    }
  }

  //console.log('Custom Ruleset Complete... results:'+JSON.stringify(processTracker.results.custom));

  console.log('Running aXe Ruleset...');
  eval(processTracker.axeRuleset); // Needs to be in this scope
  // Might be nice to make an ajax call here to change the rules list
  var rulesToRun = ['area-alt','accesskeys','aria-allowed-attr','aria-required-attr','aria-required-children','aria-required-parent','aria-roles','aria-valid-attr-value','aria-valid-attr','audio-caption','blink','button-name','bypass','checkboxgroup','color-contrast','document-title','duplicate-id','empty-heading','heading-order','html-lang-valid','image-redundant-alt','input-image-alt','label','layout-table','link-name','marquee','meta-refresh','meta-viewport','meta-viewport-large','object-alt','radiogroup','scope-attr-valid','server-side-image-map','tabindex','table-duplicate-name','td-headers-attr','th-has-data-cells','valid-lang','video-caption','video-description'
  //'definition-list','dlitem',
  //'frame-title','frame-title-unique',
  //'html-has-lang',
  //'image-alt',
  //'label-title-only',
  //'list','listitem',
  //'region',
  ];

  processTracker.results.axe = [];
  axe.run({ runOnly: {type: 'rule', values: rulesToRun }}, ProcessTrackerCallback(processTracker, rulesToRun));
}

ProcessTrackerCallback=function(processTracker, rulesToRun) {
  return function(err, axeresults) {
    // Could potentially do a simple assignment, but we filter axeresults client side so POST works
    // processTracker.results.axe - axeresults;

    //console.log('aXe rule completed... axeresults.violations.length:'+axeresults.violations.length+' axeresults.violations:'+JSON.stringify(axeresults.violations));

    var rulesWithViolation = [];

    if(axeresults.violations) {
      for(var i=0; i<axeresults.violations.length; i++) {
        var axerule = {};
        axerule.ruleName = axeresults.violations[i].id;
        axerule.violations = [];
        axerule.violations.push(axeresults.violations[i]);
        processTracker.results.axe.push(axerule);
        rulesWithViolation[axerule.ruleName] = true;
      }
    }

    for(var i=0; i<rulesToRun.length; i++) { // Show that the test was run
      if(!rulesWithViolation[rulesToRun[i]]) {
        var axerule = {};
        axerule.ruleName = rulesToRun[i];
        axerule.violations = [];
        processTracker.results.axe.push(axerule);
      }
    }

    ProcessResults(processTracker);
  };
}

ProcessResults=function(processTracker) {
  console.log('Results:'+JSON.stringify(processTracker.results));

  // HIGHLIGHT ELEMENTS ON PAGE
  console.log('Step 3: Highlight Element Failures');
  var elementCounterTotal = highlighElementsOnPage(processTracker.results);
	
  // UPLOAD RESULTS
  console.log('Step 4: Upload Results');
  if(elementCounterTotal > 0
  || confirm('There were 0 errors.  Do you still want to upload the results?')) {
	  
    // 1. Turn results object into string
    // 2. Remove special unicode characters like %u2026
    // %u2026 found on http://www.ebay.com/gds/Cool-Pet-Gadgets-/10000000205193122/g.html?_trkparms=clkid%3D2928417554041768498 page.
    // http://stackoverflow.com/questions/20856197/remove-non-ascii-character-in-string
    // 3. HTML Escape (aka encode) for POST request
    processTracker.resultsString = escape(JSON.stringify(processTracker.results).replace(/[^\x00-\x7F]/g, ""));  
	  
    UploadJSONResultsRequest(processTracker, UploadJSONResultsCompleted);
  }  
}

function highlighElementsOnPage(combinedResults) {
	var elementCounterTotal = 0;
	var elementCounter = 0;
	var divs = [];
	
	var customresults = combinedResults.custom;
	for(var i=0; i<customresults.length; i++) {
	  var customresult = customresults[i];
	  if(customresult.passOrFail === 'FAIL') {
	    elementCounterTotal = elementCounterTotal + customresult.elements.length;
	  }
	}
	for(var i=0; i<customresults.length; i++) {
	  var customresult = customresults[i];
	  if(customresult.passOrFail === 'FAIL') {
		for(var ii=0; ii<customresult.elements.length; ii++) {
		  elementCounter++;
	      var elementII = customresult.elements[ii];
		  
          // HIGHLIGHT INDIVIDUAL ELEMENT BY XPATH
          var element = CFGetElementByXpath(CFDocumentFunction(), elementII.elementXPATH, CFDocumentFunction());
          if(element) {
            console.log('highlighting custom ruleset failure:');
            var transparentInnerHTML = 'Element '+elementCounter+' of '+elementCounterTotal+'<br><b>rule:</b>'+customresult.rule;
            var visibleInnerHTML = transparentInnerHTML+'<br><b>ruleCode:</b>'+customresult.ruleCode+' <br><b>ruleComplianceLevel:</b>'+customresult.ruleComplianceLevel;
            highlightElement(element, divs, transparentInnerHTML, visibleInnerHTML);
          } else {
            console.log('Cannot highlight custom ruleset failure (elementNotFound) xpath:'+elementII.elementXPATH);
          }
		}
	  }
	}
	
	elementCounterTotal = 0;
	elementCounter = 0;
	
	var axeresults = combinedResults.axe;
	for(var i=0; i<axeresults.length; i++) {
	  var axeresult = axeresults[i];
	  var violations = axeresult['violations'];
	  if(violations.length == 1) {
	    var axeRuleViolation = violations[0]; // 1 rule run at a time, so violations only has 1 element
	    var nodes = axeRuleViolation['nodes']; // elements
	    elementCounterTotal = elementCounterTotal + nodes.length;
	  }
	}
	for(var i=0; i<axeresults.length; i++) {
	  var axeresult = axeresults[i];
	  var violations = axeresult['violations'];
	  if(violations.length == 1) {
	    var axeRuleViolation = violations[0]; // 1 rule run at a time, so violations only has 1 element
        var ruleID = axeRuleViolation['id'];
	    var nodes = axeRuleViolation['nodes']; // elements
	    for(var ii=0; ii<nodes.length; ii++) {
		  elementCounter++;
	      var node = nodes[ii];
          var element = CFDocumentFunction().querySelector(node.target); // Selector
          if(element) {
            console.log('highlighting axe ruleset failure:');
            var transparentInnerHTML = 'Element '+elementCounter+' of '+elementCounterTotal+'<br><b>rule:</b>'+axeRuleViolation.id;
            var visibleInnerHTML = transparentInnerHTML;
            highlightElement(element, divs, transparentInnerHTML, visibleInnerHTML);
          } else {
            console.log('Cannot highlight axe ruleset failure (elementNotFound) selector:'+node.target);
          }
	    }
	  }
	}
	
	return elementCounterTotal;
}

function highlightElement(element, divs, transparentInnerHTML, visibleInnerHTML) {
  try {
    if(element) {
	  
      // Create Overlay
      var d=CFOverlayCreationUsingDiv(element, "_arr", "#FFCCFF", "#FFCCFF");
      d.style.outline = "20px solid red";

      // Add Event Listener to cycle through Overlays
      CFOverlayAddEventListenerForClick(d, divs, transparentInnerHTML, visibleInnerHTML, "_arr");

      element.parentNode.appendChild(d);
      divs.push(d);
			
     }
  } catch (err) {
    console.log('element:'+JSON.stringify(element));
    console.log(err.stack);
  }
}

// AJAX CALLS AND REQUESTS

function DownloadCustom(processTracker, finishedFunction) {
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4) {
			if(xhttp.status == 200) {
				try {
					//console.log(xhttp.responseText);
					processTracker.customRuleset = xhttp.responseText;
				} catch (err) {
			        console.log(err);
				}
			} else {
			    console.log("Report Upload Failed.")
			}
			
			finishedFunction(processTracker);
		}
      };
	  
	  xhttp.open("GET", CustomRulesetImportJSURL, true);
	  xhttp.setRequestHeader("Content-Type", "application/json");
      xhttp.send();
}

function DownloadAXE(processTracker, finishedFunction) {
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4) {
			if(xhttp.status == 200) {
				try {
					//console.log(xhttp.responseText);
					processTracker.axeRuleset = xhttp.responseText;
				} catch (err) {
			        console.log(err);
				}
			} else {
			    console.log("Report Upload Failed.")
			}
			
			finishedFunction(processTracker);
		}
      };
	  
	  xhttp.open("GET", AXERulesetImportJSURL, true);
	  xhttp.setRequestHeader("Content-Type", "application/json");
      xhttp.send();
}
	
function UploadJSONResultsRequest(processTracker, finishedFunction) {
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4) {
			if(xhttp.status == 200) {
				try {
					console.log(xhttp.responseText);
			        var response = JSON.parse(xhttp.responseText);
			        processTracker.reportLocation = response.reportLocation;
				} catch (err) {
			        console.log(err);
				}
			} else {
			    console.log("Report Upload Failed.")
			}
			
			finishedFunction(processTracker);
		}
      };
	  
	  // GET ADDITIONAL PAGE INFORMATION
	  var h1 = CFGetH1InnerText();

      var title = CFGetTitleInnerText();
	  
	  var params = "&viewname="+processTracker.viewname+"&jiraprojectname="+processTracker.jiraprojectname+"&reporttitle="+processTracker.reporttitle+"&storagefolder="+processTracker.storagefolder+"&url="+escape(processTracker.url)+"&h1="+h1+"&title="+title+"&xpathRoot="+processTracker.xpathRoot+"&results="+processTracker.resultsString;
	  console.log("POST results to the server using query parameters:"+params); //insert a params=params
	  
	  xhttp.open("POST", "https://myserver.com/Server/rest/uploadJSONResults", true);
	  xhttp.setRequestHeader("Content-Type", "application/json");
      xhttp.send(JSON.stringify(params));
}

function UploadJSONResultsCompleted(processTracker) {
    console.log('Can automatically create a report at '+processTracker.storagefolder);
}