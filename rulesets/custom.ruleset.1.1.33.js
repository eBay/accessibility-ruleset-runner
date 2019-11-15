//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Scott Izu, Sean Gates, Ian McBurnie
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
 
// Print a message at the bottom of the page
AccessibilityEngine = function(jsonParameters) {
	var results = [];

	try {

		if (jsonParameters == null) {
			jsonParameters = {};
		}
		
		// Configure rules to run
		if (jsonParameters.rulesToRun == null) {
			jsonParameters.rulesToRun = [];
			jsonParameters.rulesToRun[0] = "H42 Heading Hierarchy";
			jsonParameters.rulesToRun[1] = "H44 Input Tag Label";
			jsonParameters.rulesToRun[2] = "H32 Form Submit Button";
			jsonParameters.rulesToRun[3] = "H37 Image Tag Alt Attribute";
			jsonParameters.rulesToRun[4] = "H24 Image Map Alt Attribute";
			jsonParameters.rulesToRun[5] = "H35 Applet Tag Alt Attribute";
			jsonParameters.rulesToRun[6] = "H53 Object Tag Alt Attribute";
			jsonParameters.rulesToRun[7] = "H64 IFrame Tag Title Attribute";
			jsonParameters.rulesToRun[8] = "H30 Opening New Windows";
			jsonParameters.rulesToRun[9] = "H30 Links Repeated";
			jsonParameters.rulesToRun[10] = "H75 Unique Anchor IDs";
			jsonParameters.rulesToRun[11] = "H46 Embed Tag";
			jsonParameters.rulesToRun[12] = "H25 Title Tag";
			jsonParameters.rulesToRun[13] = "H57 HTML Tag Lang Attribute";
			jsonParameters.rulesToRun[14] = "Validate Skip to Main Content";
			jsonParameters.rulesToRun[15] = "H42 H1 Heading";
		}

		// Configure xpath root
		if(jsonParameters.xpathRoot != null) { // Assign from a javascript call
			xpathRoot = jsonParameters.xpathRoot;
			xpathRootPrefix = ".";
		} else if (jsonParameters.XPATH_ROOT != null) { // Assign from an xpath
			axs.properties.XPATH_ROOT = jsonParameters.XPATH_ROOT;
			var xpathElements = getElementsByXpath(WAEDocumentFunction(),
					jsonParameters.XPATH_ROOT, WAEDocumentFunction());
			var xpathElement = xpathElements.iterateNext();
			if (xpathElement != null) {
				xpathRoot = xpathElement;
				xpathRootPrefix = ".";
			} else { // In case WAE is run twice in a row from Firefox Right Click
				xpathRoot = WAEDocumentFunction();
				xpathRootPrefix = "";
			}
		} else { // In case WAE is run twice in a row from Firefox Right Click
		  xpathRoot = WAEDocumentFunction();
		  xpathRootPrefix = "";
		}

		// Run rules
		for ( var ruleNum = 0; ruleNum < jsonParameters.rulesToRun.length; ruleNum++) {
			var result = null;
			try {
				var ruleToRun = jsonParameters.rulesToRun[ruleNum];
				if(!ruleToRun) {
				} else if (ruleToRun == "H25 Title Tag") {
					if (jsonParameters.XPATH_ROOT == null) {
						result = ruleH25TitleElement();
					}
				} else if (ruleToRun == "H57 HTML Tag Lang Attribute") {
					if (jsonParameters.XPATH_ROOT == null) {
						result = ruleH57LangAttribute();
					}
				} else if (ruleToRun == 'Validate Skip to Main Content') {
					if (jsonParameters.XPATH_ROOT == null) {
						result = ruleSkiptoMainContent();
					}
				} else if (ruleToRun == 'H42 H1 Heading') {
					if (jsonParameters.XPATH_ROOT == null) {
						result = ruleH42HeadingMarkupTags();
					}
				} else if (ruleToRun == "H42 Heading Hierarchy") {
					result = ruleH42HeadingSkipLinks();
				} else if (ruleToRun == "H44 Input Tag Label") {
					result = ruleH44PresenceOfLabelforInputTag();
				} else if (ruleToRun == "H32 Form Submit Button") {
					result = ruleH32FormSubmitBtn();
				} else if (ruleToRun == "H37 Image Tag Alt Attribute") {
					result = ruleH37PresenceOfAltInImage();
				} else if (ruleToRun == "H24 Image Map Alt Attribute") {
					result = ruleH24PresenceOfAltInImageMap();
				} else if (ruleToRun == "H35 Applet Tag Alt Attribute") {
					result = ruleH35altTextForApplet();
				} else if (ruleToRun == "H53 Object Tag Alt Attribute") {
					result = ruleH53altTextForObject();
				} else if (ruleToRun == "H64 IFrame Tag Title Attribute") {
					result = ruleH64FramesTitleAttribute();
				} else if (ruleToRun == "H30 Opening New Windows") {
					result = ruleH30OpeningNewWindows();
					result.message = axs.message; // Debug;
				} else if (ruleToRun == "H30 Links Repeated") {
					result = ruleH30LinksRepeated();
				} else if (ruleToRun == "H75 Unique Anchor IDs") {
					result = ruleH75uniqueIDs();
					result.message = axs.message; // Debug;
				} else if (ruleToRun == "H46 Embed Tag") {
					result = ruleH46EmbedElement();
				} else {
					result = createResult(ruleToRun, "222", "A");
				}
			} catch (ruleErr) {
				result = createResult(ruleToRun, "111", "A");
				addResultFailed(result, null, "111_A_0");
				console.log('111_A_0 ruleErr '+ruleErr.stack);
				result.message = ruleErr;
				//alert(ruleErr.stack);
			}
			if(result != null) {
				results.push(result);
			}
		}
	} catch (err) {
		results = [];
		var result = createResult("Error", "000", "A");
		addResultFailed(result, null, "000_A_0");
		console.log('000_A_0 err '+err.stack);
		result.message = err;
		results[0] = result;
		//alert(err.stack);
	}
	return results;
}

