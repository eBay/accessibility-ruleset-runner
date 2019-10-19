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

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.openqa.selenium.WebDriver;

/**
 * System properties used by ARR.
 * 
 * Works with many use cases:
 *  Jenkins
 *  Local Runs
 *  Mutli Threaded on the Grid
 *  
 * FOR NEW PROPERTIES:
 *   1) ADD HERE
 *   2) in projectDefault.properties
 *   3) pom.xml file (for Jenkins String parameters)
 *
 */
public enum ARRProperties {
	BROWSER, // "FF"
	BROWSER_SIZE, // "FF"
	REMOTE_URL,
	JIRA_PROJECT_KEY,
	URLS_TO_TEST, 
	SIGN_IN,
	USER_ID,
	PASSWORD, 
	ADD_TO_EMAIL, // "sizu@ebay.com
	XPATH_ROOT,
	XPATH_EXEMPTIONS, 
	USER_AGENT, 
	STORAGE_FOLDER,
	REPORT_TITLE,
	PRE_ACTIONS,
	OPT_IN_URL,
	CANONICAL_HOST_NAME,
	ADD_URL_PARAMETERS,
	VALIDATE_PAGE,
	BUILD_URL,
	RUN_AXE, 
	PROXY
	;
	
  static Map<WebDriver, String> driverToCID = new LinkedHashMap<WebDriver,String>();
  private Map<String, Boolean> printedOverrideMessage = new LinkedHashMap<String, Boolean>();
  private String value; // Should hold entire configuration
  // value may be PRE_ACTIONS=""
  // value may be PRE_ACTIONS="CLICK //div[@id='btnDone']
  // value may be PRE_ACTIONS="[PAGE_1] CLICK //div[@id='btnDone'], [PAGE_2] CLICK //div[@id='btnDone']"
  
  // ONLY CALL FROM TESTS
  public static void clear() {
	  driverToCID.clear();
	  for(ARRProperties prop: ARRProperties.values()) {
		  prop.value = null;
		  prop.printedOverrideMessage.clear();
	  }
  }
  
  /**
   * setPropertyValue directly
   * @param property
   */
	public void setPropertyValue(String property) {
		value = cleanProperty(property);
	}
  
  /**
   * OPTION 1: Get Property from System Properties (Includes Jenkins)
   * OPTION 2: Use default from projectDefault.properties
   * OPTION 3: setPropertyValue 
   * @return
   */
	public String getPropertyValue() {
		// This is not thread safe
		// Check if value == null
		// Assign System Property to value
		// 
		if (value == null) {
			value = cleanProperty(System.getProperty(this.toString()));
		}
		if (value == null) {
			value = cleanProperty(getPropertyFromPropertiesFile());
		}
		if (value == null) {
			value = "";
		}
		return value;
	}

