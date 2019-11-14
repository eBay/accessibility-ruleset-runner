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

package report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import util.HTMLGenerator;
import arr.CustomRulesetRules;
import arr.CustomRulesetRulesErrorCodes;

public class JSONToHTMLConverter extends HTMLGenerator{
	
	String reportLocation;
	String pageTemplate;
	String ruleDescriptionsTemplate;

	String failedRowTemplate;
	String exceptionTemplate;
	String stackElementTemplate;
	String reasonTemplate;
	String testStepsTemplate;

	public void convert(JSONObject results) {
		String viewName = results.getString("viewName");
		this.reportLocation = "output/"+viewName.replaceAll(" ", "_")+"_ARR_Report.html";
		
		this.pageTemplate = getPageTemplate(JSONToHTMLConverter.class, "HTMLReportTemplate.html");
		this.ruleDescriptionsTemplate = getPageTemplate(JSONToHTMLConverter.class, "RuleDescriptions.html");
		
	  	failedRowTemplate = getInnerTemplate(pageTemplate, "<!--ROW_TEMPLATE-->");
	  	exceptionTemplate = getInnerTemplate(pageTemplate, "EXCEPTION_TEMPLATE");
	  	stackElementTemplate = getInnerTemplate(exceptionTemplate, "STACK_ELEMENT_TEMPLATE");
	  	reasonTemplate = getInnerTemplate(failedRowTemplate, "<!--REASON-->");
	  	testStepsTemplate = getInnerTemplate(failedRowTemplate, "<!--TEST_STEP_ITEM-->");

		String report = pageTemplate;
		report = HTMLGenerator.substituteMarker(report, "<!--REPORT_TITLE-->", "<title>"+results.getString("reportTitle")+"</title>");
		report = HTMLGenerator.substituteMarker(report, "<!--REPORT_TITLE2-->", results.getString("reportTitle"));
		report = HTMLGenerator.substituteMarker(report, "<!--VIEW_NAME-->", results.getString("viewName"));
		report = HTMLGenerator.substituteMarker(report, "<!--URL-->", results.getString("url"));
		report = HTMLGenerator.substituteMarker(report, "<!--VIEW_SCREENSHOT-->", buildViewScreenShotAnchor(results));
		report = HTMLGenerator.substituteMarker(report, "<!--ROWS_TEMPLATE-->", listAllRowsForFailures(results));
		writeFile(new File(reportLocation), report);
		System.out.println("reportLocation:"+new File(reportLocation).getAbsolutePath());
	}

	private String buildViewScreenShotAnchor(JSONObject results) {
		StringBuilder anchor = new StringBuilder();
		if(results.has("viewImage")) {
			String viewImage = results.getString("viewImage");
			buildScreenshotHTML(anchor, viewImage, "View");
		}
		if(results.has("xpathImage")) {
			String xpathImage = results.getString("xpathImage");
			buildScreenshotHTML(anchor, xpathImage, "Root Element");
		}
		return anchor.toString();
	}

	private void buildScreenshotHTML(StringBuilder anchor, String image,
			String label) {
		String[] names = image.split("\\.");
		anchor.append("\n\n<br>");
		anchor.append("<a title='See "+label+" in Snapshot Overlay' class='modalInput' rel="+HTMLGenerator.addSingleQuotes('#'+names[0])+" relParent='#'>");
		anchor.append("<img alt='camera' style='width: 32px; height: 25px;' src='cam.png' />");
		anchor.append("</a>");
		anchor.append("<div class='modal' id="+HTMLGenerator.addSingleQuotes(names[0])+" style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>");
		anchor.append("<image src="+HTMLGenerator.addSingleQuotes(image)+" width='100%' height='100%'/>");
		anchor.append("<br>");
		anchor.append("<img class='close' src='close.png' />");
		anchor.append("</div>");
		anchor.append("<div class='exposeMask' id="+HTMLGenerator.addSingleQuotes('#'+names[0]+"_MASK")+" ></div>");
	}

