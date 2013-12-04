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
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.VisibilityHelper;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import at.molindo.wicketutils.utils.IMockRequestCallback.MockRequest;

public class MockUtils {

	private MockUtils() {
	}

	/**
	 * unset request and session to force mocking
	 */
	public static <V> V withNewRequest(WebApplication webApplication, IMockRequestCallback<V> callback) {
		ThreadContext oldContext = ThreadContext.detach();
		try {
			return withRequest(webApplication, callback);
		} finally {
			ThreadContext.restore(oldContext);
		}
	}

	public static <V> V withRequest(String applicationKey, IMockRequestCallback<V> callback) {
		return withRequest((WebApplication) Application.get(applicationKey), callback);
	}

	/**
	 * reuse an existing session if possible
	 */
	public static <V> V withRequest(WebApplication webApplication, IMockRequestCallback<V> callback) {
		Session oldSession = ThreadContext.exists() ? ThreadContext.getSession() : null;
		ThreadContext oldContext = ThreadContext.detach();

		try {
			ThreadContext.setApplication(webApplication);
			ThreadContext.setSession(oldSession);

			// mock http session
			ServletContext context = webApplication.getServletContext();
			MockHttpSession httpSession = new MockHttpSession(context);

			// mock servlet request
			MockServletRequest servletRequest = new MockServletRequest(webApplication, httpSession, context);
			callback.configure(new MockRequest(servletRequest));
			servletRequest.setDefaultHeaders();

			// mock response
			MockHttpServletResponse servletResponse = new MockHttpServletResponse(servletRequest);

			// mock web request
			final WebRequest request = VisibilityHelper.newWebRequest(webApplication, servletRequest, "/");

			// mock web response
			final WebResponse response = VisibilityHelper.newWebResponse(webApplication, request, servletResponse);

			// create
			ThreadContext.setRequestCycle(webApplication.createRequestCycle(request, response));

			return callback.call();
		} finally {
			Session newSession = ThreadContext.getSession();
			ThreadContext.restore(oldContext);
			if (oldSession == null && newSession != null && !newSession.isTemporary()) {
				// reuse session if a new one was created
				ThreadContext.setSession(newSession);
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
					}

					@Override
					public void destroy(WicketFilter filter) {
						// noop
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
