/**
 * Copyright 2010 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.wicket.protocol.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * utility to call methods with package visibility in
 * org.apache.wicket.protocol.http
 */
public class VisibilityHelper {

	private VisibilityHelper() {
	}

	/**
	 * @see WebApplication#newWebRequest(HttpServletRequest)
	 */
	public static WebRequest newWebRequest(WebApplication webApplication, HttpServletRequest servletRequest) {
		return webApplication.newWebRequest(servletRequest);
	}

	/**
	 * @see WebApplication#newWebResponse(HttpServletResponse)
	 */
	public static WebResponse newWebResponse(WebApplication webApplication, HttpServletResponse servletResponse) {
		return webApplication.newWebResponse(servletResponse);
	}

	/**
	 * @see WebRequestCycle#unset()
	 */
	public static void unset(WebRequestCycle requestCycle) {
		requestCycle.unset();
	}

}