	private String listAllRowsForFailures(JSONObject results) {
		String viewName = results.getString("viewName");
		String url = results.getString("url");
		
		StringBuilder dataHtml = new StringBuilder();

		try {
			listAllRowsForCustomRulesetFailures(results, dataHtml, viewName, url);
			listAllRowsForAXERulesetFailures(results, dataHtml, viewName, url);
		} catch (Exception e) {
			e.printStackTrace();
			String oops = exceptionTemplate;
			oops = HTMLGenerator.substituteMarker(oops, "STACK_ELEMENT_TEMPLATE", buildHTMLForViewExceptionStackTraceElements(e));
			return oops;
		}
		return dataHtml.toString();
	}
	
	private String buildHTMLForViewExceptionStackTraceElements(Exception e) {
		StringBuffer listSTElements = new StringBuffer();
		StackTraceElement[] stack = e.getStackTrace();
		for (StackTraceElement stackTraceElement : stack) {
			String stElement = stackElementTemplate;
			stElement = HTMLGenerator.substituteMarker(stElement, "/*CLASS_VALUE*/", stackTraceElement.getClassName());
			stElement = HTMLGenerator.substituteMarker(stElement, "/*METHOD_VALUE*/", stackTraceElement.getMethodName());
			stElement = HTMLGenerator.substituteMarker(stElement, "/*LINE_VALUE*/", ""+stackTraceElement.getLineNumber());
			listSTElements.append(stElement);
		}
		return listSTElements.toString();
	}
	
	private void listAllRowsForCustomRulesetFailures(JSONObject results, StringBuilder dataHtml, String viewName, String url) {

		JSONArray customArray = results.getJSONArray("custom");
		for (int i=0; i<customArray.length(); i++) {
			JSONObject customRule = customArray.getJSONObject(i);
			CustomRulesetRules customRulesetRule = CustomRulesetRules.getCustomRulesetRulesFromLongName(customRule.getString("rule"));
			JSONArray failedElements = customRule.getJSONArray("elements");
			if (failedElements.length() > 0) {
				// Generate html for page
				String rowHTML = failedRowTemplate;

				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE1-->", customRule.getString("ruleCode"));
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_NAME-->", customRule.getString("ruleCode")+" : "+customRule.getString("rule"));
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_DETAILS-->", buildDescriptionHTML(customRulesetRule));
				// Generate the HTMl for showing compliance level of each rule
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL1-->", customRulesetRule.getRuleComplianceLevel());
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL2-->", customRulesetRule.getRuleComplianceLevel());
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_SOURCE-->", "<a title='View Rule Help Overlay' class='modalInput' rel='#"+customRulesetRule.getRuleCode()+"_HELP' relParent='#failedAPIContent'>");
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_TARGET-->", "<div class='modal' id='"+customRulesetRule.getRuleCode()+"_HELP' style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>");
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--WCAG_ANCHOR_SOURCE-->", "<a target='_blank' title='Rule - WCAG Documentation' href='"+customRulesetRule.getWCAGURL()+"'>");
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_MASK_TARGET-->", "<div class='exposeMask' id='"+customRulesetRule.getRuleCode()+"_HELP_MASK' ></div>");

				String href = customRulesetRule.getPatternHREF();
				if(href != null && !href.isEmpty()) {
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--PATTERNS_LINK_ANCHOR-->", "<a id='patternLink' title='Rule - Accessibility Pattern' target='_blank' href="+HTMLGenerator.addSingleQuotes(href)+">");
				} else {
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--PATTERNS_LINK-->", "");
				}

				List<String> numberedCommentList = buildNumberedCommentList(failedElements);	
				
				System.out.println("buildTestDetails... viewName:"+viewName);			
				String testDetails = buildTestDetails(viewName, url, customRulesetRule, numberedCommentList);

				System.out.println("buildEmailerLink... viewName:"+viewName);
				String emailLink = buildEmailerLink(testDetails);
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--EMAILER_LINK-->", emailLink);

				System.out.println("buildHTMLForColumnReason... viewName:"+viewName);	
				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--REASONS-->", buildHTMLForColumnReason(failedElements, numberedCommentList));
				
				dataHtml.append(rowHTML.toString());
			}
		}
	}

