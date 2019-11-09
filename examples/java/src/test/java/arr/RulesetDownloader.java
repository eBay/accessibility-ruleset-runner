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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class RulesetDownloader {
	private static final String CUSTOM_RULESET_LOCATION = "https://raw.githubusercontent.com/eBay/accessibility-ruleset-runner/master/rulesets/custom.ruleset.1.1.33.js";
	private static final String AXE_RULESET_LOCATION = "https://raw.githubusercontent.com/eBay/accessibility-ruleset-runner/master/rulesets/aXe.ruleset.2.3.1.js";
	private static String customRulesetJS = null;
	private static String aXeRulesetJS = null;

	public static String getCustomRulesetJS() {
		if(customRulesetJS == null) {
			customRulesetJS = downloadJS(CUSTOM_RULESET_LOCATION);
		}
		return customRulesetJS;
	}

	public static String getAXERulesetJS() {
		if(aXeRulesetJS == null) {
			aXeRulesetJS = downloadJS(AXE_RULESET_LOCATION);
		}
		return aXeRulesetJS;
	}

	private static String downloadJS(String location) {
		String downloaded = null;
		try {
			HttpGet httpGet = new HttpGet(location);
			CloseableHttpResponse response = new DefaultHttpClient().execute(httpGet);
			downloaded = EntityUtils.toString(response.getEntity());
			response.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return downloaded;
	}
}
