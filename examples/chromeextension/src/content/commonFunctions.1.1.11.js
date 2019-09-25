//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Scott Izu
// Last Modified: 9/24/2019
// Version 1.1.11
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

// WINDOWS AND TABS

// Open New Window
function CFOpenNewWindow(url, title, params) {
	var win = window.open(url, title, params);
	win.focus();
}

// Open in New Tab
function CFOpenNewTab(url) {
	var win = window.open(url, '_blank');
	win.focus();
}

// DOCUMENT ACCESS

// Case I:
//   If needed within multiple scope changes (ie Ajax)
//   set global variable via:
//   cfStoredDocument = CFDocumentFunction();
// Case II:
//   Used for most cases, document is set appropriately
// Case III:
//   For Firefox, right click menu, document is null
//   However, gContextMenu.target.ownerDocument is set appropriately
var cfStoredDocument;
function CFDocumentFunction() {
  if(!(typeof cfStoredDocument === 'undefined')) {  
    return cfStoredDocument;
  } else if(typeof gContextMenu === 'undefined') {
    return document;
  } else {
    return gContextMenu.target.ownerDocument;
  }
}

// DOCUMENT META DATA FUNCTIONS

// Returns all page source including comments
function CFGetOuterHTMLAndComments(doc) {
  if(typeof doc === 'undefined') {
    doc = CFDocumentFunction();
  }
  
  var outerHTMLAndComments = doc.documentElement.outerHTML;
  var nodes = doc.childNodes;
  for (var i = 0; i < nodes.length; i++) {
    if (nodes[i].nodeType === 8) {
		outerHTMLAndComments = outerHTMLAndComments + '<!--' + nodes[i].nodeValue+'-->';
	    //alert("nodes[i].nodeValue:"+nodes[i].nodeValue);
    }
  }
  //alert('outerHTMLAndComments:'+outerHTMLAndComments);
  return outerHTMLAndComments;
}

function CFGetHighlightedText() {
	var text = "";
    if (window.getSelection) {
        text = window.getSelection().toString();
    } else if (CFGetDocument().selection && CFGetDocument().selection.type != "Control") {
        text = CFGetDocument().selection.createRange().text;
    }
	return text;
}

function CFGetHighlightedElement() {
    if (window.getSelection) {
        var sel = window.getSelection();
       return sel.anchorNode.parentNode;
    } else if (CFGetDocument().selection && CFGetDocument().selection.type != "Control") {
        return CFGetDocument().selection.createRange().parentElement();
    }
}

function CFGetTitleInnerText(doc) {
  if(typeof doc === 'undefined') {
    doc = CFDocumentFunction();
  }
  
  var elements = doc.getElementsByTagName("title");
  if(elements.length == 1) {
    return CFtrim(CFGetInnerText(elements[0]));
  }
}

function CFGetH1InnerText(doc) {
  if(typeof doc === 'undefined') {
    doc = CFDocumentFunction();
  }
  
  var elements = doc.getElementsByTagName("h1");
  if(elements.length == 1) {
    return CFtrim(CFGetInnerText(elements[0]));
  }
}

function CFGetURL(doc) {
  if(typeof doc === 'undefined') {
    doc = CFDocumentFunction();
  }
  return doc.location.href;
}

function CFGetInnerText(element) {
  return element.textContent || element.innerText;
}

// XPATH HELPER FUNCTIONS

function CFGetBasicXpathByElement(doc, element) {
  if(typeof doc === 'undefined' || doc == null) {
    doc = CFDocumentFunction();
  }
  
	if (element.tagName == 'HTML')
		return '/HTML[1]';
    if (element===doc.body)
        return '/HTML[1]/BODY[1]';

    var ix= 0;
    var siblings= element.parentNode.childNodes;
    for (var i= 0; i<siblings.length; i++) {
        var sibling= siblings[i];
        if (sibling===element)
            return CFGetBasicXpathByElement(doc, element.parentNode)+'/'+element.tagName+'['+(ix+1)+']';
        if (sibling.nodeType===1 && sibling.tagName===element.tagName)
            ix++;
    }
}