	private String buildDescriptionHTML(CustomRulesetRules customRulesetRule) {
		String descriptionHTML = getInnerTemplate(ruleDescriptionsTemplate, customRulesetRule.getTemplateString());
		return descriptionHTML;
	}

	private List<String> buildNumberedCommentList(JSONArray elementFailures) {
		List<String> commentList = new ArrayList<String>();
		for (int i=0; i<elementFailures.length(); i++) {
			JSONObject elementFailure = elementFailures.getJSONObject(i);
			CustomRulesetRulesErrorCodes errorCode = CustomRulesetRulesErrorCodes.findValidationRule(elementFailure.getString("elementFailureCode"));
			String step = errorCode.generateComment(elementFailure);
			commentList.add((i+1) + " . " + step);
		}
		return commentList;
	}

	public String buildEmailerLink(String testDetailsDescription) {	
		
		String mailTo = "mailto:sizu@ebay.com?"
				+ "subject=Please look into the following bug identified by ARR"
				+ "&body=";
		mailTo = mailTo + testDetailsDescription;
		
		String emailLinkHTML = "<a id='emailerLink' title='Rule - Email Bug Details' href=\""
				+ mailTo + "\">";
		
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!"+emailLinkHTML);
		
		return emailLinkHTML;
	}

	private String buildTestDetails(String viewName, String url, CustomRulesetRules customRulesetRule,
			List<String> numberedCommentList) {
		String testDetails = "" 
				+ "Test Details:" 
				+ "%0A  View Name: " + viewName
				+ "%0A  URL: " + url 
				+ "%0A  Rule Name: " + customRulesetRule.getName() 
				+ "%0A  Rule Code: " + customRulesetRule.getRuleCode()
				+ "%0A  Rule Compliance Level: " + customRulesetRule.getRuleComplianceLevel();
		
		testDetails = testDetails + "%0A"
				+ "%0AReasons:";
		for (String comment : numberedCommentList) {
			String alteredComment = "%0A  "
					+ removeTagsAndDoubleQuotesFromComment(comment);
			// Limit on URL is 2048
			if (testDetails.length() + alteredComment.length() < 1000) {
				testDetails = testDetails + alteredComment;
			} else {
				testDetails = testDetails + "%0A  ...";
				break;
			}
		}
			
		return testDetails;
	}

	/**
	 * 	<code><i><font size='2' color='red'>" + string + "</font></i></code>
	 * @param comment
	 * @return
	 */
	private String removeTagsAndDoubleQuotesFromComment(String comment) {
		comment = comment.replaceAll("<code><i><font size='2' color='red'>", "");
		comment = comment.replaceAll("</font></i></code>", "");
		comment = comment.replaceAll("\"", "'"); // Shouldn't have any double quotes
		comment = comment.replaceAll("<", "&lt;");
		comment = comment.replaceAll(">", "&gt;");
		//System.out.println("##################################"+comment);
		return comment;
	}

	public String buildHTMLForColumnReason(JSONArray elementFailures,
			List<String> numberedCommentList) {
		StringBuffer testStepsHTML = new StringBuffer();
		for (int i=0; i<elementFailures.length(); i++) {
			JSONObject element = elementFailures.getJSONObject(i);
			String alteredComment = addStripToComment(element, numberedCommentList.get(i), i+1);
			String testStepItem = HTMLGenerator.substituteMarker(this.testStepsTemplate,
					"<!--TEST_STEP_DETAILS-->", alteredComment); // ORIGINAL
			testStepsHTML.append(testStepItem);
		}

		String reason = reasonTemplate;
		reason = HTMLGenerator.substituteMarker(reason, "<!--TEST_STEPS-->",
				testStepsHTML.toString());

		return reason.toString();
	}

