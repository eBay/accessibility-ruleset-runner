CommonFunctionsImportJS = "content/commonFunctions.1.1.11.js";
RulesetRunnerImportJS = "content/rulesetRunner.js";
HeadingsParserImportJS = "content/headingsParser.3.1.1.js";

// To get all these menus to work, there are a few differences between Firefox and Chrome Extensions
// The GetDocument function needs to be used.  One could potentionally determine if Firefox or Chrome was being used
// The other is to Open in a New Tab like target='_blank' on anchors which should be done differently based on context
// gContextMenu is defined when using Firefox extension and in this case, GetDocument should return gContextMenu.gContextMenu.target.ownerDocument instead of document

chrome.contextMenus.create({
  id: 'Accessibility',
  title: "Accessibility",
  contexts:["all"]
});
  chrome.contextMenus.create({
    id: 'RulesetRunner',
    parentId: 'Accessibility',
    title: "Ruleset Runner",
    contexts:["all"],
  });
    chrome.contextMenus.create({
      id: 'RulesetRunnerAddOverlays',
      parentId: 'RulesetRunner',
      title: "Add Overlays",
      contexts:["all"],
      onclick: function(info, tab){
        chrome.tabs.executeScript(tab.id, {file:CommonFunctionsImportJS}, function() {});
        chrome.tabs.executeScript(tab.id, {file:RulesetRunnerImportJS}, function() {
          chrome.tabs.executeScript(tab.id, {code:"RulesetRunner();"});
	    });
      }
    });
  chrome.contextMenus.create({
      id: 'RulesetRunnerRemoveOverlays',
      parentId: 'RulesetRunner',
      title: "Remove Overlays",
      contexts:["all"],
      onclick: function(info, tab){
        chrome.tabs.executeScript(tab.id, {file:CommonFunctionsImportJS}, function() {
          chrome.tabs.executeScript(tab.id, {code:"CFRemoveOverlays('div', '_arr');"});
        });
      }
  });
  chrome.contextMenus.create({
    id: 'HeadingsParser',
    parentId: 'Accessibility',
    title: "Headings Parser",
    contexts:["all"],
  });
    chrome.contextMenus.create({
      id: 'HeadingsParserAddOverlays',
      parentId: 'HeadingsParser',
      title: "Add Overlays",
      contexts:["all"],
      onclick: function(info, tab){
        chrome.tabs.executeScript(tab.id, {file:CommonFunctionsImportJS}, function() {});
        chrome.tabs.executeScript(tab.id, {file:HeadingsParserImportJS}, function() {
          chrome.tabs.executeScript(tab.id, {code:"EHPAddOverlays();"});
        });
      }
    });
  chrome.contextMenus.create({
      id: 'HeadingsParserRemoveOverlays',
      parentId: 'HeadingsParser',
      title: "Remove Overlays",
      contexts:["all"],
      onclick: function(info, tab){
        chrome.tabs.executeScript(tab.id, {file:CommonFunctionsImportJS}, function() {
          chrome.tabs.executeScript(tab.id, {code:"CFRemoveOverlays('div', '_ehp');"});
        });
      }
  });