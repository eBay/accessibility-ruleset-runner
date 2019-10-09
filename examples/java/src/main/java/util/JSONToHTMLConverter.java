package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

public class JSONToHTMLConverter {
	
	String reportLocation;
	String pageTemplate;

	String failedRowTemplate;
	String exceptionTemplate;
	String stackElementTemplate;
	String reasonTemplate;
	String testStepsTemplate;
	String jiraLinkTemplate;
	String jpmTable;

	public void convert(JSONObject results) {
		this.pageTemplate = HTMLGenerator.getPageTemplate(JSONToHTMLConverter.class, "HTMLReportTemplate.html");
		
	  	failedRowTemplate = HTMLGenerator.getInnerTemplate(pageTemplate, "<!--ROW_TEMPLATE-->");
	  	exceptionTemplate = HTMLGenerator.getInnerTemplate(pageTemplate, "EXCEPTION_TEMPLATE");
	  	stackElementTemplate = HTMLGenerator.getInnerTemplate(exceptionTemplate, "STACK_ELEMENT_TEMPLATE");
	  	reasonTemplate = HTMLGenerator.getInnerTemplate(failedRowTemplate, "<!--REASON-->");
	  	testStepsTemplate = HTMLGenerator.getInnerTemplate(failedRowTemplate, "<!--TEST_STEP_ITEM-->");
	  	jiraLinkTemplate = HTMLGenerator.getInnerTemplate(failedRowTemplate, "<!--JIRA_LINK-->");
	  	
	  	jpmTable = HTMLGenerator.getInnerTemplate(failedRowTemplate, "<!--JPM_TABLE-->");
	  	
	  	generateHTML(results);
	}

	public void generateHTML(JSONObject results) {
		String homePage = HTMLGenerator.substituteMarker(pageTemplate, "<!--ROWS_TEMPLATE-->", listAllRowsForFailures(myTest));
		WAEFileHelper.writeFile(new File(reportLocation), homePage);
	}

