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

package arr;

import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;

public enum CustomRulesetRulesErrorCodes {
	// Page Layout
	TITLE_NO_TAG(CustomRulesetRules.ruleH25TitleElement, "025_A_1"),
	TITLE_MULTIPLE_TAGS(CustomRulesetRules.ruleH25TitleElement, "025_A_2"),
	LANGUAGE_ATTRIBUTE(CustomRulesetRules.ruleH57LangAttribute, "057_A_1"),
	
	H1_NO_TAG(CustomRulesetRules.ruleH42HeadingMarkupTags, "142_A_1"),
	H1_MULTIPLE_TAGS(CustomRulesetRules.ruleH42HeadingMarkupTags, "142_A_2"),
	H1_EMPTY_TEXT(CustomRulesetRules.ruleH42HeadingMarkupTags, "142_A_3"),
	HEADINGS_SKIPPED(CustomRulesetRules.ruleH42HeadingSkipLinks, "042_A_1"),
	
	SKIP_TO_MAIN_NO_SOURCE_ANCHOR(CustomRulesetRules.ruleSkiptoMainContent, "0Skip_AA_1"),
	SKIP_TO_MAIN_INVALID_TEXT(CustomRulesetRules.ruleSkiptoMainContent, "0Skip_AA_2"),
	SKIP_TO_MAIN_NO_TARGET_ANCHOR(CustomRulesetRules.ruleSkiptoMainContent, "0Skip_AA_3"),
	SKIP_TO_MAIN_MULTIPLE_TARGET_ANCHORS(CustomRulesetRules.ruleSkiptoMainContent, "0Skip_AA_4"),

	// Form
	FORM_INPUT_TITLE(CustomRulesetRules.ruleH44PresenceOfLabelforInputTag, "044_A_1"),
	FORM_NEEDS_SUBMIT(CustomRulesetRules.ruleH32FormSubmitBtn, "032_A_1"),
	  
	// Image
	IMAGE_PRESENTATION_HAS_ALT(CustomRulesetRules.ruleH37PresenceOfAltInImage, "037_A_1"),
	IMAGE_CONTENT_NEEDS_ALT(CustomRulesetRules.ruleH37PresenceOfAltInImage, "037_A_2"),
	IMAGE_CONTENT_IN_ANCHOR_NEEDS_ALT(CustomRulesetRules.ruleH37PresenceOfAltInImage, "037_A_3"),
	IMAGE_SPACER__HAS_ALT(CustomRulesetRules.ruleH37PresenceOfAltInImage, "037_A_4"),
	
	// Alt
	AREA_NEEDS_ALT(CustomRulesetRules.ruleH24PresenceOfAltInImageMap, "024_A_1"),
	APPLET_NEEDS_ALT(CustomRulesetRules.ruleH35altTextForApplet, "035_A_1"), 
	OBJECT_NEEDS_ALT(CustomRulesetRules.ruleH53altTextForObject, "053_A_1"),
	IFRAME_NEEDS_TITLE(CustomRulesetRules.ruleH64FramesTitleAttribute, "064_A_1"),
	
	// Anchor
	ANCHOR_OPENS_A_NEW_WINDOW(CustomRulesetRules.ruleH30OpeningNewWindows, "030_AA_1"),
	ANCHOR_LINKS_REPEATED(CustomRulesetRules.ruleH30OpeningNewWindows, "130_AA_1"), 
	ANCHOR_UNIQUE_IDS(CustomRulesetRules.ruleH75uniqueIDs, "075_AA_1"), 

	// Core
	NO_EMBED_WITHIN_EMBED(CustomRulesetRules.ruleH46EmbedElement, "046_A_1"),
//	DATA_TABLE_HEADERS(ValidationRules.ruleH43HeadersforDatatables, "043_A_1"),
	
	;
	CustomRulesetRules rule = null;
	String errorCode = null;
	static Map<String, CustomRulesetRulesErrorCodes> failureCodeMap = new LinkedHashMap<String, CustomRulesetRulesErrorCodes>();
	private CustomRulesetRulesErrorCodes(CustomRulesetRules rule, String errorCode) {
		this.rule = rule;
		this.errorCode = errorCode;
	}
	
	public static CustomRulesetRulesErrorCodes findValidationRule(String errorCode) {
		return failureCodeMap.get(errorCode);
	}
	
	public String getErrorCode(){
		return errorCode;
	}
	
	static {
		// Validate error codes, since they are duplicately maintained in code, duplicated for readability
		for(CustomRulesetRulesErrorCodes eVal: CustomRulesetRulesErrorCodes.values()) {
			if(!eVal.errorCode.startsWith(eVal.rule.getRuleCode() + "_" + eVal.rule.getRuleComplianceLevel() + "_")) {
				System.out.println("Error!!!!!"+eVal);
			}

			failureCodeMap.put(eVal.errorCode, eVal);
		}
	}