//PAGE LAYOUT
function ruleH25TitleElement(){
	var result = createResult("H25 Title Tag", "025", "A");
	var headElements = WAEDocumentFunction().getElementsByTagName("head");
	var titleElements = headElements && headElements[0] && headElements[0].getElementsByTagName("title");
	var numOfTitleElements = titleElements ? titleElements.length : 0;
	if(numOfTitleElements === 0) {
		var htmlElement = WAEDocumentFunction().getElementsByTagName("html")[0];
		return addResultFailed(result, htmlElement, "025_A_1");
	} else if (numOfTitleElements === 1) {
		return addResultPassed(result);
	} else {
		return addResultFailed(result, titleElements[0], "025_A_2");
	}
}

function ruleH57LangAttribute(){
	var result = createResult("H57 HTML Tag Lang Attribute", "057", "A");
	var htmlElement = WAEDocumentFunction().getElementsByTagName("html")[0];
	var lang = htmlElement.getAttribute('lang');
	if(lang != null && lang !== '') { // en for English, de for Germany
		return addResultPassed(result);
	} else {
		return addResultFailed(result, htmlElement, "057_A_1");
	}
}

function ruleSkiptoMainContent(){
	var result = createResult("Validate Skip to Main Content", "0Skip", "AA");
	var sourceElement = null;
	var targetNameOrId = "mainContent";
	var foundSourceInFirstTenAnchors = false;
	var sourceAnchor = null;
	var anchorElements = WAEDocumentFunction().getElementsByTagName("a"); // getElementsByTagName for non xpathroot rules
	for(var i=0; i<10; i++) {
		var anchor = anchorElements[i];
		try {
			if(anchor.href.endsWith("#mainContent")) {
				sourceAnchor = anchor;
				foundSourceInFirstTenAnchors = true;
			}
		} catch(err) {
		}
	}
	var failed = (foundSourceInFirstTenAnchors == false);
	if(failed) {
		var htmlElement = WAEDocumentFunction().getElementsByTagName("html")[0];
		return addResultFailed(result, htmlElement, "0Skip_AA_1");
	}
	
	var skipToMainContentInGerman = "Hauptinhalt anzeigen";
	var skipToMainContentInDutch = "Doorgaan naar hoofdinhoud";
	var skipToMainContentInChinese = String.fromCharCode(30452)
		+String.fromCharCode(25509)
		+String.fromCharCode(21040)
		+String.fromCharCode(20027)
		+String.fromCharCode(35201)
		+String.fromCharCode(20839)
		+String.fromCharCode(23481);
	// Char:30452, 25509, 21040, 20027, 35201, 20839, 23481
	// 直接到主要內容 means 'Directly to main content'
	var skipToMainContentInPolish = "Przejd" + String.fromCharCode(378) + " "
		+ "do "
		+ "g" + String.fromCharCode(322) + String.fromCharCode(243) + "wnej "
		+ "tre" + String.fromCharCode(347) + "ci";
	// Przejdź do głównej treści 
	// Char:80, 114, 122, 101, 106, 100, 378, 32
	// Char:100, 111, 32
	// Char:103, 322, 243, 119, 110, 101, 106, 32
	// Char:116, 114, 101, 347, 99, 105
	var sourceText = getInnerText(sourceAnchor);
	failed = (sourceText.indexOf("Skip to main content") == -1) // Should be equals, not contains
		&& (sourceText.indexOf(skipToMainContentInGerman) == -1) // German
		&& (sourceText.indexOf(skipToMainContentInDutch) == -1) // Dutch
		&& (sourceText.indexOf(skipToMainContentInPolish) == -1) // Polish
		&& (sourceText.indexOf(skipToMainContentInChinese) == -1) // Hong Kong
		;
	if (failed) {
		var htmlElement = WAEDocumentFunction().getElementsByTagName("html")[0];
		return addResultFailed(result, htmlElement, "0Skip_AA_2");
	}
	
	var divNodes = getElementsByXpath(WAEDocumentFunction(), "//div[@id='mainContent' and @role='main' and @tabindex='-1']", WAEDocumentFunction());
	var firstDiv = divNodes.iterateNext();
	var mainNodes = getElementsByXpath(WAEDocumentFunction(), "//main[@id='mainContent' and @role='main' and @tabindex='-1']", WAEDocumentFunction());
	var firstMain = mainNodes.iterateNext();
	if(firstDiv == null && firstMain == null) {
		var htmlElement = WAEDocumentFunction().getElementsByTagName("html")[0];
		return addResultFailed(result, htmlElement, "0Skip_AA_3");
	}
	
	var mainContentNodes = getElementsByXpath(WAEDocumentFunction(), "//*[(@id='mainContent' or @name='mainContent')]", WAEDocumentFunction());
	var firstMainContent = mainContentNodes.iterateNext();
	var secondMainContent = mainContentNodes.iterateNext();
	if(secondMainContent != null) {
		var htmlElement = WAEDocumentFunction().getElementsByTagName("html")[0];
		return addResultFailed(result, htmlElement, "0Skip_AA_4");
	}
	
	return addResultPassed(result);
}

