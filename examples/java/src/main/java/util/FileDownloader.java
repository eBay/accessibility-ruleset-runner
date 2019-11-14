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

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class FileDownloader {
	private static Map<String, String> locationToFile = new HashMap<String, String>();

	public static String downloadJS(String location) {
		String downloaded = locationToFile.get(location);
		if(downloaded != null) {
			return downloaded;
		}
		try {
			HttpGet httpGet = new HttpGet(location);
			CloseableHttpResponse response = new DefaultHttpClient().execute(httpGet);
			downloaded = EntityUtils.toString(response.getEntity());
			locationToFile.put(location, downloaded);
			response.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return downloaded;
	}
}
