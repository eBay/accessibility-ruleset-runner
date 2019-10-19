//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Scott Izu
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

initializeRowsHandlers = function() {	
  // Add expand/collapse to all rule rows with multiple details
  $('tr.testRuleRow').each (function () {
    var $testRuleRow = this;
    var $td10 = findNextSiblingWhichIsNotTextOrComment($testRuleRow.firstChild);
    var $a = findNextSiblingWhichIsNotTextOrComment($td10.firstChild);
    rowsInitialize($a);
  });
}

function findNextSiblingWhichIsNotTextOrComment($node) {
  var $nodeToCheck = $node.nextSibling;
  while ($nodeToCheck.nodeName == '#text' || $nodeToCheck.nodeName == '#comment') {
    $nodeToCheck = $nodeToCheck.nextSibling;
  }
  return $nodeToCheck;
}

//  <tr> rule row
//    <td> 10%
//      <a>
//    <td> 90%
//      <table>
//        <tr> detail row
function findFirstDetailRowFromAnchor($a) {
  var $td10 = $a.parentNode;
  var $td90 = findNextSiblingWhichIsNotTextOrComment($td10);
  var $table = findNextSiblingWhichIsNotTextOrComment($td90.firstChild);
  var $tbody = findNextSiblingWhichIsNotTextOrComment($table.firstChild);
  var $firstTR = $tbody.firstChild;
  return $firstTR;
}

// show all but first row, expand table
function rowsShow($a) {
  $a.innerHTML = '<img alt="minus" src="minusb.jpg">';
  $a.setAttribute('onclick', 'rowsHide(this)');

  var $firstTR = findFirstDetailRowFromAnchor($a);
  var $currentNode = $firstTR.nextSibling;
  while (($currentNode.nodeName != '#comment') || ($currentNode.nodeValue != 'TEST_STEPS')) {
    if ($currentNode.nodeName == 'TR') {
      $currentNode.style.display = ''; // takes up space, can be seen
    }
    $currentNode = $currentNode.nextSibling;
  }
}

// collapse all but first row, collapse table
function rowsHide($a) {
  $a.innerHTML = '<img alt="plus" src="plus3.jpg">';
  $a.setAttribute('onclick', 'rowsShow(this)');

  var $firstTR = findFirstDetailRowFromAnchor($a);
  var $currentNode = $firstTR.nextSibling;
  while (($currentNode.nodeName != '#comment') || ($currentNode.nodeValue != 'TEST_STEPS')) {
    if ($currentNode.nodeName == 'TR') {
      $currentNode.style.display = 'none'; // takes up no space, can't be seen
    }
    $currentNode = $currentNode.nextSibling;
  }
}
	
// add image to all rows with multiple details
function rowsInitialize($a) {
  var $firstTR = findFirstDetailRowFromAnchor($a);
  var $currentNode = $firstTR.nextSibling;
  while (($currentNode.nodeName != '#comment') || ($currentNode.nodeValue != 'TEST_STEPS')) {
    if ($currentNode.nodeName == 'TR') {
      $a.innerHTML = '<img alt="minus" src="minusb.jpg">';
      $a.setAttribute('onclick', 'rowsHide(this)');
    }
    $currentNode = $currentNode.nextSibling;
  }
}
	
// DEBUG FUNCTIONS
function printTag($node) {
  var $message = '<' + $node.tagName;
  for ( var i = 0; i < $node.attributes.length; i++) {
    $message = $message + ' ' + $node.attributes.item(i).nodeName
      + '="' + $node.attributes.item(i).nodeValue + '"';
  }
  $message = $message + '>';
  $message = $message + $node.innerHTML;
  $message = $message + '</' + $node.tagName + '>';
  alert($message);
}

function printComment($node) {
  var $message = '<!--' + $node.nodeValue + '-->';
  alert($message);
}

function printText($node) {
  var $message = $node.nodeName;
  alert($message);
}