function ruleH42HeadingMarkupTags(){
	var result = createResult("H42 H1 Heading", "142", "A");
	var h1Elements = WAEDocumentFunction().getElementsByTagName("h1");
	if(h1Elements.length == 0) {
		var htmlElement = WAEDocumentFunction().getElementsByTagName("html")[0]; // getElementsByTagName for non xpathroot rules
		return addResultFailed(result, htmlElement, "142_A_1");
	} else if (h1Elements.length == 1) {
		var h1Element = h1Elements[0];
		var h1 = getInnerText(h1Element);
		if(h1 == null || h1.trim().length == 0) {
			return addResultFailed(result, h1Element, "142_A_3");
		} else {
			return addResultPassed(result);
		}
	} else {
		var h1Element = h1Elements[0];
		var otherH1Texts = "";
		for(var i=0; i<h1Elements.length; i++) {
			var otherH1Elements = h1Elements[i];
			var otherH1Elementsh1 = getInnerText(otherH1Elements);
			otherH1Texts = otherH1Texts + " Text:" + otherH1Elementsh1;
		}
		addResultFailed(result, h1Element, "142_A_2");
		addAdditionalFieldOtherH1Texts(result,otherH1Texts);
		return result;
	}
}

function ruleH42HeadingSkipLinks(){
	var result = createResult("H42 Heading Hierarchy", "042", "A");
	var xpath = xpathRootPrefix+"//*[local-name()='h1' or local-name()='h2' or local-name()='h3' or local-name()='h4' or local-name()='h5' or local-name()='h6']";
	var headingTags = getElementsByXpath(WAEDocumentFunction(), xpath, xpathRoot);
	var firstHeadingTag = headingTags.iterateNext();
	if(firstHeadingTag != null) {
		var firstTag = null;
		var firstNumber = null;
		var secondTag = firstHeadingTag.tagName;
		var secondNumber = Number(secondTag.substring(1,2));
		
		var nextHeadingTag = headingTags.iterateNext();
		while(nextHeadingTag != null) {
			firstTag = secondTag;
			firstNumber = secondNumber;
			var secondHeadingTag = nextHeadingTag;
			secondTag = secondHeadingTag.tagName;
			secondNumber = Number(secondTag.substring(1,2));
			var diff = secondNumber - firstNumber;
			var passed = (diff < 2);
			if (passed) {
				addResultPassed(result);
			} else {
				addResultFailed(result, secondHeadingTag, "042_A_1");
				addAdditionalFieldText(result, getInnerText(secondHeadingTag));
				addAdditionalFieldFirstTag(result, firstTag);
			}

			nextHeadingTag = headingTags.iterateNext();
		}
	}
	return result;
}

//FORMS
function ruleH44PresenceOfLabelforInputTag(){
	var result = createResult("H44 Input Tag Label", "044", "A");
	var inputElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//input[@type='password' or @type='text' or @type='checkbox' or @type='radio']", xpathRoot);
	var inputElement = inputElements.iterateNext();
	while(inputElement != null) {
		if(inputElementHasATitle(inputElement)) {
			// CASE 1: Input element has a title
			addResultPassed(result);
		} else if (inputElementHasAWrappedLabelWithText(inputElement)){
			// CASE 2: Input element has a wrapped label with text
			addResultPassed(result);
		} else if (inputElementHasAReferencedLabelWithText(inputElement)){
			// CASE 3: Input element has a referenced label with text
			addResultPassed(result);
		} else if (inputElementHasAAriaLabel(inputElement)){
			// CASE 4: Input element has a aria-label
			addResultPassed(result);
		} else if (inputElementLabelledWithAriaLabelledByWithText(inputElement)){
			// CASE 5: Input element labelled with aria-labelledby with text
			addResultPassed(result);
		} else if (isHidden(inputElement)){
			// CASE 6: Hidden is exempt
			addResultPassed(result);
		} else {
			addResultFailed(result, inputElement, "044_A_1");
		}
			
		inputElement = inputElements.iterateNext();
	}
	
	return result;
}

// CASE 1: Input element has a title
// You may add message like so to log each case: axs.message = axs.message + "CASE 1:";
function inputElementHasATitle(inputElement) {
	return !isEmpty(inputElement.title);
}