// Added: 08/29/2017
function CFGetAdvancedXpathByElement(doc, element) {
  if(typeof doc === 'undefined' || doc == null) {
    doc = CFDocumentFunction();
  }
	if (element.tagName == 'HTML')
		return '/HTML[1]';
    if (element===doc.body)
        return '/HTML[1]/BODY[1]';

    var ix= 0;
    var siblings= element.parentNode.childNodes;
    for (var i= 0; i<siblings.length; i++) {
        var sibling= siblings[i];
        if (sibling.nodeType===1 && sibling.tagName===element.tagName) {
            ix++;
        }
        if (sibling===element) {
            var parentXPATH = CFGetAdvancedXpathByElement(doc, element.parentNode);
            var xpath = parentXPATH +'/'+element.tagName+'['+ix+']';
            if(element.id != null && !(CFIsEmpty(element.id))) {
            	xpath = xpath + "[@id='"+element.id+"']";
            } else if(element.name != null && !(CFIsEmpty(element.name))) {
            	xpath = xpath + "[@name='"+element.name+"']";
            } else if(element.className != null && !(CFIsEmpty(element.className)) && element.className.indexOf(' ') == -1) {
            	xpath = xpath + "[@class='"+element.className+"']";
            }
            //if(element.className != null && !(CFIsEmpty(element.className))) {
            //	xpath = xpath + "[@class='"+element.className+"']";
            //}
            return xpath;
        }
    }
}

