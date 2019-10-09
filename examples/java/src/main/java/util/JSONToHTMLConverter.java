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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;

public class JSONToHTMLConverter extends HTMLGenerator{
	
	String reportLocation;
	String pageTemplate;

	String failedRowTemplate;
	String exceptionTemplate;
	String stackElementTemplate;
	String reasonTemplate;
	String testStepsTemplate;
	String jiraLinkTemplate;
	String jpmTable;

	public static void main (String[] args) {
		JSONToHTMLConverter ppi = new JSONToHTMLConverter();
		String output = ppi.buildHTMLForPageException(new Exception());
		System.out.println("output:"+output);
	}

	public void convert(JSONObject results) {
		this.pageTemplate = getPageTemplate(JSONToHTMLConverter.class, "HTMLReportTemplate.html");
		
	  	failedRowTemplate = getInnerTemplate(pageTemplate, "<!--ROW_TEMPLATE-->");
	  	exceptionTemplate = getInnerTemplate(pageTemplate, "EXCEPTION_TEMPLATE");
	  	stackElementTemplate = getInnerTemplate(exceptionTemplate, "STACK_ELEMENT_TEMPLATE");
	  	reasonTemplate = getInnerTemplate(failedRowTemplate, "<!--REASON-->");
	  	testStepsTemplate = getInnerTemplate(failedRowTemplate, "<!--TEST_STEP_ITEM-->");
	  	jiraLinkTemplate = getInnerTemplate(failedRowTemplate, "<!--JIRA_LINK-->");
	  	
	  	jpmTable = getInnerTemplate(failedRowTemplate, "<!--JPM_TABLE-->");

		String homePage = HTMLGenerator.substituteMarker(pageTemplate, "<!--ROWS_TEMPLATE-->", listAllRowsForFailures(results));
		writeFile(new File(reportLocation), homePage);
	}

	private String listAllRowsForFailures(JSONObject results) {
		StringBuilder dataHtml = new StringBuilder();

		try {

//			for (MyAssertionsTrackerInt atInt : myTest.getAssertions()) {
//				MyRule myRule = (MyRule) atInt;
//				if (myRule.getNumberOfAssertionsFailed() > 0) {
//					
//					// Generate html for page
//					String rowHTML = failedRowTemplate;
//
//					List<String> numberedCommentList = createNumberedCommentList(myRule);
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE1-->", myRule.getRule().getRuleCode());
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE2-->", myRule.getRule().getRuleCode());
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_NAME-->", myRule.getRule().getLongName());
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_DETAILS-->", myRule.getRule().getDescriptionHTML());
//					// Generate the HTMl for showing compliance level of each rule
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL1-->", myRule.getRule().getRuleComplianceLevel());
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL2-->", myRule.getRule().getRuleComplianceLevel());
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_SOURCE-->", "<a title='View Rule Help Overlay' class='modalInput' rel='#"+myRule.getRule().getRuleCode()+"_HELP' relParent='#failedAPIContent'>");
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_TARGET-->", "<div class='modal' id='"+myRule.getRule().getRuleCode()+"_HELP' style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>");
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--WCAG_ANCHOR_SOURCE-->", "<a target='_blank' title='Rule - WCAG Documentation' href='"+myRule.getRule().getWCAGURL()+"'>");
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_MASK_TARGET-->", "<div class='exposeMask' id='"+myRule.getRule().getRuleCode()+"_HELP_MASK' ></div>");
//					System.out.println("buildTestDetails... myTest.name:"+myTest.getPageImage());
//					String testDetails = buildTestDetails(myTest, myRule, numberedCommentList);
//
//					System.out.println("buildEmailerLink... myTest.name:"+myTest.getPageImage());
//					String emailLink = buildEmailerLink(testDetails);
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--EMAILER_LINK-->", emailLink);
//					
//					String key = new MyJiraDaoImpl().generateJiraKey(myTest, myRule);
//
//					System.out.println("constructJPMTable... myTest.name:"+myTest.getPageImage());
//					String jpmTable = constructJPMTable(WAEProperties.JIRA_PROJECT_KEY.getPropertyValue());
//
//					System.out.println("buildJIRALink... myTest.name:"+myTest.getPageImage());
//					String jirapLink = buildJIRALink(key, jpmTable, testDetails);
////					System.out.println("Building JIRA Link:"+jirapLink);
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--JIRA_LINK-->", jirapLink);
//
//					String href = myRule.getRule().getPatternHREF();
//					if(href != null && !href.isEmpty()) {
//						rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--PATTERNS_LINK_ANCHOR-->", "<a id='patternLink' title='Rule - Accessibility Pattern' target='_blank' href="+HTMLGenerator.addSingleQuotes(href)+">");
//					} else {
//						rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--PATTERNS_LINK-->", "");
//					}
//
//					System.out.println("buildHTMLForColumnReason... myTest.name:"+myTest.getPageImage());
//					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--REASONS-->", buildHTMLForColumnReason(myTest, myRule, numberedCommentList));
//					
//					dataHtml.append(rowHTML.toString());
//				}
//			}

		} catch (Exception e) {
			e.printStackTrace();
			return buildHTMLForPageException(e);
		}
		return dataHtml.toString();
	}

//	private List<String> createNumberedCommentList(MyRule myRule) {
//		List<String> commentList = new ArrayList<String>();
//		for (int stepsCounter=1; stepsCounter<myRule.getNumberOfAssertionsFailed()+1; stepsCounter++) {
//			String step = myRule.getMyFailure(stepsCounter-1).generateWAEComment();
//			commentList.add(stepsCounter + " . " + step);
//		}
//		return commentList;
//	}
	