// CASE 2: Input element has a wrapped label with text
function inputElementHasAWrappedLabelWithText(inputElement) {
	var labelElement = findFirstAncestorByTagName(inputElement, "label");
	if(labelElement != null) {
		return !isEmpty(getInnerText(labelElement));
	}
	return false;
}

// CASE 3: Input element has a referenced label with text
function inputElementHasAReferencedLabelWithText(inputElement) {
	var inputID = inputElement.id;
	var referenceLabels = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//label[@for='" + inputID + "']", xpathRoot);
	var labelElement = referenceLabels.iterateNext();
	if(labelElement != null) {
		return !isEmpty(getInnerText(labelElement));
	}
	return false;
}

// CASE 4: Input element has a aria-label
function inputElementHasAAriaLabel(inputElement) {
	return !isEmpty(inputElement.getAttribute("aria-label"));
}

// CASE 5: Input element labelled with aria-labelledby with text
function inputElementLabelledWithAriaLabelledByWithText(inputElement) {
	var ariaLabelledByText = buildTextFromReferencedIDs(inputElement, "aria-labelledby");
	return !isEmpty(ariaLabelledByText);
}

// CASE 6: Hidden is exempt
// https://api.jquery.com/visible-selector/
// visibility:hidden and opacity:0 are considered visible by jQuery(":visible") selector
function isHidden(element) {
	// http://stackoverflow.com/questions/18062372/how-does-selenium-webdrivers-isdisplayed-method-work
	// A. Option or optgroup is shown iff enclosing select is shown (ignoring the
	// B. Image map elements are shown if image that uses it is shown, and
	// C.  Any hidden input is not shown.
	if((element.tagName.toLowerCase() == "input") && (element.type.toLowerCase() == "hidden")) {
		return true;
	}
	// D. // Any NOSCRIPT element is not shown. - uncomment to include
//	if(element.tagName.toLowerCase() == "noscript") {
//		return true;
//	}
	// E. // Any element with hidden visibility is not shown.
	if(findFirstAncestorByVisibilityHidden(element) != null) {
		return true;
	}
	// F. Any element with a display style equal to 'none' or that has an ancestor
	if(findFirstAncestorByDisplayNone(element) != null) {
		return true;
	}
	// G. // Any transparent element is not shown. - uncomment to include
//	if(element.style.opacity == 0) {
//		return true;
//	}
	// H. // Any element with the hidden attribute or has an ancestor with the hidden - uncomment to include
//	if(findFirstAncestorByHidden(element) != null) {
//		return true;
//	}
	// I. // Any element without positive size dimensions is not shown.
	if(element.offsetWidth == 0 && element.offsetHeight == 0) {
		return true;
	}
	// J. // Elements that are hidden by overflow are not shown. - uncomment to include
//	if(element.style.overflow == 'hidden') {
//		return true;
//	}
	return false;
}

function ruleH32FormSubmitBtn(){
	var result = createResult("H32 Form Submit Button", "032", "A");
	var formElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//form", xpathRoot);
	var formElement = formElements.iterateNext();
	while(formElement != null) {
		if(formElementFindNonHiddenInputElement(WAEDocumentFunction(), formElement, ".//*[(local-name()='input' and @type='submit') or (local-name()='input' and @type='image') or (local-name()='button' and @type='submit')]") != null) {
			// CASE 1: Form Element Has Non Hidden Submit Input Element
			addResultPassed(result);
		} else if (formElementFindNonHiddenInputElement(WAEDocumentFunction(), formElement, ".//*[local-name() = 'input' or local-name() = 'button']") == null) {
			// CASE 2: Form Element Has All Hidden
			addResultPassed(result);
		} else if (formElementIFrameHasSubmitButton(formElement)) {
			// CASE 3: Form Element IFrame Has Submit Button
			addResultPassed(result);
		} else if (formElementSubmitButtonOutsideForm(formElement)) {
			// CASE 4: Submit button outside of form
			addResultPassed(result);
		} else {
			addResultFailed(result, formElement, "032_A_1");
			addAdditionalFieldAction(result, formElement.action);
		}
		formElement = formElements.iterateNext();
	}
	return result;
}

// CASE 1: Form Element Has Non Hidden Submit Input Element // CASE 2: Form Element Has All Hidden
function formElementFindNonHiddenInputElement(documentToEvaluate, formElement, xpath) {
	var inputSubmits = getElementsByXpath(documentToEvaluate, xpath, formElement);
	var inputSubmit = inputSubmits.iterateNext();
	while(inputSubmit != null) {
		if(!isHidden(inputSubmit)) {
			return inputSubmit;
		}
		inputSubmit = inputSubmits.iterateNext();
	}
	return null;
}

// CASE 3: Form Element IFrame Has Submit Button
function formElementIFrameHasSubmitButton(formElement) {
	var iframeElements = getElementsByXpath(WAEDocumentFunction(), ".//iframe", formElement);
	var iframeElement = iframeElements.iterateNext();
	while(iframeElement != null) {
		try{
		    var innerDoc = iframeElement.contentDocument || iframeElement.contentWindow.document;
		    if(formElementFindNonHiddenInputElement(innerDoc, innerDoc, ".//*[(local-name()='input' and @type='submit') or (local-name()='input' and @type='image') or (local-name()='button' and @type='submit')]") != null) {
		    return true;
		    }
		} catch (err) {}
		iframeElement = iframeElements.iterateNext();
	}
}

