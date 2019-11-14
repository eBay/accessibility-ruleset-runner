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

public enum CustomRulesetRules {

	  // Alt Tags

	  ruleH24PresenceOfAltInImageMap("H24 Image Map Alt Attribute", "<!--H24-->", "024", "A", "https://www.w3.org/TR/WCAG20-TECHS/H24.html", ""),

	  ruleH35altTextForApplet("H35 Applet Tag Alt Attribute", "<!--H35-->", "035", "A", "https://www.w3.org/TR/WCAG20-TECHS/H35.html", ""),
		
	  ruleH53altTextForObject("H53 Object Tag Alt Attribute", "<!--H53-->", "053", "A", "https://www.w3.org/TR/WCAG20-TECHS/H53.html", ""),

	  ruleH64FramesTitleAttribute("H64 IFrame Tag Title Attribute", "<!--H64-->", "064", "A", "https://www.w3.org/TR/WCAG20-TECHS/H64.html", ""),

	  ruleH46EmbedElement("H46 Embed Tag", "<!--H46-->", "046", "A", "https://www.w3.org/TR/WCAG20-TECHS/H46.html", ""),

	  // Anchor
	  
	  ruleH30OpeningNewWindows("H30 Opening New Windows", "<!--H30-->", "030", "AA", "https://www.w3.org/TR/WCAG20-TECHS/H30.html", "https://ebay.gitbook.io/mindpatterns/navigation/link.html"),
	  
	  ruleH33sameAnchorLinks("H30 Links Repeated","<!--H30_Links_Repeated-->", "130", "AA", "https://www.w3.org/TR/WCAG20-TECHS/H30.html", ""),
	  
	  ruleH75uniqueIDs("H75 Unique Anchor IDs","<!--H75_UniqueIDs-->", "075", "AA", "http://www.w3.org/TR/WCAG20-TECHS/H75.html", ""),	  
	  
	  // Form

	  ruleH44PresenceOfLabelforInputTag("H44 Input Tag Label", "<!--H44_POL-->", "044", "A", "https://www.w3.org/TR/WCAG20-TECHS/H44.html", ""),
	  
	  ruleH32FormSubmitBtn("H32 Form Submit Button", "<!--H32-->", "032", "A", "https://www.w3.org/TR/WCAG20-TECHS/H32.html", "https://ebay.gitbook.io/mindpatterns/structure/form.html"),
	  
	  // Image
	  
	  ruleH37PresenceOfAltInImage("H37 Image Tag Alt Attribute", "<!--H37_PAI-->", "037" , "A", "https://www.w3.org/TR/WCAG20-TECHS/H37.html", "https://ebay.gitbook.io/mindpatterns/structure/image.html"),

	  // Page Layout
	
	  ruleH25TitleElement("H25 Title Tag", "<!--H25-->", "025", "A", "https://www.w3.org/TR/WCAG20-TECHS/H25.html", ""),

	  ruleH57LangAttribute("H57 HTML Tag Lang Attribute", "<!--H57-->", "057", "A", "https://www.w3.org/TR/WCAG20-TECHS/H57.html", ""),
		
	  ruleSkiptoMainContent("Validate Skip to Main Content", "<!--skipToMainCont-->", "0Skip", "AA", "https://www.w3.org/TR/WCAG20-TECHS/G1.html", "https://ebay.gitbook.io/mindpatterns/navigation/skipto.html"),

	  ruleH42HeadingMarkupTags("H42 H1 Heading", "<!--H42_HMT-->", "142", "A", "https://www.w3.org/TR/WCAG20-TECHS/H42.html", "https://ebay.gitbook.io/mindpatterns/structure/heading.html"),

	  ruleH42HeadingSkipLinks("H42 Heading Hierarchy", "<!--H42_HSL-->", "042", "A", "https://www.w3.org/TR/WCAG20-TECHS/H42.html", "https://ebay.gitbook.io/mindpatterns/structure/heading.html"),
	  ;

	private String longName;
	private String templateString;
	private String ruleCode;
	private String ruleCompliance;
	private String wcagURL;
	private String patternHREF;

	/**
	 * Enum constructor to initialise default values.
	 * 
	 * @param longName
	 *            input parameter for Test rule to be executed
	 */
	private CustomRulesetRules(String longName, String templateString,
			String ruleCode, String ruleCompliance, String wcagURL,
			String patternsURL) {
		this.longName = longName;
		this.templateString = templateString;
		this.ruleCode = ruleCode;
		this.ruleCompliance = ruleCompliance;
		this.wcagURL = wcagURL;
		this.patternHREF = patternsURL;
	}

	/**
	 * Getter for the ruleName.
	 * 
	 * @return the WCAG Test rule name
	 */
	public String getName() {
		return this.name();
	}

	public String getLongName() {
		return longName;
	}

	public String getTemplateString() {
		return templateString;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public String getRuleComplianceLevel() {
		return ruleCompliance;
	}

	public String getWCAGURL() {
		return wcagURL;
	}

	public String getPatternHREF() {
		return patternHREF;
	}
	
	public static CustomRulesetRules getCustomRulesetRulesFromLongName(String aLongNameToCheck) {
		for(CustomRulesetRules rule: CustomRulesetRules.values()) {
			if(rule.getLongName().equals(aLongNameToCheck)) {
				return rule;
			}
		}
		return null;
	}
}