function CFGetElementByXpath(doc, path, element) {
  if(typeof doc === 'undefined' || doc == null) {
    doc = CFDocumentFunction();
  }
  if(typeof element === 'undefined' || element == null) {
    element = CFDocumentFunction();
  }
  
  return doc.evaluate(path, element, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}

// STRING MANIPULATION

function CFIsEmpty(aString) {
	return (aString == null) || (aString.trim().length == 0);
}

function CFtrim(s) {
    if (typeof s === "string") {
        s = s.replace(/^\s+|\s+$/g, "");
    }
    return s;
}

// MODAL AND DIV CREATION HELPER FUNCTIONS

function CFCreateModal(doc, modalTitle, modalContent) {
  if(typeof doc === 'undefined' || doc == null) {
    doc = CFDocumentFunction();
  }
  if(typeof modalTitle === 'undefined') {
    modalTitle = 'Default Title';
  }
  if(typeof modalContent === 'undefined') {
    modalContent = 'Hello World';
  }
  
  // MODAL PATTERN
  var modal = doc.createElement("div");
  modal.id="cf_modal_wrapper";
  var modalHTML = "";
  modalHTML = modalHTML + "\n<div id='cf_modal_div' aria-labelledby='cf_dialog_title' class='cf__dialog' role='dialog' style='background-color: rgba(0,0,0,0.85);position: fixed; top:0; left:0; height:100%; width:100%; z-index:1000;'>";
  modalHTML = modalHTML + "\n  <div id='cf_modal_document' role='document' style='background-color:#fff;padding:15px;border:2px solid #333;-webkit-border-radius:6px;position:fixed;'>";
  modalHTML = modalHTML + "\n    <div role='banner'>"; // Not header because some pages style header
  modalHTML = modalHTML + "\n      <h2 id='cf_dialog_title'>"+modalTitle+"</h2>";
  modalHTML = modalHTML + "\n    </div>";
  modalHTML = modalHTML + "\n    <br>";
  modalHTML = modalHTML + "\n    <div>";
  modalHTML = modalHTML + "\n      <!-- dialog content goes here -->";
  modalHTML = modalHTML + modalContent;
  modalHTML = modalHTML + "\n      <!-- dialog content goes here -->";
  modalHTML = modalHTML + "\n    </div>";
  modalHTML = modalHTML + "\n    <br><button id='cf_dialog_close_button' aria-label='Close Dialog' class='dialog__close'>Close</button>";
  modalHTML = modalHTML + "\n  </div>";
  modalHTML = modalHTML + "\n</div>";
  modal.innerHTML = modalHTML;
  doc.body.appendChild(modal);
  
  // CLOSE BUTTON
  var closeButton = doc.getElementById('cf_dialog_close_button');
  closeButton.addEventListener("click", function(event){
    doc.body.removeChild(modal);
  });
  
  // ADD MODAL - SEE ABOVE - MODAL PATTERN
  var modalDiv = doc.getElementById('cf_modal_div');
  var documentDiv = doc.getElementById('cf_modal_document');
  //alert('modalDiv.clientHeight:'+modalDiv.clientHeight);
  documentDiv.style.marginTop = ''+(modalDiv.clientHeight/2-documentDiv.clientHeight/2)+'px';
  documentDiv.style.marginLeft = ''+(modalDiv.clientWidth/2-documentDiv.clientWidth/2)+'px';
  //documentDiv.style.top = modalDiv.clientHeight(); // margin-top:20px; 
  //alert('Modal attached');
  
  return modal;
}

function CFGenerateJIRAHTML(projectID, projectKey, componentName) {
	var jiraURL = "http://jirap.com"; // Modify the JIRA URL appropriately
	var customFieldKey = "customfield_11111"; // Add multiple custom fields and values if necessary
	var customFieldValue = "22222";
	return "<a id='supportLnk' target='_blank' href='"+encodeURI(jiraURL+"/secure/CreateIssueDetails!init.jspa?pid="+projectID+"&amp;"+customFieldKey+"="+customFieldValue+"&amp;issuetype=1&amp;description=Component: "+componentName)+"'>File a Bug for "+projectKey+"</a>";
}

// OVERLAY HELPER FUNCTIONS

CFOverlayCreationUsingDiv=function(e, suffix, colorBack, colorOutline){
  var d=CFDocumentFunction().createElement("div");

  d.id = e.id+suffix;
  d.name = e.name+suffix;
  //d.className = e.className;
  
  // Set style, width and height, top and left offset based off e
  d.style.cssText=e.style.cssText;
  d.style.width=e.clientWidth+"px";
  d.style.height=e.clientHeight+"px";
  d.style.top=e.offsetTop+"px";
  d.style.left=e.offsetLeft+"px";
  
  // Add extra overlay styling
  d.style.position="absolute";
  d.style.display="block";
  d.style.textAlign="center";
  d.style.fontSize="12px";
  
  // Set Minimum Width/Height and add text (undefined, auto, 0)
  var w=e.clientWidth;
  if((!w)||((w!="auto")&&(w<150))){
	d.style.width="150px";
  }
  var h=e.clientHeight;
  if((!h)||((h!="auto")&&(h<50))){
    d.style.height="50px";
  }
  
  // Set display none
  if(e.style.display == 'none' || e.innerHTML=='' || e.style.height=='0px' || e.style.width=='0px') {
    d.style.display = "none";
  }
  
  d.style.border="2px solid "+colorOutline; // Pink Outline #A7A
  d.style.background=colorBack; // Blue Background #ADF
  if(d.style.display == 'none') {
    d.style.border="2px dashed #CCC"; // Gray Background
	d.style.background="#DDD"; // Gray Background
  }
  
  return d;
};

// This event handler allows click functionality for overlays
// Divs is used to cycle through overlays when they stack on one another
CFOverlayAddEventListenerForClick=function(d, divs, transparentInnerHTML, visibleInnerHTML, suffix) {
  var opacity1 = .5;
  var opacity2 = 1;
  d.innerHTML=transparentInnerHTML;
  d.style.opacity = opacity1;
  d.style.zIndex=10+divs.length; // stack the new divs
  d.addEventListener('click', function(event) {
    var elementClicked = event.target || event.srcElement;
    if(elementClicked.id.indexOf(suffix) == -1) { // Allow other elements and click handlers within the innerHTML
		return;
	}
    if(this.style.opacity==opacity1){ // Visible
	  this.style.opacity=opacity2;
	  this.style.zIndex=10+divs.length; // bring to front
	  this.innerHTML=visibleInnerHTML;
	}else{ // Transparent
	  this.style.opacity=opacity1;
	  var thisIndex = divs.indexOf(this);
	  divs.splice(thisIndex, 1); // remove this
	  divs.splice(0, 0, this); // insert at position 0, remove 0 items
	  for(i=0;i<divs.length;i++){
	    divs[i].style.zIndex=10+i;
	  }
	  this.innerHTML=transparentInnerHTML;
	}
  });
};

CFRemoveOverlays=function(tagName, suffix){
  var divsToRemove = [];
  var divs = CFDocumentFunction().getElementsByTagName(tagName);
  for(var i = 0; i < divs.length; i++){
    var div = divs[i];
    var id = div.id;
	//console.log('ID:'+id);
    if(id.indexOf(suffix, id.length - suffix.length) !== -1){
	  divsToRemove.push(div);
    }
  }
  
  for(var i=0; i<divsToRemove.length; i++) {
    //console.log('ID:'+divsToRemove[i].id);
    divsToRemove[i].parentNode.removeChild(divsToRemove[i]);
  }
}