//CASE 4: Submit button outside of form
function formElementSubmitButtonOutsideForm(formElement) {
	var formID = formElement.id;
	var submitButtons = getElementsByXpath(WAEDocumentFunction(), "//*[(local-name()='input' and @type='submit' and @form='"+formID+"') or (local-name()='input' and @type='image' and @form='"+formID+"') or (local-name()='button' and @type='submit' and @form='"+formID+"')]", WAEDocumentFunction());
	var submitButton = submitButtons.iterateNext();
	if(submitButton != null) {
		return true;
	}
}

//IMAGE
function ruleH37PresenceOfAltInImage(){
	var result = createResult("H37 Image Tag Alt Attribute", "037", "A");
	var imgElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//img", xpathRoot);
	var imgElement = imgElements.iterateNext();
	while(imgElement != null) {
		var anchorParent = findFirstAncestorByTagName(imgElement, "a");
		if ( // Any elements hidden from the Screen Reader can have empty or existing alt
				(imgElement.getAttribute("aria-hidden") === 'true')
				|| (imgElement.getAttribute("role") === 'presentation')
				|| (anchorParent != null && anchorParent.getAttribute("aria-hidden") === 'true' && anchorParent.getAttribute("tabindex") === '-1') 
			){
			addResultPassed(result);
		} else if((imgElement.src.indexOf("s.gif") != -1)
				|| (imgElement.src.indexOf("spacer.gif") != -1)
				|| (imgElement.src.indexOf("imgLatest.gif") != -1) ) {
			if(handlePresentationImage(imgElement, anchorParent)){
				addResultPassed(result);
			} else {
				addResultFailed(result, imgElement, "037_A_4");
				addAdditionalFieldSRC(result, imgElement.src);
				addAdditionalFieldAlt(result, imgElement.alt);
			}
		} else if ((imgElement.width*imgElement.height) <= 1) {
			if(handlePresentationImage(imgElement, anchorParent)){
				addResultPassed(result);
			} else {
				addResultFailed(result, imgElement, "037_A_1");
				addAdditionalFieldSRC(result, imgElement.src);
				addAdditionalFieldAlt(result, imgElement.alt);
				addAdditionalFieldWidth(result, imgElement.width);
				addAdditionalFieldHeight(result, imgElement.height);
			}
		} else {
			if(handleContentImage(imgElement, anchorParent)) {
				addResultPassed(result);
			} else {
				addResultFailed(result, imgElement, "037_A_2");
				addAdditionalFieldSRC(result, imgElement.src);
				addAdditionalFieldWidth(result, imgElement.width);
				addAdditionalFieldHeight(result, imgElement.height);
			}
		}
		
		imgElement = imgElements.iterateNext();
	}
	return result;
}
	
// CASE 1: Presentation Image Gif
// CASE 2: Presentation Image Size
function handlePresentationImage(imgElement, anchorParent) {
	if (imgElement.alt == null) { // Never will be null
		return true; // Should be false in future
	} else if(isEmpty(imgElement.alt)) {
		return true;
	} else if (isHidden(imgElement)) {
		return true;
	} else {
		return false;
	}
}

// CASE 3: Content Image
function handleContentImage(imgElement, anchorParent) {
	if (imgElement.alt == null) { // Never will be null
		return false; // Should be false in future
	} else if(!isEmpty(imgElement.alt)) {
		return true;
	} else if (anchorParent != null && !isEmpty(anchorParent.title)) {
		return true;
	} else if (anchorParent != null && !isEmpty(getInnerText(anchorParent))) {
		return true;
	} else if (isHidden(imgElement)) {
		return true;
	} else {
		return false;
	}
}

//ALT TAGS
function ruleH24PresenceOfAltInImageMap(){
	var result = createResult("H24 Image Map Alt Attribute", "024", "A");
	var areaElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//area", xpathRoot);
	var areaElement = areaElements.iterateNext();
	while(areaElement != null) {
		if(!isEmpty(areaElement.alt)) {
			addResultPassed(result);
		} else {
			addResultFailed(result, areaElement, "024_A_1");
			addAdditionalFieldHREF(result, areaElement.href);
		}
		
		areaElement = areaElements.iterateNext();
	}
	return result;
}
function ruleH35altTextForApplet(){
	var result = createResult("H35 Applet Tag Alt Attribute", "035", "A");
	var appletElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//applet", xpathRoot);
	var appletElement = appletElements.iterateNext();
	while(appletElement != null) {
		if(!isEmpty(appletElement.alt)) {
			addResultPassed(result);
		} else {
			addResultFailed(result, appletElement, "035_A_1");
			addAdditionalFieldCode(result, appletElement.code);
		}
		
		appletElement = appletElements.iterateNext();
	}
	return result;
}
function ruleH53altTextForObject(){
	var result = createResult("H53 Object Tag Alt Attribute", "053", "A");
	var objectElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//object", xpathRoot);
	var objectElement = objectElements.iterateNext();
	while(objectElement != null) {
		if(!isEmpty(objectElement.alt)) {
			addResultPassed(result);
		} else if(isHidden(objectElement)) {
			addResultPassed(result);
		} else {
			addResultFailed(result, objectElement, "053_A_1");
		}
		
		objectElement = objectElements.iterateNext();
	}
	return result;
}
function ruleH64FramesTitleAttribute(){
	var result = createResult("H64 IFrame Tag Title Attribute", "064", "A");
	var iframeElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//iframe", xpathRoot);
	var iframeElement = iframeElements.iterateNext();
	while(iframeElement != null) {
		if(!isEmpty(iframeElement.title)) { // BUG_FOUND: Had iframeElement.alt here
			addResultPassed(result);
		} else if(isHidden(iframeElement)) {
			addResultPassed(result);
		} else {
			addResultFailed(result, iframeElement, "064_A_1");
			addAdditionalFieldSRC(result, iframeElement.src);
		}
		
		iframeElement = iframeElements.iterateNext();
	}
	return result;
}

