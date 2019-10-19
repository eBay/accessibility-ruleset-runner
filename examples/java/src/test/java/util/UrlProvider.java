//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Aravind Kannan, Scott Izu
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

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.DataProvider;

public class UrlProvider {

  /**
   * Spawns n threads where n= # of urls specified in -DURLS_TO_TEST flag while
   * running the tests.
   */
  @DataProvider(parallel = true, name = "urlsToTest")
  public static Object[][] getMonitorUrls() {
	System.out.println("Using UrlProvider as DataProvider...");
    String urlsToTest = ARRProperties.URLS_TO_TEST.getPropertyValue();
    System.out.println("URLS_TO_TEST:"+urlsToTest);

	List<String> urlList = Arrays.asList(urlsToTest.split(","));
	System.out.println(urlList.size() + " Url(s) are being evaluated for accessibility issues:");

    String[] arrayOfUrls = urlsToTest.split(",");
    Object[][] urlArray = new Object[arrayOfUrls.length][2];
    int urlArrayRow = 0;
    for (String url : arrayOfUrls) {
      String viewName = "NA";
      if (url.contains("[") && url.contains("]")) {
        String pattern = "\\[(.*?)\\]";
        viewName = url.substring(url.indexOf("["), url.indexOf("]") + "]".length()); // Grab in between []
        viewName = viewName.replace("[", "").replace("]", ""); // remove brackets
        url = url.replaceAll(pattern, "").trim();
      }
      System.out.println(viewName + " " + url);
      
      // BUG_FIXED 1004: Some teams were not using http:// and this causes url exception to be thrown.
	    if(url.startsWith("www")) {
	    	url = "http://" + url;
	    }
      
      
      urlArray[urlArrayRow][0] = url;
      urlArray[urlArrayRow][1] = viewName;
      urlArrayRow++;
    }
    return urlArray;
  }

}
