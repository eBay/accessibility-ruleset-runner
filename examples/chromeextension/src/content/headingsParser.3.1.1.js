//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Scott Izu
// Last Modified: 9/24/2019
// Version 3.1.1
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

// ELEMENT MATCHING FUNCTIONS
EHPgetMatchingElements=function(tagName, byFunction, match) {
  var tags=CFDocumentFunction().getElementsByTagName(tagName);
  var els=[];
  for(var i=0;i<tags.length;i++){
    var e=tags[i];
    if(byFunction(e,match)){
      els.push(e);
    }
  }
  return els;
};

EHPbyIdContains=function(e, match){
  return e.id.indexOf(match)>-1;
};
EHPbyClassContains=function(e, match){
  return e.className.indexOf(match)>-1;
};
EHPbySrcContains=function(e, match){
  return e.src.indexOf(match)>-1;
};
EHPbyIdEquals=function(e, match){
  return e.id == match;
};
EHPbyClassEquals=function(e, match){
  return e.className == match;
};
EHPbyRoleContains=function(e, match){
  try {
    return e.getAttribute("role").indexOf(match)>-1;
  } catch(error){
    return false;
  }
};

// OVERLAY CONFIGURATIONS

// Highlights the placement by creating an overlay
EHPhighlighter=function(tagName, byFunction, match, name, colorBack, colorOutline, divs){
  try{
    var els=EHPgetMatchingElements(tagName, byFunction, match);
    for(var i=0;i<els.length;i++){
      var e=els[i];
	  
	  // Create Overlay
	  var d=CFOverlayCreationUsingDiv(e, "_ehp", colorBack, colorOutline);

      // Add Event Listener to cycle through Overlays
      var transparentInnerHTML = name;
      var visibleInnerHTML = name+"<br>"+d.style.width+" x "+d.style.height;
      CFOverlayAddEventListenerForClick(d, divs, transparentInnerHTML, visibleInnerHTML, "_ehp");

      e.parentNode.appendChild(d);
	  divs.push(d);
    }
  }catch(error){
    console.log(error);
  }
};

// ADD AND REMOVE OVERLAYS

EHPAddOverlays=function() {
  try{
    var divs=[];
  
    EHPhighlighter("header", EHPbyIdContains, "", "Header", "#c4652d", "#b05b28", divs); // Orange
    EHPhighlighter("footer", EHPbyIdContains, "", "Footer", "#ffebd7", "#e6d3c2", divs); // Peach
    EHPhighlighter("nav", EHPbyIdContains, "", "Navigation", "#53548a", "#454673", divs); // Purple
    EHPhighlighter("aside", EHPbyIdContains, "", "Aside", "#5c92b6", "#4f7d9c", divs); // Turquoise
    EHPhighlighter("div", EHPbyRoleContains, "complementary", "Aside Div", "#5c92b6", "#4f7d9c", divs); // Turquoise
    EHPhighlighter("article", EHPbyIdContains, "", "Article", "#eaeaea", "#d1d1d1", divs); // Grey
    EHPhighlighter("main", EHPbyIdContains, "", "Main", "#a14da3", "#8b428c", divs); // Lavendar
    EHPhighlighter("div", EHPbyRoleContains, "main", "Main Div", "#a14da3", "#8b428c", divs); // Lavendar
    //EHPhighlighter("div", EHPbyIdContains, "mainContent", "Main ID", "#a14da3", "#8b428c", divs); // Lavendar // mainContent
    EHPhighlighter("section", EHPbyIdContains, "", "Section", "#ffd3ea", "#dbb5c9", divs); // Pink
    EHPhighlighter("h1", EHPbyIdContains, "", "Heading Level 1", "#aed500", "#9ec200", divs); // Light Green
    EHPhighlighter("h2", EHPbyIdContains, "", "Heading Level 2", "#63d400", "#9ec200", divs); // Green
    EHPhighlighter("h3", EHPbyIdContains, "", "Heading Level 3", "#52b099", "#9ec200", divs); // Blue-Green
    EHPhighlighter("h4", EHPbyIdContains, "", "Heading Level 4", "#4b82ff", "#9ec200", divs); // Blue
    EHPhighlighter("h5", EHPbyIdContains, "", "Heading Level 5", "#42eee0", "#9ec200", divs); // Bright Blue
    EHPhighlighter("h6", EHPbyIdContains, "", "Heading Level 6", "#99b7c8", "#9ec200", divs); // Grey Blue
    EHPhighlighter("body", EHPbyClassContains, "pushdown-ad", "Pushdown Ad", "#00cbfa", "4f8f9d", divs); // For Ad blocker
    EHPhighlighter("div", EHPbyClassContains, "pushdown-ad", "Pushdown Ad", "00cbfa", "4f8f9d", divs); // For Ad blocker
  }catch(error){
    console.log(error);
  }
};