function ruleH46EmbedElement() {
	var result = createResult("H46 Embed Tag", "046", "A");
	var embedElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//embed", xpathRoot);
	var embedElement = embedElements.iterateNext();
	while(embedElement != null) {
		// Avoid second xpath search if possible
		if(getElementsByXpath(WAEDocumentFunction(), ".//noembed", embedElement).iterateNext() != null) { // child element
			addResultPassed(result);
		} else if (getElementsByXpath(WAEDocumentFunction(), "..//noembed", embedElement).iterateNext() != null){ // sibling element
			addResultPassed(result);
		} else {
			addResultFailed(result, embedElement, "046_A_1");
		}
		
		embedElement = embedElements.iterateNext();
	}
	return result;
}

//ANCHORS
function ruleH30OpeningNewWindows() {
	var result = createResult("H30 Opening New Windows", "030", "AA");
	var anchorElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//a[@target='_blank']", xpathRoot);

	var anchorElement = anchorElements.iterateNext();
	while(anchorElement != null) {
		var screenReaderLabel = buildLabel(anchorElement);
		var screenReaderDescription = buildDescription(anchorElement);
		var screenReaderText = screenReaderLabel + screenReaderDescription;
		
		if((screenReaderText.indexOf("opens") != -1) && (screenReaderText.indexOf("new window") != -1)) {
			addResultPassed(result);
		} else if((screenReaderText.indexOf("ge"+String.fromCharCode(246)+"ffnet") != -1) && (screenReaderText.indexOf("neuem Fenster") != -1)) { // German
			// Char: 103, 101, 246, 102, 102, 110, 101, 116
			// geöffnet
			addResultPassed(result);
		} else if ((screenReaderText.indexOf("geopend") != -1) && (screenReaderText.indexOf("nieuw venster") != -1)) { // Dutch
			addResultPassed(result);
		} else if ((screenReaderText.indexOf("otwiera") != -1) && (screenReaderText.indexOf("nowym oknie") != -1)) { // Polish
			addResultPassed(result);
		} else if ((screenReaderText.indexOf(String.fromCharCode(26032)) != -1) && (screenReaderText.indexOf(String.fromCharCode(31383)) != -1) && (screenReaderText.indexOf(String.fromCharCode(38283)) != -1)) { // Chinese
			addResultPassed(result);
			// Char:26371, 22312, (26032), 35222, (31383), 25110, 27161, 31844, 20013, (38283), 21855
			// 會在新視窗或標籤中開啟 means 'Will open in a new window or tab'
		} else {
			addResultFailed(result, anchorElement, "030_AA_1");
			addAdditionalFieldHREF(result, anchorElement.href);
			addAdditionalFieldScreenReaderLabel(result, screenReaderLabel);
			addAdditionalFieldScreenReaderDescription(result, screenReaderDescription);
		}
		
		anchorElement = anchorElements.iterateNext();
	}
	
	return result;
}

function ruleH30LinksRepeated() {
	var result = createResult("H30 Links Repeated", "130", "AA");
	var anchorElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//a[not(descendant::img)]", xpathRoot);
	var currentAnchorHref = [];
	var currentAnchorAnchorElement = [];
	
	var anchorElement = anchorElements.iterateNext();

	while(anchorElement != null) {
		var screenReaderLabel = buildLabel(anchorElement);
		var screenReaderDescription = buildDescription(anchorElement);
		var screenReaderText = screenReaderLabel + screenReaderDescription;
		var anchorHREF = anchorElement.href;
		var foundHref = null;
		var foundAnchorElement = null;
		if(!isEmpty(screenReaderText)) {
			foundHref =  currentAnchorHref[screenReaderText];
			foundAnchorElement = currentAnchorAnchorElement[screenReaderText];
		}
		
		if(isEmpty(screenReaderText)) {
			addResultPassed(result);
		} else if(isHidden(anchorElement)) {
			addResultPassed(result);
		} else if (foundHref == null || isEmpty(foundHref)) {
			// No conflict, put this in the array for a later potential conflict
			currentAnchorHref[screenReaderText] = removeHashParameters(removeQueryParameters(anchorHREF));
			currentAnchorAnchorElement[screenReaderText] = anchorElement;
			addResultPassed(result);
		} else if (foundHref.indexOf(removeHashParameters(removeQueryParameters(anchorHREF))) != -1) { // Should be equals, not contains
			addResultPassed(result);
		} else {
			addResultFailed(result, anchorElement, "130_AA_1");
			addAdditionalFieldHREF(result, anchorElement.href);
			addAdditionalFieldFoundHREF(result, foundHref);	
			addAdditionalFieldScreenReaderLabel(result, screenReaderLabel);
			addAdditionalFieldScreenReaderDescription(result, screenReaderDescription);
		}
		
		anchorElement = anchorElements.iterateNext();
	}
	
	return result;
}

