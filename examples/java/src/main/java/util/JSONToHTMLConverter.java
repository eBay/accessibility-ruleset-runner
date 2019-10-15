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

package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
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
		this.reportLocation = "output/"+results.getString("viewName")+".html";
		
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
		report = HTMLGenerator.substituteMarker(report, "<!--VIEW_SCREENSHOT-->", createViewScreenShotAnchor(results));
		report = HTMLGenerator.substituteMarker(report, "<!--ROWS_TEMPLATE-->", listAllRowsForFailures(results));
		writeFile(new File(reportLocation), report);
		System.out.println("reportLocation:"+new File(reportLocation).getAbsolutePath());
	}

	private String createViewScreenShotAnchor(JSONObject results) {
		String viewImage = results.getString("viewImage");
		String[] names = viewImage.split("\\.");
		StringBuilder anchor = new StringBuilder();
		anchor.append("\n\n");
		anchor.append("<a title='View Page in Snapshot Overlay' class='modalInput' rel="+HTMLGenerator.addSingleQuotes('#'+names[0]+"view")+" relParent='#leftPanel'>");
		anchor.append("<img alt='camera' style='width: 32px; height: 25px;' src='cam.png' />");
		anchor.append("</a>");
		anchor.append("<div class='modal' id="+HTMLGenerator.addSingleQuotes(names[0]+"view")+" style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>");
		anchor.append("<image src="+HTMLGenerator.addSingleQuotes(viewImage)+" width='100%' height='100%'/>");
		anchor.append("<br>");
		anchor.append("<img class='close' src='close.png' />");
		anchor.append("</div>");
		anchor.append("<div class='exposeMask' id="+HTMLGenerator.addSingleQuotes('#'+names[0]+"view_MASK")+" ></div>");
		return anchor.toString();
	}

	private String listAllRowsForFailures(JSONObject results) {
		String viewName = results.getString("viewName");
		String url = results.getString("url");
		
		StringBuilder dataHtml = new StringBuilder();

		try {
			JSONArray customArray = results.getJSONArray("custom");
			for (int i=0; i<customArray.length(); i++) {
				JSONObject customRule = customArray.getJSONObject(i);
				JSONArray failedElements = customRule.getJSONArray("elements");
				if (failedElements.length() > 0) {
					// Generate html for page
					String rowHTML = failedRowTemplate;

					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE1-->", customRule.getString("ruleCode"));
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE2-->", customRule.getString("ruleCode"));
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_NAME-->", customRule.getString("rule"));
					
					CustomRulesetRules customRulesetRule = CustomRulesetRules.getCustomRulesetRulesFromLongName(customRule.getString("rule"));
					
					String descriptionHTML = getInnerTemplate(ruleDescriptionsTemplate, customRulesetRule.getTemplateString());
					
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_DETAILS-->", descriptionHTML);
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

					List<String> numberedCommentList = createNumberedCommentList(failedElements);	
					
					System.out.println("buildTestDetails... viewName:"+viewName);			
					String testDetails = buildTestDetails(viewName, url, customRulesetRule, numberedCommentList);

					System.out.println("buildEmailerLink... viewName:"+viewName);
					String emailLink = buildEmailerLink(testDetails);
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--EMAILER_LINK-->", emailLink);

					System.out.println("buildHTMLForColumnReason... viewName:"+viewName);	
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--REASONS-->", buildHTMLForColumnReason(customRulesetRule, failedElements, numberedCommentList));
					
					dataHtml.append(rowHTML.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			String oops = exceptionTemplate;
			oops = HTMLGenerator.substituteMarker(oops, "STACK_ELEMENT_TEMPLATE", buildHTMLForViewExceptionStackTraceElements(e));
			return oops;
		}
		return dataHtml.toString();
	}

	private List<String> createNumberedCommentList(JSONArray elementFailures) {
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

	public String buildHTMLForColumnReason(CustomRulesetRules customRulesetRule, JSONArray elementFailures,
			List<String> numberedCommentList) {
		StringBuffer testStepsHTML = new StringBuffer();
		for (int i=0; i<elementFailures.length(); i++) {
			JSONObject elementFailure = elementFailures.getJSONObject(i);
			String alteredComment = addStripToComment(customRulesetRule, elementFailure, numberedCommentList.get(i), i+1);
			String testStepItem = HTMLGenerator.substituteMarker(this.testStepsTemplate,
					"<!--TEST_STEP_DETAILS-->", alteredComment); // ORIGINAL
			testStepsHTML.append(testStepItem);
		}

		String reason = reasonTemplate;
		reason = HTMLGenerator.substituteMarker(reason, "<!--TEST_STEPS-->",
				testStepsHTML.toString());

		return reason.toString();
	}

	private String addStripToComment(CustomRulesetRules customRulesetRule, JSONObject elementFailure, String comment, Integer counter) {
		StringBuilder commentSB = new StringBuilder();
		commentSB.append(comment);
		commentSB.append("<br>");
//		try {
//			String imageName = myRule.getMyFailure(counter).getImage();
//			if (imageName != null) {
//				String[] names = imageName.split("\\.");
//				String anchor = "\n\n"
//						+ "<a title='View Element in Snapshot Overlay' class='modalInput' rel="+HTMLGenerator.addSingleQuotes('#'+names[0])+" relParent='#failedAPIContent'>"
//						+ "<img alt='camera' style='width: 32px; height: 25px;' src='"+ARRCredentials.IMAGES_URL+"cam.png' />"
//						+ "</a>"
//						+ "<div class='modal' id="+HTMLGenerator.addSingleQuotes(names[0])+" style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>"
//						+ "<image src="+HTMLGenerator.addSingleQuotes(imageName)+" width='100%' height='100%'/>"
//						+ "<br>"
//						+ "<img class='close' src='close.png' />"
//						+ "</div>"
//						+ "<div class='exposeMask' id="+HTMLGenerator.addSingleQuotes(names[0]+"_MASK")+" ></div>";
//				comment = comment + anchor;
//			}
//		} catch (Exception ex) {
//		}
	
		// Default id is from id attribute. If that fails, try class, if that fails, use the text of the tag
	
		// Generate ID
		commentSB.append("\n&nbsp;&nbsp;&nbsp;");
		commentSB.append("<b>tag:</b>");
		commentSB.append(elementFailure.getString("elementTag"));
		if (elementFailure.has("elementClass") && !elementFailure.getString("elementClass").isEmpty()) {
			commentSB.append("\n&nbsp;&nbsp;&nbsp;");
			commentSB.append("<b>class:</b>");
			commentSB.append(elementFailure.getString("elementClass"));
		}
		if (elementFailure.has("elementName") && !elementFailure.getString("elementName").isEmpty()) {
			commentSB.append("\n&nbsp;&nbsp;&nbsp;");
			commentSB.append("<b>name:</b>");
			commentSB.append(elementFailure.getString("elementName"));
		}
		if (elementFailure.has("elementID") && !elementFailure.getString("elementID").isEmpty()) {
			commentSB.append("\n&nbsp;&nbsp;&nbsp;");
			commentSB.append("<b>id:</b>");
			commentSB.append(elementFailure.getString("elementID"));
		}
		
		// New addition
		commentSB.append("<br>\n&nbsp;&nbsp;&nbsp;");
		commentSB.append("<b>xpath:</b>");
		commentSB.append(elementFailure.getString("elementXPATH"));
		return commentSB.toString();
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
}