	private static Properties prop;
	private String getPropertyFromPropertiesFile() {
		if (prop == null) {
			prop = new Properties();
			InputStream isCss = ARRProperties.class
					.getResourceAsStream("projectDefault.properties");
			try {
				prop.load(isCss);
				isCss.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String propertyFromPropertiesFile = prop.getProperty(this.name());
		System.out.println("Using property from projectDefault.properties, " + this + ":"
				+ propertyFromPropertiesFile);

		// Third, force certain actions for some parameters
		if (this == ARRProperties.URLS_TO_TEST) {
			if (propertyFromPropertiesFile
					.equals("[HomePage - Used as default since URLS_TO_TEST was not set while you ran ARR] http://www.ebay.com")) {
				System.out.println("");
				System.out.println("");
				System.out.println("************************ WARNING!!! MISSING PARAMETER *************************");
				System.out.println("");
				System.out.println("URLS_TO_TEST system property has to be set as shown below.");
				System.out.println("eg: mvn test -e -DURLS_TO_TEST=\"[eBay] http://www.ebay.com, [PayPal] http://www.paypal.com\" ");
				System.out.println("You can set -DURLS_TO_TEST=http://www.ebay.com in the VM arguments for mvn or TestNG");
				System.out.println("");
				System.out.println("************************ WARNING!!! MISSING PARAMETER *************************");
				System.out.println("");
				System.out.println("");
				System.out.println("Setting default, URLS_TO_TEST:" + propertyFromPropertiesFile);
			}
		} 
		
		return propertyFromPropertiesFile;
	}

	/**
	 * The purpose of this is to return null if the property is not properly set
	 * 
	 * @param property
	 * @return
	 */
	private String cleanProperty(String property) {
		if(property == null) {
			return null;
		}

		if(property.startsWith("\"") && property.endsWith("\"")) { // REMOVE DOUBLE QUOTES FROM BEGINNING AND END WHICH ARE INSERTED AUTOMATICALLY FOR JENKINS JOBS
			property = property.substring(1, property.length()-1);
		}
		if(property.isEmpty() || property.equals("null")
				|| property.trim().startsWith(this.toString())
				|| property.trim().startsWith("${"+this.toString()) // Jenkins will use ${STORAGE_FOLDER} 
			) { // URLS_TO_TEST contains URLS_TO_TEST
			return null;
		} else {
			return property;
		}
	}
	
	// Called by Engine to show which driver (multiple nodes in Jenkins) is testing which page
	public static void viewConfigurationsSetup(WebDriver driver, String convenientIdentifier) {
		driverToCID.put(driver, convenientIdentifier);
	}
	
	public String getPropertyValueBasedOnView(WebDriver driver) {
		// Second, look for page configurations within value
		// value may be PRE_ACTIONS="" 
		//   -> returns ""
		// value may be PRE_ACTIONS="CLICK //div[@id='btnDone']" 
		//   -> returns "CLICK //div[@id='btnDone']"
		// value may be PRE_ACTIONS="[PAGE_1] CLICK //div[@id='btnDone'], [PAGE_2] CLICK //div[@id='btnDone']"
		//   -> for PAGE_1 returns "CLICK //div[@id='btnDone']"
		//   -> for PAGE_2 returns "CLICK //div[@id='btnDone']"
		//   -> for PAGE_3 returns "" (default)
		String property = getPropertyValue();
		String cid = driverToCID.get(driver);
		try {
			// //div[@id='gh-gb']
			//
			// WAIT 3, REFRESH
			//
			// [GH Header 1] //div[@id='gh-gb'], [GH Header 2] //div[@id='Top'], [GH Footer] //html
			//
			// [GH Header 1] WAIT 3, REFRESH, [GH Header 2] WAIT 3, REFRESH, [GH Footer] WAIT 3, REFRESH
		    String[] arrayOfUrls = property.split(",");
		    // //div[@id='gh-gb']
		    //
		    // WAIT 3
		    // REFRESH
		    //
		    // [GH Header 1] //div[@id='gh-gb']
		    // [GH Header 2] //div[@id='Top']
		    // [GH Footer] //html
		    // 
		    // [GH Header 1] WAIT 3
		    // REFRESH
		    // [GH Header 2] WAIT 3
		    // REFRESH
		    // [GH Footer] WAIT 3
		    // REFRESH
		    String currentViewName = null;
		    String currentParameter = null;
		    for (String line : arrayOfUrls) {
		    	if(line.trim().startsWith("[")) {
		    		if(cid.equals(currentViewName)) {
		    			if(printedOverrideMessage.get(cid) == null) {
		    				 printedOverrideMessage.put(cid, true);
							System.out.println("Override value is being used for Page:'"+cid+"' Property:"+this.name()+" Value:"+currentParameter);
						}
	    				return currentParameter;
		    		}
		    		
		    		int idx = line.trim().indexOf("]");
		    	    String head = line.trim().substring(0, idx);
		    	    String tail = line.trim().substring(idx + 1);
		    		currentViewName = head.substring(1, head.length());
		    		currentParameter = tail;
		    	} else if (currentViewName != null){
		    		if(currentParameter == null) {
		    			currentParameter = line.trim();
		    		} else {
		    			currentParameter = currentParameter+","+line.trim();
		    		}
		    	}
		    }
		    
    		if(cid.equals(currentViewName)) {
    			if(printedOverrideMessage.get(cid) == null) {
    				 printedOverrideMessage.put(cid, true);
					System.out.println("Override value is being used for Page:'"+cid+"' Property:"+this.name()+" Value:"+currentParameter);
				}
				return currentParameter;
    		}
    		if(currentViewName != null) {
    			String propertyFromPropertiesFile = cleanProperty(getPropertyFromPropertiesFile());
    			if (propertyFromPropertiesFile == null){
    				propertyFromPropertiesFile = "";
    			}
				System.out.println("Other Pages configured... Using property from projectDefault.properties:"+propertyFromPropertiesFile);
    			return propertyFromPropertiesFile;
    		}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		return property;	
	}
}