	private String listAllRowsForFailures(MyTest myTest) {
		StringBuilder dataHtml = new StringBuilder();

		try {

			for (MyAssertionsTrackerInt atInt : myTest.getAssertions()) {
				MyRule myRule = (MyRule) atInt;
				if (myRule.getNumberOfAssertionsFailed() > 0) {
					
					// Generate html for page
					String rowHTML = failedRowTemplate;

					List<String> numberedCommentList = createNumberedCommentList(myRule);
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE1-->", myRule.getRule().getRuleCode());
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_CODE2-->", myRule.getRule().getRuleCode());
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_NAME-->", myRule.getRule().getLongName());
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--RULE_DETAILS-->", myRule.getRule().getDescriptionHTML());
					// Generate the HTMl for showing compliance level of each rule
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL1-->", myRule.getRule().getRuleComplianceLevel());
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--COMPLIANCE_LEVEL2-->", myRule.getRule().getRuleComplianceLevel());
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_SOURCE-->", "<a title='View Rule Help Overlay' class='modalInput' rel='#"+myRule.getRule().getRuleCode()+"_HELP' relParent='#failedAPIContent'>");
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_ANCHOR_TARGET-->", "<div class='modal' id='"+myRule.getRule().getRuleCode()+"_HELP' style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>");
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--WCAG_ANCHOR_SOURCE-->", "<a target='_blank' title='Rule - WCAG Documentation' href='"+myRule.getRule().getWCAGURL()+"'>");
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--OVERLAY_MASK_TARGET-->", "<div class='exposeMask' id='"+myRule.getRule().getRuleCode()+"_HELP_MASK' ></div>");
					System.out.println("buildTestDetails... myTest.name:"+myTest.getPageImage());
					String testDetails = buildTestDetails(myTest, myRule, numberedCommentList);

					System.out.println("buildEmailerLink... myTest.name:"+myTest.getPageImage());
					String emailLink = buildEmailerLink(testDetails);
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--EMAILER_LINK-->", emailLink);
					
					String key = new MyJiraDaoImpl().generateJiraKey(myTest, myRule);

					System.out.println("constructJPMTable... myTest.name:"+myTest.getPageImage());
					String jpmTable = constructJPMTable(WAEProperties.JIRA_PROJECT_KEY.getPropertyValue());

					System.out.println("buildJIRALink... myTest.name:"+myTest.getPageImage());
					String jirapLink = buildJIRALink(key, jpmTable, testDetails);
//					System.out.println("Building JIRA Link:"+jirapLink);
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--JIRA_LINK-->", jirapLink);

					String href = myRule.getRule().getPatternHREF();
					if(href != null && !href.isEmpty()) {
						rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--PATTERNS_LINK_ANCHOR-->", "<a id='patternLink' title='Rule - Accessibility Pattern' target='_blank' href="+HTMLGenerator.addSingleQuotes(href)+">");
					} else {
						rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--PATTERNS_LINK-->", "");
					}

					System.out.println("buildHTMLForColumnReason... myTest.name:"+myTest.getPageImage());
					rowHTML = HTMLGenerator.substituteMarker(rowHTML, "<!--REASONS-->", buildHTMLForColumnReason(myTest, myRule, numberedCommentList));
					
					dataHtml.append(rowHTML.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return buildHTMLForPageException(e);
		}
		return dataHtml.toString();
	}

	private List<String> createNumberedCommentList(MyRule myRule) {
		List<String> commentList = new ArrayList<String>();
		for (int stepsCounter=1; stepsCounter<myRule.getNumberOfAssertionsFailed()+1; stepsCounter++) {
			String step = myRule.getMyFailure(stepsCounter-1).generateWAEComment();
			commentList.add(stepsCounter + " . " + step);
		}
		return commentList;
	}

	public static void main (String[] args) {
		JSONToHTMLConverter ppi = new JSONToHTMLConverter("");
		String output = ppi.buildHTMLForPageException(new Exception());
		System.out.println("output:"+output);
	}
	
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

	private String buildTestDetails(MyTest myTest, MyRule myRule,
			List<String> numberedCommentList) {
		String testDetails = "" 
				+ "Test Details:" 
				+ "%0A  WAE: http://go/wae"
				+ "%0A  Page Name: " + myTest.getViewName()
				+ "%0A  URL: " + myTest.getUrl() 
				+ "%0A  Rule Name: " + myRule.getName() 
				+ "%0A  Rule Code: " + myRule.getRule().getRuleCode()
				+ "%0A  Rule Compliance Level: " + myRule.getRule().getRuleComplianceLevel();
				
		System.out.println("buildTestDetails WAEProperties.STORAGE_FOLDER.getPropertyValue():"+WAEProperties.STORAGE_FOLDER.getPropertyValue());
		if(!WAEProperties.STORAGE_FOLDER.getPropertyValue().equals("")) {
			if(!WAEProperties.JIRA_PROJECT_KEY.getPropertyValue().equals("") && !WAEProperties.JIRA_PROJECT_KEY.getPropertyValue().equals("ACCESSIB")) {
				System.out.println("buildTestDetails WAEProperties.JIRA_PROJECT_KEY.getPropertyValue():"+WAEProperties.JIRA_PROJECT_KEY.getPropertyValue());
				testDetails = testDetails + "%0A"
						+ "%0APhase 3 Accessibility Issues Dashboard: http://go/ahr";
				testDetails = testDetails + "%0A %0AWAE Monitoring Overview: "+WAECredentials.AHR_SERVER_URL+"generateWAEOverview?projectKey="+WAEProperties.JIRA_PROJECT_KEY.getPropertyValue()+"%26pageFilter=ALL%26pageFilter=HPV";
			}
			// space encoded is %20, % encoded is %25.  space double encoded is %25%20.  This is double encoded because both JIRA and Outlook will display decoded.  To link in the email and jira ticket, the single encoded must be displayed. 
			testDetails = testDetails + "%0A %0AWAE Report: "+WAECredentials.AHR_STORAGE_IIS_URL+WAEProperties.STORAGE_FOLDER.getPropertyValue().replaceAll(" ", "%2520")+"/WAE/current/WAEreports/WAeTestReport.html";
			testDetails = testDetails + "%0A %0APage Test Summary: "+WAECredentials.AHR_STORAGE_IIS_URL+WAEProperties.STORAGE_FOLDER.getPropertyValue().replaceAll(" ", "%2520")+"/WAE/current/WAEreports/failedtests"
					+ /* timeStamp */myTest.getViewName().replaceAll(" ", "%2520") + ".htm";
		}
		
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

	public String buildJIRALink(String jafKey, String jpmTable, String testDetailsDescription) {
		try {	

				// Construct Jira Auto Filer Overlay Header
				String jiraLinkHeader = HTMLGenerator.getInnerTemplate(jiraLinkTemplate, "<!--JIRA_LINK_HEADER-->");
	
				// Construct Jira Auto Filer Overlay JAF Key Table
				String jafKeyTable = HTMLGenerator.getInnerTemplate(jiraLinkTemplate, "<!--JAF_KEY_TABLE-->");
				jafKeyTable = HTMLGenerator.substituteMarker(jafKeyTable, "<!--JAF_KEY-->", jafKey);
				jafKeyTable = HTMLGenerator.substituteMarker(jafKeyTable, "<!--JAF_KEY_TABLE_LINK-->", "<a target='_blank' href="+HTMLGenerator.addSingleQuotes("https://jirap.corp.ebay.com/issues/?jql=labels%3D%27"+jafKey+"%27")+">View Existing Bug</a>");
	
//				String jpmTable = WAEJPMAdapter.constructJPMTableFromUrl(myTest.getUrl());

				String projectID = "" + ProjectsModelHolder.projectModelHolder.projectKeyToProjectMap.get("ACCESSIB").id;
				try {
					projectID = "" + ProjectsModelHolder.projectModelHolder.projectKeyToProjectMap.get(WAEProperties.JIRA_PROJECT_KEY.getPropertyValue()).id;
				} catch (Exception ex) {
					
				}
	
				// Construct Jira Auto Filer Overlay JIRA Link
				// VIP - 11564
				// ACCESSIB - 18070
				StringBuilder jirapLink = new StringBuilder();
				jirapLink.append("<a id='supportLnk' target ='_blank' href=\"https://jirap.corp.ebay.com/secure/CreateIssueDetails!init.jspa?");
				jirapLink.append("pid=");
				jirapLink.append(projectID); // This is the projectID
				jirapLink.append("&labels="+jafKey);
				jirapLink.append("&labels=WAE_JAF");
				jirapLink.append("&labels=WAEReport");
//				jirapLink.append("&labels=OOSLA_Exemptions"); // Removed 9/20/2016
				jirapLink.append("&labels=Accessibility_BugBash");
				jirapLink.append("&customfield_11205=14302");
				jirapLink.append("&customfield_11206=14311");
				jirapLink.append("&customfield_11207=14312");
				jirapLink.append("&customfield_11208=14323");
				jirapLink.append("&customfield_11209=14324");
				jirapLink.append("&issuetype=1&summary=");
				jirapLink.append("&description=");
				jirapLink.append(testDetailsDescription);
				jirapLink.append("\">");
	
				StringBuilder anchor = new StringBuilder();
				anchor.append("\n\n");
				anchor.append("<a title='Rule - File a Bug' class='modalInput' rel="+HTMLGenerator.addSingleQuotes('#'+jafKey)+" relParent='#failedAPIContent'><img alt='Jira Auto Filer' style='width: 17px; height: 17px;' src='"+WAECredentials.IMAGES_URL+"JAF.png' /></a>");
				anchor.append("<div class='modal' id="+HTMLGenerator.addSingleQuotes(jafKey)+" style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>");
				anchor.append(jiraLinkHeader);
				anchor.append(jafKeyTable);
				anchor.append(jpmTable);
				anchor.append("<br>");
				anchor.append(jirapLink);
				anchor.append("<img src='"+WAECredentials.IMAGES_URL+"FileABug.png' />");
				anchor.append("</a>");
				anchor.append("&nbsp;&nbsp;&nbsp;<img class='close' src='"+WAECredentials.IMAGES_URL+"close.png' />");
				anchor.append("</div>");
				anchor.append("<div class='exposeMask' id="+HTMLGenerator.addSingleQuotes(jafKey+"_MASK")+" ></div>");
				return anchor.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ""; // Machine may not have access to JIRA
		}
	}

	private String constructJPMTable(String projectKeyToSelect) {
		String jpmTable = this.jpmTable;
		
		Collection<ProjectsModelProject> projects = ProjectsModelHolder.projectModelHolder.projectKeyToProjectMap.values();
		List<ProjectsModelProject> projectList = new ArrayList<ProjectsModelProject>(projects);
		for(Object object: projectList.toArray()) {
			ProjectsModelProject project = (ProjectsModelProject) object;
			if(project.name == null && project.infoHubName == null) {
				projectList.remove(project);
			}
		}
		try {
			Collections.sort(projectList);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ERROR!!! Sort Failed");
			System.out.println("ERROR!!! Sort Failed");
			System.out.println("ERROR!!! Sort Failed");
			System.out.println("ERROR!!! Sort Failed");
			
		}
			
		Set<String> healthReportProjectNames = new HashSet<String>();
		healthReportProjectNames.add("Accessibility Evaluator");
		healthReportProjectNames.add("Advertising");
		healthReportProjectNames.add("Billing");
		healthReportProjectNames.add("C2C Seller Experience My eBay");
		healthReportProjectNames.add("CS OCS");
		healthReportProjectNames.add("Checkout Applications");
		healthReportProjectNames.add("Collections");
		healthReportProjectNames.add("Feed");
		healthReportProjectNames.add("Global Header");
		healthReportProjectNames.add("Messages Raptor");
		healthReportProjectNames.add("MYEBAYBUYING - My eBay Buying");
		healthReportProjectNames.add("Registration");
		healthReportProjectNames.add("Search FE");
		healthReportProjectNames.add("Seller Marketing Engine");
		healthReportProjectNames.add("Shipping");
		healthReportProjectNames.add("TXNFLOW - Transaction Flow");
		healthReportProjectNames.add("Verification Platform-Seller Registration");
		healthReportProjectNames.add("View Item Page - VIP");
		healthReportProjectNames.add("View Order Details");
		healthReportProjectNames.add("Webnext/Gandalf - Unified Selling Flow");
		
		ProjectsModelProject projectToSelect = ProjectsModelHolder.projectModelHolder.projectKeyToProjectMap.get(projectKeyToSelect);
		String name = projectToSelect.infoHubName;
		if(projectToSelect.infoHubName == null) {
			name = projectToSelect.name;
		}
		jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--EF_PROJECT-->", "<a target='_blank' href='"+WAECredentials.EF_SERVER_URL+"project?projectKey="+projectToSelect.infoHubKey+"'>"+name+"</a>");
		
		String displayMessage = "";
		String delimiter = "<br><b>Errors:</b> ";
		for(ProjectErrorMessage pem: projectToSelect.errorMessage) {
			displayMessage = displayMessage + delimiter + pem.getDisplayMessage();
			delimiter = ",";
		}
		jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--EF_ERROR-->", displayMessage);
		
		if(projectToSelect.infoHubID != null) {
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_HUB_DOMAIN-->", "<a target='_blank' href='https://infohub.corp.ebay.com/domain/"+projectToSelect.infoHubDomainID+"/view.do'>"+projectToSelect.infoHubDomainName+"</a>");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_HUB_DEDICATED_TEAM-->", "<a target='_blank' href='https://infohub.corp.ebay.com/dedicatedTeam/"+projectToSelect.infoHubDedicatedTeamID+"/view.do'>"+projectToSelect.infoHubDedicatedTeamName+"</a>");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_PROJECT-->", "<a target='_blank' href='https://infohub.corp.ebay.com/project/"+projectToSelect.infoHubID+"/view.do'>"+projectToSelect.infoHubName+"</a>");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_PROJECT_KEY-->", projectToSelect.infoHubKey);
			
			if(projectToSelect.projectOwners.isEmpty()) {
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_PROJECT_OWNER-->", "Not Found");
			} else {
				String pos = "";
				String del = "";
				for(String po: projectToSelect.projectOwners) {
					String pd = ProjectsModelHolder.projectModelHolder.infoHubLoginToDisplayMap.get(po);
					pos = pos + del + "<a target='_blank' href='https://infohub.corp.ebay.com/user/"+po+"/infohub/info.do'>"+pd+"</a>";
					delimiter = ", ";
				}
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_PROJECT_OWNER-->", pos);
			}
		} else {
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_HUB_DOMAIN-->", "Not Found in Info Hub");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_HUB_DEDICATED_TEAM-->", "Not Found in Info Hub");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_PROJECT-->", "Not Found in Info Hub");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_PROJECT_KEY-->", "Not Found in Info Hub");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--INFO_PROJECT_OWNER-->", "Not Found in Info Hub");
		}
		if(projectToSelect.id != null) {
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_ID_DISPLAY-->", projectToSelect.id);
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_NAME_DISPLAY2-->", "<a target='_blank' href='https://jirap.corp.ebay.com/browse/"+projectToSelect.key+"'>"+projectToSelect.name+"</a>");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_KEY_DISPLAY-->", projectKeyToSelect);
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_CATEGORY-->", projectToSelect.jiraCategory);

			if(projectToSelect.jiraLeadName == null) {
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_LEAD-->", "Not Found");
			} else {
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_LEAD-->", "<a target='_blank' href='https://jirap.corp.ebay.com/secure/ViewProfile.jspa?name="+projectToSelect.jiraLeadName+"'>"+projectToSelect.jiraLeadDisplayName+"</a>");
			}
			if(projectToSelect.issueCount == null) {
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--ISSUES_FILED-->", "Not Found");
			} else {
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--ISSUES_FILED-->", ""+projectToSelect.issueCount);
			}
		} else {
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_ID_DISPLAY-->", "Not Found in JIRA");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_NAME_DISPLAY2-->", "Not Found in JIRA");
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_KEY_DISPLAY-->", "Not Found in JIRA");	
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_CATEGORY-->", "Not Found in JIRA");	
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--PROJECT_LEAD-->", "Not Found in JIRA");	
			jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--ISSUES_FILED-->", "Not Found in JIRA");	
		}
		
		// Process Options Last

		String options = "";
		for(ProjectsModelProject project: projectList) {			
			StringBuilder value = new StringBuilder();
			value.append(project.infoHubDomainName);
			value.append(":::");
			value.append(project.infoHubDomainID);
			value.append(":::");
			value.append(project.infoHubDedicatedTeamName);
			value.append(":::");
			value.append(project.infoHubDedicatedTeamID);
			value.append(":::");
			value.append(project.infoHubName);
			value.append(":::");
			value.append(project.infoHubID);
			value.append(":::");
			value.append(project.name);
			value.append(":::");
			value.append(project.id);
			value.append(":::");
			value.append(project.key);
			value.append(":::");
			if(project.errorMessage.isEmpty()) {
				value.append("None");
			} else {
				String delimiterAAA = "";
				for(ProjectErrorMessage pem: project.errorMessage) {
					value.append(delimiterAAA + pem.getDisplayMessage());
					delimiterAAA = ":";
				}
			}
			value.append(":::");
			value.append(project.infoHubKey);
			value.append(":::");
			value.append(project.jiraCategory);
			value.append(":::");
			value.append(project.issueCount);
			value.append(":::");
			if(project.projectOwners.isEmpty()) {
				value.append("None");
			} else {
				String delimiterBBB = "";
				for(String po: project.projectOwners) {
					String pd = ProjectsModelHolder.projectModelHolder.infoHubLoginToDisplayMap.get(po);
					value.append(delimiterBBB + po + ":" + pd);
					delimiterBBB = "::";
				}
			}
			value.append(":::");
			if(project.jiraLeadName == null) {
				value.append("None");
			} else {
				value.append(project.jiraLeadName + ":" + project.jiraLeadDisplayName);
			}
//			// TODO - Add Info Hub Project Owner and JIRA Lead
			
			String additionalOptionTags = "";
			String prefix = "Active Project";
			if(projectKeyToSelect.equals(project.key)) {
				prefix = "Default Project";
				additionalOptionTags = " selected='selected' class='highGreen'";
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_1-->", "<td id='efTD1' class='highGreen'>");
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_2-->", "<td id='efTD2' class='highGreen'>");
				jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_3-->", "<td id='efTD3' class='highGreen'>");
			} else if (healthReportProjectNames.contains(project.infoHubName)){
				prefix = "NFB Project: ";
				additionalOptionTags = " class='highGreen'";
				// Bug Fixed - Was substituting the starting value for each project!
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_1-->", "<td id='efTD1' class='highGreen'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_2-->", "<td id='efTD2' class='highGreen'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_3-->", "<td id='efTD3' class='highGreen'>");
			} else if (project.errorMessage.contains(ProjectErrorMessage.MISSING_INFO_HUB_ID)) {
				prefix = "Retired Project";
				additionalOptionTags = " class='gray'";
				// Bug Fixed - Was substituting the starting value for each project!
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_1-->", "<td id='efTD1' class='gray'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_2-->", "<td id='efTD2' class='gray'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_3-->", "<td id='efTD3' class='gray'>");
			} else if (!project.errorMessage.isEmpty()) { // No Issues
				prefix = "Invalid Project";
				additionalOptionTags = " class='yellow'";
				// Bug Fixed - Was substituting the starting value for each project!
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_1-->", "<td id='efTD1' class='yellow'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_2-->", "<td id='efTD2' class='yellow'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_3-->", "<td id='efTD3' class='yellow'>");
			} else {
				additionalOptionTags = " class='green'";
				// Bug Fixed - Was substituting the starting value for each project!
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_1-->", "<td id='efTD1' class='green'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_2-->", "<td id='efTD2' class='green'>");
//				jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--EF_TD_3-->", "<td id='efTD3' class='green'>");
			}
			String nameAAA = project.infoHubName;
			if(project.infoHubName == null) {
				nameAAA = project.name;
			}
			String errorPrefix = "";
			if(!project.errorMessage.isEmpty()) {
				errorPrefix = " (";
				String del = "";
				for(ProjectErrorMessage pem: project.errorMessage) {
					errorPrefix = errorPrefix + del + pem.getErrorCode();
					del = ", ";
				}
				errorPrefix = errorPrefix + ")";
			}
			options = options + "<option value='"+value+"'"+additionalOptionTags+">"+nameAAA+": "+prefix+errorPrefix+"</option>";
//			System.out.println("value:"+value);
		}
		
		jpmTable = HTMLGenerator.substituteMarker(jpmTable, "<!--JIRA_SELECT_COMBO_BOX_OPTIONS-->", options);