	private String addStripToComment(JSONObject element, String comment, Integer counter) {
		StringBuilder commentSB = new StringBuilder();
		commentSB.append(comment);
		
		if(element.has("elementImage")) {
			String elementImage = element.getString("elementImage");
			buildScreenshotHTML(commentSB, elementImage, "Element");
		}
	
		// Default id is from id attribute. If that fails, try class, if that fails, use the text of the tag
	
		// Generate ID
		commentSB.append("<br>");
		commentSB.append("\n&nbsp;&nbsp;&nbsp;");
		commentSB.append("<b>tag:</b>");
		commentSB.append(element.getString("elementTag"));
		if (element.has("elementClass") && !element.getString("elementClass").isEmpty()) {
			commentSB.append("\n&nbsp;&nbsp;&nbsp;");
			commentSB.append("<b>class:</b>");
			commentSB.append(element.getString("elementClass"));
		}
		if (element.has("elementName") && !element.getString("elementName").isEmpty()) {
			commentSB.append("\n&nbsp;&nbsp;&nbsp;");
			commentSB.append("<b>name:</b>");
			commentSB.append(element.getString("elementName"));
		}
		if (element.has("elementID") && !element.getString("elementID").isEmpty()) {
			commentSB.append("\n&nbsp;&nbsp;&nbsp;");
			commentSB.append("<b>id:</b>");
			commentSB.append(element.getString("elementID"));
		}
		
		// New addition
		commentSB.append("<br>\n&nbsp;&nbsp;&nbsp;");
		commentSB.append("<b>xpath:</b>");
		commentSB.append(element.getString("elementXPATH"));
		return commentSB.toString();
	}

	/**
	 * Adds rows into HTML Report by parsing aXe response.
	 * See also https://www.deque.com/axe/axe-for-web/documentation/api-documentation/#results-object (Results Object section)
	 * 
	 * @param results
	 * @param dataHtml
	 * @param viewName
	 * @param url
	 */
	private void listAllRowsForAXERulesetFailures(JSONObject results,
			StringBuilder dataHtml, String viewName, String url) {
		JSONObject axeResults = results.getJSONObject("axe");
		JSONArray violations = axeResults.getJSONArray("violations");
		for (int i=0; i<violations.length(); i++) {
			JSONObject axeRule = violations.getJSONObject(i);
			String ruleName = axeRule.getString("id");

			String complianceLevel = "AAA";
			JSONArray tags = axeRule.getJSONArray("tags");
			for(int k=0; k<tags.length(); k++) {
				String tag = tags.getString(k);
				if(tag.equals("wcag2a")) {
					complianceLevel = "A";
				} else if(tag.equals("wcag2aa")) {
					complianceLevel = "AA";
				}
			}
			
			// Generate html for page
			String rowHTML = failedRowTemplate;

			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE1-->", ruleName);
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_NAME-->", ruleName);
			
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_DETAILS-->", buildDescriptionHTMLForAXE(axeRule));
			// Generate the HTMl for showing compliance level of each rule
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL1-->", complianceLevel);
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL2-->", complianceLevel);
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_SOURCE-->", "<a title='View Rule Help Overlay' class='modalInput' rel='#"+ruleName+"_HELP' relParent='#failedAPIContent'>");
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_TARGET-->", "<div class='modal' id='"+ruleName+"_HELP' style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>");
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--WCAG_ANCHOR_SOURCE-->", "");
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_MASK_TARGET-->", "<div class='exposeMask' id='"+ruleName+"_HELP_MASK' ></div>");

			// Remove WCAG LINK
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--WCAG_ANCHOR_SOURCE-->", "");
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--WCAG_ANCHOR_SOURCE2-->", "");
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--WCAG_ANCHOR_IMG-->", "");
			
			// Remove MIND Pattern LINK
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--PATTERNS_LINK-->", "");

			List<String> numberedCommentList = buildNumberedCommentListForAXE(axeRule.getJSONArray("nodes"));	
			
			System.out.println("buildTestDetails... viewName:"+viewName);			
//				String testDetails = buildTestDetailsForAXE(viewName, url, customRulesetRule, numberedCommentList);

			System.out.println("buildEmailerLink... viewName:"+viewName);
//				String emailLink = buildEmailerLink(testDetails);
//				rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--EMAILER_LINK-->", emailLink);

			System.out.println("buildHTMLForColumnReason... viewName:"+viewName);	
			rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--REASONS-->", buildHTMLForColumnReasonForAXE(axeRule.getJSONArray("nodes"), numberedCommentList));
			
			dataHtml.append(rowHTML.toString());
		}
	}

