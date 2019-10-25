# Custom Ruleset Test Library
This is a <a href='../README.md#test-library'>Test Library</a> for the <a href="../../README.md#custom-ruleset">Custom Ruleset</a>.

There are four types of code snippets:
<ul>
<li><b>Good:</b> Code satisfies accessibility criteria.</li>
<li><b>Exception:</b> Code satisfies accessibility criteria but is not recommended.</b></li>
<li><b>Exemption:</b> Code fails to satisfy accessibility criteria but is not expected to be fixed.  (see also <a href='../README.md#exemptions'>Exemptions</a>)</li>
<li><b>Bad:</b> Code fails to satisfy accessibility criteria.</li>
</ul>

Multiple code snippets are placed into a single HTML file.  These files are grouped into 5 main categories (see below).

<ul>

<li>Objects and Alternative Text
<ul>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/altTagsBad.html'>altTagsBad</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/altTagsGood.html'>altTagsGood</a></li>
</ul>
</li>

<li>Anchors
<ul>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/anchorBad.html'>anchorBad</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/anchorGood.html'>anchorGood</a></li>
</ul>
</li>

<li>Forms
<ul>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/formBad.html'>formBad</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/formGood.html'>formGood</a></li>
</ul>
</li>

<li>Images
<ul>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/imageBad.html'>imageBad</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/imageGood.html'>imageGood</a></li>
</ul>
</li>

<li>Page Layout
<ul>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutBad.html'>pageLayoutBad</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutBad_SkipToMainSourceInvalidText.html'>pageLayoutBad_SkipToMainSourceInvalidText</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutBad_SkipToMainSourceMissing.html'>pageLayoutBad_SkipToMainSourceMissing</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutBad_SkipToMainSourceTargetMismatch.html'>pageLayoutBad_SkipToMainSourceTargetMismatch</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutBad_SkipToMainTargetDuplicated.html'>pageLayoutBad_SkipToMainTargetDuplicated</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutBad_SkipToMainTargetIsHTag.html'>pageLayoutBad_SkipToMainTargetIsHTag</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutGood.html'>pageLayoutGood</a></li>
<li><a href='https://htmlpreview.github.io/?https://github.com/ebay/accessibility-ruleset-runner/blob/master/rulesets/tests/input/pageLayoutGood_HiddenH1.html'>pageLayoutGood_HiddenH1</a></li>
</ul>
</li>

</ul>