//		jpmTable = WAEHTMLGenerator.substituteMarker(jpmTable, "<!--JIRA_SELECT_COMBO_BOX_OPTIONS-->", "<option value='Data Services and Solutions:::464:::Data:::230:::Alation Self-Service Analytics:::null:::Alation Self-Service Analytics:::null:::null:::ALATION'>Alation Self-Service Analytics</option>" +
//				"<option value='Verticals and Deals:::558456f2e4b020a43992899d:::Verticals and Deals:::628:::AEON:::null:::AEON:::null:::null:::AEON'>AEON</option>");

		
			
		return jpmTable;
	}

	public String buildHTMLForColumnReason(MyTest myTest, MyRule myRule,
			List<String> numberedCommentList) {
		StringBuffer testStepsHTML = new StringBuffer();
		int counter = 0;
		for (String comment : numberedCommentList) {
			String alteredComment = addStripToComment(myTest, myRule, comment, counter);
			String testStepItem = HTMLGenerator.substituteMarker(this.testStepsTemplate,
					"<!--TEST_STEP_DETAILS-->", alteredComment); // ORIGINAL
			testStepsHTML.append(testStepItem);
			counter++;
		}

		String reason = reasonTemplate;
		reason = HTMLGenerator.substituteMarker(reason, "<!--TEST_STEPS-->",
				testStepsHTML.toString());

		return reason.toString();
	}