	//To capture the compliance level for all issues failed in the rule
	public String getComplianceLevel() {
		return CustomRulesetRules.valueOf(CustomRulesetRules.class, rule.getName()).getRuleComplianceLevel();
	}
	
	public String getRuleCode() {
		return CustomRulesetRules.valueOf(CustomRulesetRules.class, rule.getName()).getRuleCode();
	}
	
	protected String makeRed(String string) {
		return "<code><i><font size='2' color='red'>" + string + "</font></i></code>";
	}

	public String generateComment(JSONObject element) {
		if(this.equals(AREA_NEEDS_ALT)) {
			String failureMessage = "The area with href="
					+ makeRed(element.getString("elementHREF"))
					+ " does not have an alt attribute.  Try &lt;area alt='Area Description'&gt;";
			return failureMessage;
		}
		else if(this.equals(APPLET_NEEDS_ALT)) {
			String failureMessage = "The applet with code="
					+ makeRed(element.getString("elementCode"))
					+ " does not have an alt attribute.  Try &lt;applet alt='Applet Description'&gt;";
			return failureMessage;
		}
		else if(this.equals(OBJECT_NEEDS_ALT)) {
			String failureMessage = "The object with id="
					+ makeRed(element.getString("elementID"))
					+ " does not have an alt attribute.  Try &lt;object alt='Object Description'&gt;";
			return failureMessage;
		}
		else if(this.equals(IFRAME_NEEDS_TITLE)) {
			String failureMessage = "The iframe with src="
					+ makeRed(element.getString("elementSRC"))
					+ " does not have an title attribute.  Try &lt;iframe title='IFrame Title'&gt;";
			return failureMessage;
		}
		else if(this.equals(ANCHOR_UNIQUE_IDS)) {
			String failureMessage = "The anchor with href="
					+ makeRed(element.getString("elementHREF"))
					+ " title="
					+ makeRed(element.getString("elementTitle"))
					+ " text="
					+ makeRed(element.getString("elementText"))
					+ " has the same ID as the anchor with href="
					+ makeRed(element.getString("elementFoundHREF"))
					+ ".  Anchors need to have unique IDs on the page.";
			return failureMessage;
		}
		else if(this.equals(ANCHOR_LINKS_REPEATED)) {
			String failureMessage = "The anchor with href="
					+ makeRed(element.getString("elementHREF"))
					+ " is too similar to another anchor with href="
					+ makeRed(element.getString("elementFoundHREF")) // Fixed code to show who href here for similar links
					+ ".  "
					+ "The current label='"+element.getString("elementScreenReaderLabel") + "' and description='"+element.getString("elementScreenReaderDescription")+"' is not sufficient.  "
					+ "These may be distinguished by adding context to either the label or description.";
			return failureMessage;
		}
		else if(this.equals(ANCHOR_OPENS_A_NEW_WINDOW)) {
			String failureMessage = "The anchor with href="
					+ makeRed(element.getString("elementHREF"))
					+ " and target="
					+ makeRed("_blank")
					+ " opens in a new window/tab and needs to inform the user. "
					+ "The current label='"+element.getString("elementScreenReaderLabel") + "' and description='"+element.getString("elementScreenReaderDescription")+"' is not sufficient.  "
					+ "Improve the label (which comes from the aria-labelledby attribute, aria-label attribute or inline text) and/or description (which comes from the aria-describedby attribute or title attribute) to warn the user with the exact verbiage 'opens in new window or tab.'  "
					+ "Try &lt;a href='...'&gt;...&lt;span class='clipped'&gt; - opens in new window or tab&lt;/span&gt&lt;/a&gt;.";
			return failureMessage;
		}
		else if(this.equals(NO_EMBED_WITHIN_EMBED)) {
			String failureMessage = "Each embed tag needs to contain a noembed tag to aid the assistive technology.";
			return failureMessage;
		}
		else if(this.equals(FORM_INPUT_TITLE)) {
			String failureMessage = "The input with id="
					+ makeRed(element.getString("elementID"))
					+ " needs a title or label:  1) title attribute, 2) label text from a wrapped label (the label tag wraps the input tag) or 3) label text from a referrence label (the label for attribute references the input id attribute).  Try &lt;label for='inputElement'&gt;Input Label&lt;/label&gt;&lt;input id='inputElement' /&gt;";
				return failureMessage;
		}
		else if(this.equals(FORM_NEEDS_SUBMIT)) {
			String failureMessage = "The form with id="
					+ makeRed(element.getString("elementID"))
					+ " and action="
					+ makeRed(element.getString("elementAction"))
					+ " needs a submit button: 1) input type=submit, 2) input type=image (needs a value attribute) or 3) button type=submit.  Try &lt;input type='submit'&gt;";
			return failureMessage;
		}
		else if(this.equals(IMAGE_SPACER__HAS_ALT)) {
			String failureMessage = "The image with src="
					+ makeRed(element.getString("elementSRC"))
					+ " and alt="
					+ makeRed(element.getString("elementAlt"))
					+ " was identified as a Spacer image.  Either remove the alt attribute (&lt;img alt=''&gt;) or use s_1x2.gif to mark it as a content image.";
			return failureMessage;
		}
		else if(this.equals(IMAGE_PRESENTATION_HAS_ALT)) {
			String failureMessage = "The presentation image with src="
					+ makeRed(element.getString("elementSRC"))
					+ " and alt="
					+ makeRed(element.getString("elementAlt"))
					+ " should have empty alt attribute.  Try &lt;img alt=''&gt;.";
			return failureMessage;
		}
		else if(this.equals(IMAGE_CONTENT_IN_ANCHOR_NEEDS_ALT)) {
			String failureMessage = "The content image with src="
					+ makeRed(element.getString("elementSRC"))
					+ " needs an: 1) image alt attribute, 2) anchor title attribute or 3) anchor text.  Try &lt;img alt='Image Description'&gt;.";
			return failureMessage;
		}
		else if(this.equals(IMAGE_CONTENT_NEEDS_ALT)) {
			String failureMessage = "The content image with src="
					+ makeRed(element.getString("elementSRC"))
					+ " needs an image alt attribute.  Try &lt;img alt='Image Description'&gt;.";
			return failureMessage;
		}
		else if(this.equals(TITLE_NO_TAG)) {
			String failureMessage = "This page has no title tag.  Try &lt;title&gt;Page Title&lt;/title&gt;";
			return failureMessage;
		}
		else if(this.equals(TITLE_MULTIPLE_TAGS)) {
			String failureMessage = "This page has multiple title tags.";
			return failureMessage;
		}
		else if(this.equals(LANGUAGE_ATTRIBUTE)) {
			String failureMessage = "The html tag needs a language attribute.  Try &lt;html lang='en'&gt;.";
			return failureMessage;
		}
		else if(this.equals(H1_NO_TAG)) {
			String failureMessage = "This page has no h1 tag.  Try &lt;h1&gt;Main Page Heading&lt/h1&gt;.";
			return failureMessage;
		}
		else if(this.equals(H1_EMPTY_TEXT)) {
			String failureMessage = "The H1 does not have any text.  Try &lt;h1&gt;Main Page Heading&lt/h1&gt;.";
			return failureMessage;
		}
		else if(this.equals(H1_MULTIPLE_TAGS)) {
			String failureMessage = "This page has multiple h1 tags.  "+element.getString("elementOtherH1Texts");
			return failureMessage;
		}
		else if(this.equals(HEADINGS_SKIPPED)) {
			String failureMessage = "The heading with tag="
					+ makeRed(element.getString("elementTag"))
					+ " and text="
					+ makeRed(element.getString("elementText")) // Only call getText for failing case, shaves 1.5 seconds off run
					+ " skips a level since it follows a heading with tag="
					+ makeRed(element.getString("elementFirstTag")) + ".";
			return failureMessage;
		}
		else if(this.equals(SKIP_TO_MAIN_NO_SOURCE_ANCHOR)) {
			String failureMessage = "There is no mechanism to skip to the main content of the page.  One of the first ten anchors on the page needs to allow a user to skip to the main content of the page.  Try &lt;a href='#mainContent'&gt;Skip to main content&lt;/a&gt;.";
			return failureMessage;
		}
		else if(this.equals(SKIP_TO_MAIN_INVALID_TEXT)) {
			String failureMessage = "The text 'Skip to main content' should be contained within the skip to main content source anchor.";
			return failureMessage;
		}
		else if(this.equals(SKIP_TO_MAIN_NO_TARGET_ANCHOR)) {
			String failureMessage = "This page has no skip to main content target.  Try &lt;div id='mainContent' role='main' tabindex='-1'&gt;...&lt;/div&gt;.";
			return failureMessage;
		}
		else if(this.equals(SKIP_TO_MAIN_MULTIPLE_TARGET_ANCHORS)) {
			String failureMessage = "This page has multiple skip to main content target anchors.";
			return failureMessage;
		}
		
		return "";
	}
}
