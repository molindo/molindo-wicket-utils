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

package at.molindo.wicketutils.utils;

import java.util.Locale;

import org.apache.wicket.protocol.http.MockHttpServletRequest;

public interface IMockRequestCallback<V> {

	void configure(MockRequest request);

	V call();

	public static class MockRequest {
		private final MockHttpServletRequest _servletRequest;

		MockRequest(MockHttpServletRequest servletRequest) {
			if (servletRequest == null) {
				throw new NullPointerException("servletRequest");
			}
			_servletRequest = servletRequest;
		}

		public final MockHttpServletRequest getServletRequest() {
			return _servletRequest;
		}

		public MockRequest setLocale(Locale l) {
			String lang = l.getLanguage();
			String country = l.getCountry();

			String accept;

			if (country != null) {
				accept = lang.toLowerCase() + "-" + country.toLowerCase() + "," + lang.toLowerCase() + ";q=0.5";
			} else {
				accept = lang.toLowerCase();
			}

			getServletRequest().addHeader("Accept-Language", accept);
			return this;
		}
	}
}