private String addStripToComment(MyTest myTest, MyRule myRule, String comment, Integer counter) {
	comment = comment + "<br>";
	try {
		String imageName = myRule.getMyFailure(counter).getImage();
		if (imageName != null) {
			String[] names = imageName.split("\\.");
			String anchor = "\n\n"
					+ "<a title='View Element in Snapshot Overlay' class='modalInput' rel="+HTMLGenerator.addSingleQuotes('#'+names[0])+" relParent='#failedAPIContent'>"
					+ "<img alt='camera' style='width: 32px; height: 25px;' src='"+WAECredentials.IMAGES_URL+"cam.png' />"
					+ "</a>"
					+ "<div class='modal' id="+HTMLGenerator.addSingleQuotes(names[0])+" style='background-color:#fff;display:none;padding:15px;border:2px solid #333;-webkit-border-radius:6px;'>"
					+ "<image src="+HTMLGenerator.addSingleQuotes(imageName)+" width='100%' height='100%'/>"
					+ "<br>"
					+ "<img class='close' src='"+WAECredentials.IMAGES_URL+"close.png' />"
					+ "</div>"
					+ "<div class='exposeMask' id="+HTMLGenerator.addSingleQuotes(names[0]+"_MASK")+" ></div>";
			comment = comment + anchor;
		}
	} catch (Exception ex) {
	}
	
	ValidationRulesErrorCodes errorCode = myRule.getMyFailure(counter).getFailureCode();

	// Default id is from id attribute. If that fails, try class, if that
	// fails, use the text of the tag
	
	comment = comment
			+ "<a target='_blank' title='Element - eBay Wiki for Error:"+errorCode.getErrorCode()+"' href='"
			+ errorCode.getURL() + "' >"
			+ "<img alt='WIKI' style='width: 20px; height: 20px;' src='"+WAECredentials.IMAGES_URL+"WIKI.png' />"
			+  "</a>";

	// Generate ID
	MyFailure failure = myRule.getMyFailure(counter);
	String tag = failure.getTag();
	String clazz = failure.getHtmlClass();
	String name = failure.getName();
	String id = failure.getID();
	comment = comment
			+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; tag="
			+ tag;
	if (clazz != null && !clazz.isEmpty()) {
		comment = comment
				+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; class="
				+ clazz;
	}
	if (name != null && !name.isEmpty()) {
		comment = comment
				+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; name="
				+ name;
	}
	if (id != null && !id.isEmpty()) {
		comment = comment
				+ "\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; id="
				+ id;
	}
	
	// New addition
	comment = comment + "<br>\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xpath="+failure.getXPath();
	return comment;
}

}
