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

import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.MockHttpServletRequest;
import org.apache.wicket.protocol.http.MockHttpServletResponse;
import org.apache.wicket.protocol.http.MockHttpSession;
import org.apache.wicket.protocol.http.MockServletContext;
import org.apache.wicket.protocol.http.VisibilityHelper;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.WicketFilter;

import at.molindo.wicketutils.utils.IMockRequestCallback.MockRequest;

public class MockUtils {

	private MockUtils() {
	}

	public static <V> V withRequest(String applicationKey, IMockRequestCallback<V> callback) {
		return withRequest((WebApplication) Application.get(applicationKey), callback);
	}

	public static <V> V withRequestAndNewSession(WebApplication webApplication, IMockRequestCallback<V> callback) {
		Session oldSession = Session.exists() ? Session.get() : null;
		RequestCycle oldRequestCycle = RequestCycle.get();
		try {
			if (oldSession != null) {
				Session.unset();

			}
			return withRequest(webApplication, callback);
		} finally {
			if (oldSession != null) {
				Session.set(oldSession);
			}
			if (oldRequestCycle != null) {
				org.apache.wicket.VisibilityHelper.set(oldRequestCycle);
			}
		}
	}

	/**
	 * reuse an existing session if possible
	 */
	public static <V> V withRequest(WebApplication webApplication, IMockRequestCallback<V> callback) {
		Application prevApplication = null;
		final boolean setupSession = !Session.exists();
		WebRequestCycle requestCycle = null;

		try {
			if (setupSession) {
				if (Application.exists()) {
					prevApplication = Application.get();
				}
				Application.set(webApplication);

				ServletContext context = webApplication.getServletContext();
				MockHttpSession httpSession = new MockHttpSession(context);

				MockServletRequest servletRequest = new MockServletRequest(webApplication, httpSession, context);
				callback.configure(new MockRequest(servletRequest));
				servletRequest.setDefaultHeaders();

				MockHttpServletResponse servletResponse = new MockHttpServletResponse(servletRequest);

				final WebRequest request = VisibilityHelper.newWebRequest(webApplication, servletRequest);
				final WebResponse response = VisibilityHelper.newWebResponse(webApplication, servletResponse);

				requestCycle = (WebRequestCycle) webApplication.newRequestCycle(request, response);
				Session.findOrCreate(request, response);
			}
			return callback.call();
		} finally {
			if (setupSession) {
				if (requestCycle != null) {
					VisibilityHelper.unset(requestCycle);
				}
				Session.unset();

				if (prevApplication != null) {
					Application.set(prevApplication);
				} else {
					Application.unset();
				}
			}
		}
	}

	private static final class MockServletRequest extends MockHttpServletRequest {
		private boolean _initialized = false;

		private MockServletRequest(Application application, HttpSession session, ServletContext context) {
			super(application, session, context);
			_initialized = true;
		}

		@Override
		public void addHeader(String name, String value) {
			if (_initialized) {
				super.addHeader(name, value);
			}
		}

		private void setDefaultHeaders() {
			if (getHeader("Accept") == null) {
				addHeader("Accept", "text/xml,application/xml,application/xhtml+xml,"
						+ "text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
			}
			if (getHeader("Accept-Charset") == null) {
				addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			}

			if (getHeader("Accept-Language") == null) {
				Locale l = Locale.getDefault();
				addHeader("Accept-Language", l.getLanguage().toLowerCase() + "-" + l.getCountry().toLowerCase() + ","
						+ l.getLanguage().toLowerCase() + ";q=0.5");
			}

			if (getHeader("User-Agent") == null) {
				addHeader("User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7) Gecko/20040707 Firefox/0.9.2");
			}
		}
	}

	public static WicketFilter newMockFilter(final WebApplication application) {
		final MockServletContext context = new MockServletContext(application, "/");
		final WicketFilter filter = new WicketFilter() {
			@Override
			protected IWebApplicationFactory getApplicationFactory() {
				return new IWebApplicationFactory() {
					@Override
					public WebApplication createApplication(WicketFilter filter) {
						return application;
					};
				};
			}
		};

		try {
			filter.init(new FilterConfig() {

				@Override
				public ServletContext getServletContext() {
					return context;
				}

				@Override
				public Enumeration<?> getInitParameterNames() {
					return null;
				}

				@Override
				public String getInitParameter(String name) {
					return null;
				}

				@Override
				public String getFilterName() {
					return "WicketMockServlet";
				}
			});
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}

		return filter;
	}
}
