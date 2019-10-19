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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HTMLGenerator {
	public static final String BACKSLASH = "\\"; // prints out '\' character, backslash
	public static final String FORWARDSLASH = "/"; // prints out '/' character, slash
	public static final String ASTERISK = "*"; // prints out '*' character, used to match
	public static final String REPLACE_BACKSLASH = BACKSLASH + BACKSLASH; // prints out "\\", used to replace a single backslash
	public static final String REPLACE_FORWARDSLASH = BACKSLASH + FORWARDSLASH; // prints out "\/", used to replace a single slash
	public static final String REPLACE_ASTERISK = BACKSLASH + ASTERISK; // prints out "\*" characters, used to replace a single asterisk
//	public static final String EMPTY_STRING = "";
//	public static final String SPACE = " ";
//	public static final String TAB = "\t";
//	public static final String CARRIAGE_RETURN = "\r";
//	public static final String NEWLINE = "\n";
//	public static final String WINDOWS_RETURN = CARRIAGE_RETURN + NEWLINE;
	public static final String SINGLEQUOTE = "'";
//	public static final String DOT = ".";
//	public static final String COLON = ":";
//	public static final String COMMA = ",";
//	public static final String LEFT_BRACKET = "[";
//	public static final String RIGHT_BRACKET = "]";
	public static String addSingleQuotes(String string) {
		return SINGLEQUOTE + string + SINGLEQUOTE;
	}
	
	public static String getPageTemplate(Class clazz, String fileLocation) {
		//BUG_FIXED 1005
		//Bug fix for case where users were not able to use API since the html template
		//The bug was due to html template not available in invoking project's path.
		//Had to use getResourceAsStream instead of getResource
		String value = "";
		try {
			InputStream isCss = clazz.getResourceAsStream(fileLocation);
			BufferedReader br = new BufferedReader(new InputStreamReader(isCss));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				value = value + sCurrentLine + '\n';
				//System.out.println(sCurrentLine);
			}
			br.close();
			isCss.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return value;
	}

	public static String getInnerTemplate(String htmlString,
			String templateString) {
		String[] split = htmlString.split(templateString);
		// System.out.println("SPLIT:"+split[1]);
		return templateString+split[1]+templateString;
	}

	public static String substituteMarker(String htmlString,
			String templateString, String substitutionString) {
		String splitString = templateString;

		splitString = splitString.replaceAll(REPLACE_FORWARDSLASH,
				REPLACE_BACKSLASH + REPLACE_FORWARDSLASH); // replaces '/' with
															// '\/' characters
		splitString = splitString.replaceAll(REPLACE_ASTERISK, REPLACE_BACKSLASH
				+ REPLACE_ASTERISK); // replaces '*' character with '\*'
									// characters

		// Ready to do the main work
		String[] split = htmlString.split(splitString);
		StringBuilder value = new StringBuilder();
		value.append(split[0]);
		value.append(templateString);
		value.append(substitutionString);
		value.append(templateString);
		value.append(split[2]);
		// System.out.println("value:"+value);
		return value.toString();
	}
	
	public static void writeFile(File outputFile, String value) {
		createParentDirectory(outputFile);
		try {
			FileWriter fw = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(value);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String readFile(File inputFile) {
		String value = "";
		try {
			FileReader fr = new FileReader(inputFile);
			BufferedReader br = new BufferedReader(fr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				value = value + sCurrentLine + '\n';
				//System.out.println(sCurrentLine);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	private static void createParentDirectory(File outputFile) {
		if (!outputFile.exists()) {
			outputFile.getParentFile().mkdirs();
		}
	}
}