	// If error occurs generate a page for exceptions
	public String buildHTMLForPageException(Exception e) {
		String oops = exceptionTemplate;
		oops = HTMLGenerator.substituteMarker(oops, "STACK_ELEMENT_TEMPLATE", buildHTMLForPageExceptionStackTraceElements(e));
		return oops;
		}
	
	private String buildHTMLForPageExceptionStackTraceElements(Exception e) {
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

	public String buildEmailerLink(String testDetailsDescription) {	
		
		String mailTo = "mailto:sizu@ebay.com?"
				+ "subject=Please look into the following bug identified by WAE"
				+ "&body=";
		mailTo = mailTo + testDetailsDescription;
		
		String emailLinkHTML = "<a id='emailerLink' title='Rule - Email Bug Details' href=\""
				+ mailTo + "\">";
		
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!"+emailLinkHTML);
		
		return emailLinkHTML;
	}

//	private String buildTestDetails(MyTest myTest, MyRule myRule,
//			List<String> numberedCommentList) {
//		String testDetails = "" 
//				+ "Test Details:" 
//				+ "%0A  WAE: http://go/wae"
//				+ "%0A  Page Name: " + myTest.getViewName()
//				+ "%0A  URL: " + myTest.getUrl() 
//				+ "%0A  Rule Name: " + myRule.getName() 
//				+ "%0A  Rule Code: " + myRule.getRule().getRuleCode()
//				+ "%0A  Rule Compliance Level: " + myRule.getRule().getRuleComplianceLevel();
//				
//		System.out.println("buildTestDetails WAEProperties.STORAGE_FOLDER.getPropertyValue():"+WAEProperties.STORAGE_FOLDER.getPropertyValue());
//		if(!WAEProperties.STORAGE_FOLDER.getPropertyValue().equals("")) {
//			if(!WAEProperties.JIRA_PROJECT_KEY.getPropertyValue().equals("") && !WAEProperties.JIRA_PROJECT_KEY.getPropertyValue().equals("ACCESSIB")) {
//				System.out.println("buildTestDetails WAEProperties.JIRA_PROJECT_KEY.getPropertyValue():"+WAEProperties.JIRA_PROJECT_KEY.getPropertyValue());
//				testDetails = testDetails + "%0A"
//						+ "%0APhase 3 Accessibility Issues Dashboard: http://go/ahr";
//				testDetails = testDetails + "%0A %0AWAE Monitoring Overview: "+WAECredentials.AHR_SERVER_URL+"generateWAEOverview?projectKey="+WAEProperties.JIRA_PROJECT_KEY.getPropertyValue()+"%26pageFilter=ALL%26pageFilter=HPV";
//			}
//			// space encoded is %20, % encoded is %25.  space double encoded is %25%20.  This is double encoded because both JIRA and Outlook will display decoded.  To link in the email and jira ticket, the single encoded must be displayed. 
//			testDetails = testDetails + "%0A %0AWAE Report: "+WAECredentials.AHR_STORAGE_IIS_URL+WAEProperties.STORAGE_FOLDER.getPropertyValue().replaceAll(" ", "%2520")+"/WAE/current/WAEreports/WAeTestReport.html";
//			testDetails = testDetails + "%0A %0APage Test Summary: "+WAECredentials.AHR_STORAGE_IIS_URL+WAEProperties.STORAGE_FOLDER.getPropertyValue().replaceAll(" ", "%2520")+"/WAE/current/WAEreports/failedtests"
//					+ /* timeStamp */myTest.getViewName().replaceAll(" ", "%2520") + ".htm";
//		}
//		
//		testDetails = testDetails + "%0A"
//				+ "%0AReasons:";
//		for (String comment : numberedCommentList) {
//			String alteredComment = "%0A  "
//					+ removeTagsAndDoubleQuotesFromComment(comment);
//			// Limit on URL is 2048
//			if (testDetails.length() + alteredComment.length() < 1000) {
//				testDetails = testDetails + alteredComment;
//			} else {
//				testDetails = testDetails + "%0A  ...";
//				break;
//			}
//		}
//			
//		return testDetails;
//	}

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

//	public String buildHTMLForColumnReason(MyTest myTest, MyRule myRule,
//			List<String> numberedCommentList) {
//		StringBuffer testStepsHTML = new StringBuffer();
//		int counter = 0;
//		for (String comment : numberedCommentList) {
//			String alteredComment = addStripToComment(myTest, myRule, comment, counter);
//			String testStepItem = HTMLGenerator.substituteMarker(this.testStepsTemplate,
//					"<!--TEST_STEP_DETAILS-->", alteredComment); // ORIGINAL
//			testStepsHTML.append(testStepItem);
//			counter++;
//		}
//
//		String reason = reasonTemplate;
//		reason = HTMLGenerator.substituteMarker(reason, "<!--TEST_STEPS-->",
//				testStepsHTML.toString());
//
//		return reason.toString();
//	}
//
//
//	private String addStripToComment(MyTest myTest, MyRule myRule, String comment, Integer counter) {
//		comment = comment + "<br>";
//		try {
//			String imageName = myRule.getMyFailure(counter).getImage();
//			if (imageName != null) {
//				String[] names = imageName.split("\\.");
//				String anchor = "\n\n"
//						+ "<a title='View Element in Snapshot Overlay' class='modalInput' rel="+HTMLGenerator.addSingleQuotes('#'+names[0])+" relParent='#failedAPIContent'>"
//						+ "<img alt='camera' style='width: 32px; height: 25px;' src='"+WAECredentials.IMAGES_URL+"cam.png' />"
//						+ "</a>"
//						+ "<div class='modal' id="+HTMLGenerator.addSingleQuotes(names[0])+" style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>"
//						+ "<image src="+HTMLGenerator.addSingleQuotes(imageName)+" width='100%' height='100%'/>"
//						+ "<br>"
//						+ "<img class='close' src='"+WAECredentials.IMAGES_URL+"close.png' />"
//						+ "</div>"
//						+ "<div class='exposeMask' id="+HTMLGenerator.addSingleQuotes(names[0]+"_MASK")+" ></div>";
//				comment = comment + anchor;
//			}
//		} catch (Exception ex) {
//		}
//		
//		ValidationRulesErrorCodes errorCode = myRule.getMyFailure(counter).getFailureCode();
//	
//		// Default id is from id attribute. If that fails, try class, if that
//		// fails, use the text of the tag
//		
//		comment = comment
//				+ "<a target='_blank' title='Element - eBay Wiki for Error:"+errorCode.getErrorCode()+"' href='"
//				+ errorCode.getURL() + "' >"
//				+ "<img alt='WIKI' style='width: 20px; height: 20px;' src='"+WAECredentials.IMAGES_URL+"WIKI.png' />"
//				+  "</a>";
//	
//		// Generate ID
//		MyFailure failure = myRule.getMyFailure(counter);
//		String tag = failure.getTag();
//		String clazz = failure.getHtmlClass();
//		String name = failure.getName();
//		String id = failure.getID();
//		comment = comment
//				+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; tag="
//				+ tag;
//		if (clazz != null && !clazz.isEmpty()) {
//			comment = comment
//					+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; class="
//					+ clazz;
//		}
//		if (name != null && !name.isEmpty()) {
//			comment = comment
//					+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; name="
//					+ name;
//		}
//		if (id != null && !id.isEmpty()) {
//			comment = comment
//					+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; id="
//					+ id;
//		}
//		
//		// New addition
//		comment = comment + "<br>\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xpath="+failure.getXPath();
//		return comment;
//	}

}