function ruleH75uniqueIDs() {
	var result = createResult("H75 Unique Anchor IDs", "075", "AA");
	var anchorElements = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//a", xpathRoot);
	var currentAnchorHref = [];

	var anchorElement = anchorElements.iterateNext();
	while(anchorElement != null) {
		var anchorID = anchorElement.id;
		var anchorHREF = anchorElement.href
		var foundHref = null;
		if(!isEmpty(anchorID)) {
			foundHref =  currentAnchorHref[anchorID];
		}
		
		if(isEmpty(anchorID)) {
			addResultPassed(result);
		} else if (foundHref == null || isEmpty(foundHref)) {
			currentAnchorHref[anchorID] = anchorHREF;
			addResultPassed(result);
		} else {
			addResultFailed(result, anchorElement, "075_AA_1");
			addAdditionalFieldTitle(result, anchorElement.title);
			addAdditionalFieldText(result, getInnerText(anchorElement));
			addAdditionalFieldFoundHREF(result, foundHref);	
		}
		
		anchorElement = anchorElements.iterateNext();
	}
	
	return result;
}

//RESULT DATA
createResult=function (ruleName, ruleCode, ruleComplianceLevel){
	var result = {};
	result.passOrFail = axs.constants.AuditResult.PASS;
	result.rule = ruleName;
	result.ruleCode = ruleCode;
	result.ruleComplianceLevel = ruleComplianceLevel;
	result.passed = 0;
	result.elements = [];
	return result;
}

addResultPassed=function(result){
	result.passed = result.passed + 1;
	return result;
}
addResultFailed=function(result, element, elementFailureCode) {
	var elementIndex = result.elements.length;
	result.passOrFail = axs.constants.AuditResult.FAIL;
	result.elements[elementIndex] = {};
	result.elements[elementIndex].elementFailureCode = elementFailureCode;
	if(element) {
	  result.elements[elementIndex].element = element;
	  result.elements[elementIndex].elementTag = element.tagName.toLowerCase();
	  result.elements[elementIndex].elementClass = element.className;
	  result.elements[elementIndex].elementName = element.name;
	  result.elements[elementIndex].elementID = element.id;
	  result.elements[elementIndex].elementXPATH = getXpathByElement(element);
	
	  // http://stackoverflow.com/questions/442404/retrieve-the-position-x-y-of-an-html-element
	  var rect = element.getBoundingClientRect();
	  result.elements[elementIndex].elementXRight = rect.right;
	  result.elements[elementIndex].elementYTop = rect.top;
	  result.elements[elementIndex].elementXLeft = rect.left;
	  result.elements[elementIndex].elementYBottom = rect.bottom;
	}
	return result;
}
addAdditionalFieldAction=function(result, elementAction) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementAction = elementAction;
}
addAdditionalFieldAlt=function(result, elementAlt) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementAlt = elementAlt;
}
addAdditionalFieldCode=function(result, elementCode) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementCode = elementCode;
}
addAdditionalFieldFirstTag=function(result, elementFirstTag) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementFirstTag = elementFirstTag;
}
addAdditionalFieldFoundHREF=function(result, elementFoundHREF) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementFoundHREF = elementFoundHREF;
}
addAdditionalFieldHREF=function(result, elementHREF) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementHREF = elementHREF;
}
addAdditionalFieldOtherH1Texts=function(result, elementOtherH1Texts) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementOtherH1Texts = elementOtherH1Texts;
}
addAdditionalFieldScreenReaderDescription=function(result, elementScreenReaderDescription) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementScreenReaderDescription = elementScreenReaderDescription;
}
addAdditionalFieldScreenReaderLabel=function(result, elementScreenReaderLabel) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementScreenReaderLabel = elementScreenReaderLabel;
}
addAdditionalFieldSRC=function(result, elementSRC) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementSRC = elementSRC;
}
addAdditionalFieldText=function(result, elementText) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementText = elementText;
}
addAdditionalFieldTitle=function(result, elementTitle) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementTitle = elementTitle;
}
addAdditionalFieldWidth=function(result, elementWidth) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementWidth = elementWidth;
}
addAdditionalFieldHeight=function(result, elementHeight) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementHeight = elementHeight;
}
addAdditionalFieldErrorMessage=function(result, message) {
	var elementNumber = result.elements.length-1;
	result.elements[elementNumber].elementErrorMessage = message;
}