	private String buildDescriptionHTMLForAXE(JSONObject violation) {
		String description = violation.getString("description");
		String help = violation.getString("help");
		String helpUrl = violation.getString("helpUrl");
		
		StringBuilder descriptionHTML = new StringBuilder();
		descriptionHTML.append("<h3>Description</h3>");
		descriptionHTML.append(description);
		descriptionHTML.append(".");
		descriptionHTML.append("<h3>Help</h3>");
		descriptionHTML.append(help);
		descriptionHTML.append(".  For more information, visit <a href='");
		descriptionHTML.append(helpUrl);
		descriptionHTML.append("'>Deque University</a>.");
		descriptionHTML.append("<br><br>");
		
		return descriptionHTML.toString();
	}

	private List<String> buildNumberedCommentListForAXE(JSONArray nodes) {
		List<String> commentList = new ArrayList<String>();
		for (int i=0; i<nodes.length(); i++) {
			JSONObject node = nodes.getJSONObject(i);
			JSONArray any = node.getJSONArray("any");
			Set<String> messages = new HashSet<String>();
			for(int j=0; j<any.length(); j++) {
				JSONObject anyJSON = any.getJSONObject(j);
				messages.add(anyJSON.getString("message"));
			}
			JSONArray all = node.getJSONArray("all"); // Should not have anything here for violations
			for(int j=0; j<all.length(); j++) {
				JSONObject allJSON = all.getJSONObject(j);
				messages.add(allJSON.getString("message"));
			}
			JSONArray none = node.getJSONArray("none");
			for(int j=0; j<none.length(); j++) {
				JSONObject noneJSON = none.getJSONObject(j);
				messages.add(noneJSON.getString("message"));
			}
			
			StringBuilder messagesSB = new StringBuilder();
			for(String message: messages) {
				messagesSB.append(message);
				messagesSB.append(". ");
			}
			
			commentList.add((i+1) + " . " + messagesSB.toString());
		}
		return commentList;
	}

	private String buildHTMLForColumnReasonForAXE(JSONArray nodes,
			List<String> numberedCommentList) {
		StringBuffer testStepsHTML = new StringBuffer();
		for (int i=0; i<nodes.length(); i++) {
			JSONObject node = nodes.getJSONObject(i);
			String alteredComment = addStripToCommentForAXE(node, numberedCommentList.get(i), i+1);
			String testStepItem = HTMLGenerator.substituteMarker(this.testStepsTemplate,
					"<!--TEST_STEP_DETAILS-->", alteredComment); // ORIGINAL
			testStepsHTML.append(testStepItem);
		}

		String reason = reasonTemplate;
		reason = HTMLGenerator.substituteMarker(reason, "<!--TEST_STEPS-->",
				testStepsHTML.toString());

		return reason.toString();
	}
	
	private String addStripToCommentForAXE(JSONObject node, String comment, Integer counter) {
		StringBuilder commentSB = new StringBuilder();
		commentSB.append(comment);

		// Note: Screenshots for aXe element failures have not been implemented yet
		if(node.has("elementImage")) {
			String elementImage = node.getString("elementImage");
			buildScreenshotHTML(commentSB, elementImage, "Element");
		}
	
		// Default id is from id attribute. If that fails, try class, if that fails, use the text of the tag
	
		// Generate ID
		commentSB.append("<br>");
		commentSB.append("\n&nbsp;&nbsp;&nbsp;");
		commentSB.append("<b>html:</b>");
		String html = node.getString("html");
		try {
			html = StringEscapeUtils.escapeXml(html);
			html = html.replaceAll("\n", "");
			html = html.replaceAll("\r", "");
			html = html.replaceAll("\t", "");
		} catch (Exception ex) {}
		commentSB.append(html);
		commentSB.append("\n<br>&nbsp;&nbsp;&nbsp;");
		commentSB.append("<b>target:</b>");
		JSONArray targets = node.getJSONArray("target");
		String del = "";
		for(int i=0; i<targets.length(); i++) {
			String target = targets.getString(i);
			commentSB.append(del);
			commentSB.append(target);
			del = ", ";
		}
		return commentSB.toString();
	}
}
