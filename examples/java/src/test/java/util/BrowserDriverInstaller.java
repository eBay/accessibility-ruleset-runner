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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;

/**
 * This class can dynamically detect the driver needed and download it from a Server
 * Required: Need to setup a server with the various browser drivers located there
 * This class will download it to the active file directory
 *
 */
public class BrowserDriverInstaller {
	public static String installRequiredExeIfNeeded(String browser) {
	      String executableFilePrefix = null;
	      if(browser.equals("CHROME")) {
	    	  executableFilePrefix = "chromedriver";
	      } else if (browser.equals("FF")) {
	          executableFilePrefix = "geckodriver";
	      } else if (browser.equals("IE")) {
	          executableFilePrefix = "IEDriverServer";
	      }
	      
	      String osNameLowerCase = System.getProperty("os.name").toLowerCase();
	      String osNameCAPS = null;
	      if(osNameLowerCase.contains("win")) {
	    	  osNameCAPS = "WINDOWS";
	      } else if (osNameLowerCase.contains("mac")) {
	    	  osNameCAPS = "MAC";
	      } else if (osNameLowerCase.contains("nux")) {
	    	  osNameCAPS = "LINUX";
	      }
	      
	      String sunArchDataModel = System.getProperty("sun.arch.data.model");;
	      System.out.println("BrowserDriverInstaller installRequiredExeIfNeeded browser:"+browser+" executableFilePrefix:"+executableFilePrefix+" osNameCAPS:"+osNameCAPS+" sunArchDataModel:"+sunArchDataModel);
	      
	      String exeStorageFileName = null;
	      if(browser.equals("CHROME") && osNameLowerCase.contains("win")) {
//	    	  exeStorageFileName = "chromedriverWINDOWS.exe";
	    	  exeStorageFileName = "chromedriverWINDOWS67.exe";
	      } else if(browser.equals("CHROME") && osNameLowerCase.contains("win") && sunArchDataModel.equals("64")) {
	    	  exeStorageFileName = "geckodriverWINDOWS64.exe";
	      } else if(browser.equals("FF") && osNameLowerCase.contains("mac") && sunArchDataModel.equals("64")) {
	    	  System.out.println("Install geckodriverMAC");
	    	  exeStorageFileName = "geckodriverMAC";
	      } else if(browser.equals("CHROME") && osNameLowerCase.contains("mac") && sunArchDataModel.equals("64")) {
	    	  System.out.println("Install chromedriverMAC");
	    	  exeStorageFileName = "chromedriverMAC";
	      } else {
	    	System.out.println("ERROR: BrowserDriverInstaller unknown driver");
	    	return null;
	      }
	      
	      String executablePath = new File(".").getAbsolutePath().replace(".", "") + exeStorageFileName;
	      System.out.println("BrowserDriverInstaller installRequiredExeIfNeeded exeStorageFileName:"+exeStorageFileName+" executablePath:"+executablePath);
	      
	      File executable = new File(executablePath);
	      if (!executable.exists()) {

	        System.out.println("BrowserDriverInstaller installing");
		    try {
			  URL driverURL = new URL("https://server.com/" + exeStorageFileName);
		      FileUtils.copyURLToFile(driverURL, executable);
		        if(executable.exists()) {
		            executable.setExecutable(true);
		          }
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	      }
	      
	      return executablePath;
	  }
}