/*
 * JAVASCRIPT ELEMENT FUNCTIONS
 */
function findFirstAncestorByVisibilityHidden(element) {
	while(element != null){
		if(element.style.visibility == 'hidden') {
			return element;
		}
		element = element.parentElement;
	}
	return element;
}

function findFirstAncestorByDisplayNone(element) {
	while(element != null){
		if(element.style.display == 'none') {
			return element;
		}
		element = element.parentElement;
	}
	return element;
}

function findFirstAncestorByHidden(element) {
	while(element != null){
		if(element.hidden == true) {
			return element;
		}
		element = element.parentElement;
	}
	return element;
}

function findFirstAncestorByTagName(element, tagName) {
	while(element != null){
		if(element.tagName.toLowerCase() == tagName) {
			return element;
		}
		element = element.parentElement;
	} 
	return element;
}

function removeQueryParameters(href) {
	var hrefSplit = href.split('?'); // split on queryParameters
	return hrefSplit[0];
}

function removeHashParameters(href) {
	var hrefSplit = href.split('#'); // split on queryParameters
	return hrefSplit[0];
}

//1. aria-labelledby
//2. aria-label
//3. inner text
//@param element
//@returns
function buildLabel(element) {
	var ariaLabelledBy = buildTextFromReferencedIDs(element, "aria-labelledby");
	if(!isEmpty(ariaLabelledBy)) {
		return ariaLabelledBy;
	}
	
	var ariaLabel = element.getAttribute("aria-label");
	if(!isEmpty(ariaLabel)) {
		return ariaLabel;
	}
	
	return getInnerText(element);
}

//1. aria-describedby
//2. title
//@param element
//@returns
function buildDescription(element) {
	var ariaDescribedBy = buildTextFromReferencedIDs(element, "aria-describedby");
	if(!isEmpty(ariaDescribedBy)) {
		return ariaDescribedBy;
	}
	
	return element.title;
}

function buildTextFromReferencedIDs(element, attribute) {
	var referenceIDs = element.getAttribute(attribute);
	if(referenceIDs != null) {
		var referenceIDsText = "";
		var referenceIDsSplit = referenceIDs.split(/[\s]+/); // split on multiple spaces
		for(var i=0; i<referenceIDsSplit.length; i++) {
			var referenceLabels = getElementsByXpath(WAEDocumentFunction(), xpathRootPrefix+"//*[@id='" + referenceIDsSplit[i] + "']", xpathRoot);
			var labelElement = referenceLabels.iterateNext();
			if(labelElement != null) {
				var labelElementText = getInnerText(labelElement);
				if (!isEmpty(labelElementText)) {
					referenceIDsText = referenceIDsText + labelElementText;
				}
			}
		}
		return referenceIDsText;
	}
	return "";
}

// Later versions of Firefox stopped including visibility:hidden in innerText, so we check textContent first
function getInnerText(element) {
	return element.textContent || element.innerText;
}

function isEmpty(aString) {
	return (aString == null) || (aString.trim().length == 0);
}

function getElementsByXpath(documentToEvaluate, path, element) {
  try {
    return documentToEvaluate.evaluate(path, element, null, XPathResult.ANY_TYPE, null);
  } catch (err) {
    console.log('getElementsByXpath err:'+err.stack);
    console.log('typeof(element):'+(typeof(element)));
  }
}

function getXpathByElement(element) {
	if (element.tagName == 'HTML')
		return '/HTML[1]';
    if (element===WAEDocumentFunction().body)
        return '/HTML[1]/BODY[1]';

    var ix= 0;
    var siblings= element.parentNode.childNodes;
    for (var i= 0; i<siblings.length; i++) {
        var sibling= siblings[i];
        if (sibling.nodeType===1 && sibling.tagName===element.tagName) {
            ix++;
        }
        if (sibling===element) {
            var parentXPATH = getXpathByElement(element.parentNode);
            var xpath = parentXPATH +'/'+element.tagName+'['+ix+']';
            if(element.id != null && !(isEmpty(element.id))) {
            	xpath = xpath + "[@id='"+element.id+"']";
            } else if(element.name != null && !(isEmpty(element.name))) {
            	xpath = xpath + "[@name='"+element.name+"']";
            } else if(element.className != null && !(isEmpty(element.className)) && element.className.indexOf(' ') == -1) {
            	xpath = xpath + "[@class='"+element.className+"']";
            }
            return xpath;
        }
    }
}
var storedDocument;
function WAEDocumentFunction() {
  if(!(typeof storedDocument === 'undefined')) {
    return storedDocument;
  } else if(typeof gContextMenu === 'undefined') {
    return document;
  } else {
    return gContextMenu.target.ownerDocument;
  }
}

var xpathRoot;
var xpathRootPrefix;

var axs={
  Audit:{
    run:AccessibilityEngine
  },
  constants:{
    AuditResult:{
      PASS:"PASS",
      FAIL:"FAIL"
    }
  },
  properties:{
	  XPATH_ROOT:null
  },
  message:"",
  version:"1.1.33",
};
