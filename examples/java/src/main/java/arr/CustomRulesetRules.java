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

	  // Page Layout
	
	  ruleH25TitleElement("H25 Title Tag", "<!--H25-->", "025", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H25.html", ""),

	  ruleH57LangAttribute("H57 HTML Tag Lang Attribute", "<!--H57-->", "057", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H57.html", ""),
		
	  ruleSkiptoMainContent("Validate Skip to Main Content", "<!--skipToMainCont-->", "0Skip", "AA", "http://www.w3.org/TR/2007/WD-UNDERSTANDING-WCAG20-20071211/navigation-mechanisms-skip.html", "https://ebay.gitbook.io/mindpatterns/navigation/skipto.html"),

	  ruleH42HeadingMarkupTags("H42 H1 Heading", "<!--H42_HMT-->", "142", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H42.html", "https://ebay.gitbook.io/mindpatterns/structure/heading.html"),

	  ruleH42HeadingSkipLinks("H42 Heading Hierarchy", "<!--H42_HSL-->", "042", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H42.html", "https://ebay.gitbook.io/mindpatterns/structure/heading.html"),
	  
	  // Form

	  ruleH44PresenceOfLabelforInputTag("H44 Input Tag Label", "<!--H44_POL-->", "044", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H44.html", ""),
	  
	  ruleH32FormSubmitBtn("H32 Form Submit Button", "<!--H32-->", "032", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H32.html", "https://ebay.gitbook.io/mindpatterns/structure/form.html"),
	  
	  // Image
	  
	  ruleH37PresenceOfAltInImage("H37 Image Tag Alt Attribute", "<!--H37_PAI-->", "037" , "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H37.html", "https://ebay.gitbook.io/mindpatterns/structure/image.html"),

	  // Alt Tags

	  ruleH24PresenceOfAltInImageMap("H24 Image Map Alt Attribute", "<!--H24-->", "024", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H24.html", ""),

	  ruleH35altTextForApplet("H35 Applet Tag Alt Attribute", "<!--H35-->", "035", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H35.html", ""),
		
	  ruleH53altTextForObject("H53 Object Tag Alt Attribute", "<!--H53-->", "053", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H53.html", ""),

	  ruleH64FramesTitleAttribute("H64 IFrame Tag Title Attribute", "<!--H64-->", "064", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H64.html", ""),

	  ruleH46EmbedElement("H46 Embed Tag", "<!--H46-->", "046", "A", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H46.html", ""),

	  // Anchor
	  
	  ruleH33LinkOpensinNewWindow("H33 Anchor Tag Title For New Windows", "<!--H33-->", "033", "AA", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H33.html", "https://ebay.gitbook.io/mindpatterns/navigation/link.html"),
	  
	  ruleH33sameAnchorLinks("H33 Links Repeated","<!--H33_DupLinks-->", "133", "AA", "http://www.w3.org/TR/2012/NOTE-WCAG20-TECHS-20120103/H33.html", ""),
	  
	  ruleH75uniqueIDs("H75 Unique Anchor IDs","<!--H75_UniqueIDs-->", "075", "AA", "http://www.w3.org/TR/WCAG20-TECHS/H75.html", ""),	  
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
