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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.INamedParameters.NamedPair;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.lang.Classes;

import at.molindo.thirdparty.org.apache.http.client.utils.URIUtils;

public final class WicketUtils {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WicketUtils.class);

	private WicketUtils() {
		// no instance
	}

	public static Class<? extends IRequestablePage> getBookmarkablePage(final RequestCycle cycle) {
		if (cycle == null) {
			return null;
		}

		final IRequestHandler handler = cycle.getActiveRequestHandler();
		if (handler instanceof BookmarkablePageRequestHandler) {
			return ((BookmarkablePageRequestHandler) handler).getPageClass();
		}

		return null;
	}

	public static boolean isBookmarkableRequest(final URL url) {
		return getBookmarkableRequestHandler(url) != null;
	}

	public static Class<? extends IRequestablePage> getBookmarkablePage(final URL url) {
		final BookmarkablePageRequestHandler handler = getBookmarkableRequestHandler(url);
		return handler == null ? null : handler.getPageClass();
	}

	/**
	 * @deprecated use {@link #getBookmarkableRequestHandler(URL)}
	 */
	@Deprecated
	public static BookmarkablePageRequestHandler getBookmarkableRequestTarget(final URL url) {
		return getBookmarkableRequestHandler(url);
	}

	public static BookmarkablePageRequestHandler getBookmarkableRequestHandler(final URL url) {
		final IRequestHandler handler = getRequestHandler(url);
		if (handler instanceof BookmarkablePageRequestHandler) {
			return (BookmarkablePageRequestHandler) handler;
		} else {
			return null;
		}
	}

	/**
	 * @deprecated use {@link #getRequestHandler(URL)}
	 */
	@Deprecated
	public static IRequestHandler getRequestTarget(final URL url) {
		return getRequestHandler(url);
	}

	public static IRequestHandler getRequestHandler(final URL url) {
		if (url != null) {
			MockWebRequest request = new MockWebRequest(Url.parse(url.toString()));

			return Application.get().getRootRequestMapper().mapRequest(request);
		} else {
			return null;
		}
	}

	public static AbstractLink getBookmarkableRefererLink(final String id, final IModel<String> labelModel) {
		final String referer = getReferer();
		if (referer == null) {
			return null;
		}
		try {
			if (isBookmarkableRequest(new URL(referer))) {
				return new ExternalLink(id, new Model<String>(referer), labelModel);
			}
		} catch (final MalformedURLException e) {
			log.warn("malformed referer url: " + referer + " (" + e.toString() + ")");
		}
		return null;
	}

	public static String getReferer() {
		return getHttpServletRequest().getHeader("Referer");
	}

	// public static class UrlRequest extends Request {
	// private final ValueMap params = new ValueMap();
	//
	// private final Request realRequest;
	//
	// private final URL url;
	//
	// /**
	// * Construct.
	// *
	// * @param realRequest
	// * @param url
	// */
	// public UrlRequest(final Request realRequest, final URL url) {
	// this.realRequest = realRequest;
	// this.url = url;
	//
	// final String query = url.getQuery();
	// if (query != null) {
	// RequestUtils.decodeParameters(query, params);
	// }
	// }
	//
	// /**
	// * @see org.apache.wicket.Request#getLocale()
	// */
	// @Override
	// public Locale getLocale() {
	// return realRequest.getLocale();
	// }
	//
	// /**
	// * @see org.apache.wicket.Request#getParameter(java.lang.String)
	// */
	// @Override
	// public String getParameter(final String key) {
	// return (String) params.get(key);
	// }
	//
	// /**
	// * @see org.apache.wicket.Request#getParameterMap()
	// */
	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// @Override
	// public Map getParameterMap() {
	// return params;
	// }
	//
	// /**
	// * @see org.apache.wicket.Request#getParameters(java.lang.String)
	// */
	// @Override
	// public String[] getParameters(final String key) {
	// final String param = (String) params.get(key);
	// if (param != null) {
	// return new String[] { param };
	// }
	// return new String[0];
	// }
	//
	// /**
	// * @see org.apache.wicket.Request#getPath()
	// */
	// @Override
	// public String getPath() {
	// String path = url.getPath();
	// if (path.startsWith("/")) {
	// path = path.substring(1);
	// }
	// return path;
	// }
	//
	// @Override
	// public String getRelativePathPrefixToContextRoot() {
	// throw new NotImplementedException();
	// }
	//
	// @Override
	// public String getRelativePathPrefixToWicketHandler() {
	// throw new NotImplementedException();
	// }
	//
	// /**
	// * @see org.apache.wicket.Request#getURL()
	// */
	// @Override
	// public String getURL() {
	// return url.toString();
	// }
	//
	// @Override
	// public String getQueryString() {
	// return realRequest.getQueryString();
	// }
	// }

	public static String getRequested() {
		final HttpServletRequest req = getHttpServletRequest();
		final StringBuilder buf = new StringBuilder();
		buf.append(req.getServletPath());
		final String path = req.getPathInfo();
		if (path != null) {
			buf.append(path);
		}
		final String query = req.getQueryString();
		if (query != null) {
			buf.append("?").append(query);
		}
		return buf.toString();
	}

	/**
	 * @return may return null
	 */
	public static HttpServletRequest getHttpServletRequest() {
		return getHttpServletRequest(getRequest());
	}

	public static HttpServletRequest getHttpServletRequest(Request request) {
		Object cr = request != null ? request.getContainerRequest() : null;
		return cr instanceof HttpServletRequest ? (HttpServletRequest) cr : null;
	}

	/**
	 * @return may return null
	 */
	public static HttpServletResponse getHttpServletResponse() {
		return getHttpServletResponse(getResponse());
	}

	public static HttpServletResponse getHttpServletResponse(Response response) {
		Object cr = response != null ? response.getContainerResponse() : null;
		return cr instanceof HttpServletResponse ? (HttpServletResponse) cr : null;
	}

	/**
	 * @return may return null
	 */
	public static HttpSession getHttpSession() {
		HttpServletRequest r = getHttpServletRequest();
		return r == null ? null : r.getSession();
	}

	public static String getRemoteAddr() {
		return getHttpServletRequest().getRemoteAddr();
	}

	public static String getRequestParameter(final String name) {
		return getHttpServletRequest().getParameter(name);
	}

	/**
	 * @return may return null
	 */
	public static Request getRequest() {
		final RequestCycle rc = RequestCycle.get();
		return rc == null ? null : rc.getRequest();
	}

	/**
	 * @return may return null
	 */
	public static WebRequest getWebRequest() {
		final Request request = getRequest();
		return request instanceof WebRequest ? (WebRequest) request : null;
	}

	/**
	 * @return may return null
	 */
	public static ServletWebRequest getServletWebRequest() {
		final Request request = getRequest();
		return request instanceof ServletWebRequest ? (ServletWebRequest) request : null;
	}

	/**
	 * @deprecated use {@link RequestCycle#get()}
	 */
	@Deprecated
	public static RequestCycle getWebRequestCycle() {
		return RequestCycle.get();
	}

	/**
	 * @return may return null
	 */
	public static Response getResponse() {
		final RequestCycle rc = RequestCycle.get();
		return rc == null ? null : rc.getResponse();
	}

	/**
	 * @return may return null
	 */
	public static WebResponse getWebResponse() {
		final Response response = getResponse();
		return response instanceof WebResponse ? (WebResponse) response : null;
	}

	public static ServletWebResponse getServletWebResponse() {
		final Response response = getResponse();
		return response instanceof ServletWebResponse ? (ServletWebResponse) response : null;
	}

	public static boolean isHttps() {
		return "https".equalsIgnoreCase(getHttpServletRequest().getScheme());
	}

	public static Cookie getCookie(final String name) {
		final Cookie[] cookies = getHttpServletRequest().getCookies();
		if (cookies == null) {
			return null;
		}
		for (final Cookie c : cookies) {
			if (name.equals(c.getName())) {
				return c;
			}
		}
		return null;
	}

	public static void deleteCookie(final String name) {
		final Cookie c = new Cookie(name, "");
		c.setMaxAge(0);
		getHttpServletResponse().addCookie(c);
	}

	public static String getHeader(final String name) {
		final HttpServletRequest r = getHttpServletRequest();
		return r == null ? null : r.getHeader(name);
	}

	public static WebClientInfo getClientInfo() {
		return (WebClientInfo) Session.get().getClientInfo();
	}

	public static String getUserAgent() {
		final WebClientInfo info = getClientInfo();
		return info == null ? null : info.getUserAgent();
	}

	/**
	 * @return true if cookies are disabled, false if unknown
	 */
	public static boolean isCookiesDisabled() {
		final WebClientInfo info = getClientInfo();
		return info == null || info.getProperties() == null ? false : !info.getProperties().isCookiesEnabled();

	}

	public static String getClientInfoString() {
		final WebClientInfo info = getClientInfo();
		return info == null ? null : info.getUserAgent() + " (" + info.getProperties().getRemoteAddress() + ")";
	}

	public static String getRequestContextPath() {
		final HttpServletRequest r = getHttpServletRequest();
		if (r == null) {
			return null;
		}
		try {
			final URL url = new URL(r.getRequestURL().toString());
			final StringBuilder buf = new StringBuilder(100);

			buf.append(url.getProtocol()).append("://").append(url.getHost());
			if (url.getPort() != -1) {
				buf.append(":").append(url.getPort());
			}
			return buf.append(r.getContextPath()).toString();
		} catch (final MalformedURLException e) {
			throw new WicketRuntimeException("client sent an illegal url?", e);
		}

	}

	public static String getHost() {
		final HttpServletRequest r = getHttpServletRequest();
		if (r == null) {
			return null;
		}
		try {
			return new URL(r.getRequestURL().toString()).getHost();
		} catch (final MalformedURLException e) {
			throw new WicketRuntimeException("client sent an illegal url?", e);
		}
	}

	/**
	 * @deprecated bad naming, use {@link #toUrl(Class)} instead
	 */
	@Deprecated
	public static String toAbsolutePath(final Class<? extends Page> pageClass) {
		return toUrl(pageClass, null).toString();
	}

	/**
	 * @deprecated bad naming, use {@link #toUrl(Class, PageParameters)} instead
	 */
	@Deprecated
	public static String toAbsolutePath(final Class<? extends Page> pageClass, final PageParameters parameters) {
		return toUrl(pageClass, parameters).toString();
	}

	public static URL toUrl(final Class<? extends Page> pageClass) {
		return toUrl(pageClass, null);
	}

	public static URL toUrl(final Class<? extends Page> pageClass, final PageParameters params) {
		try {
			String relativePagePath = RequestCycle.get().urlFor(pageClass, params).toString();
			URL requestUrl = new URL(getHttpServletRequest().getRequestURL().toString());
			URI resolved = URIUtils.resolve(requestUrl.toURI(), relativePagePath);
			return resolved.toURL();
		} catch (MalformedURLException e) {
			throw new WicketRuntimeException("failed to create URL", e);
		} catch (URISyntaxException e) {
			throw new WicketRuntimeException("failed to create URL", e);
		}
	}

	public static void performTemporaryRedirect(String targetURL) {
		performRedirect(targetURL, HttpServletResponse.SC_MOVED_TEMPORARILY);
	}

	public static void performPermanentRedirect(String targetURL) {
		performRedirect(targetURL, HttpServletResponse.SC_MOVED_PERMANENTLY);
	}

	public static void performRedirect(final String targetURL, final int statusCode) {
		ThreadContext.getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(targetURL, statusCode));
	}

	public static void performRedirect(final Class<? extends Page> pageClass, final PageParameters parameters,
			final int statusCode) {
		performRedirect(toAbsolutePath(pageClass, parameters), statusCode);
	}

	/**
	 * @return <code>true</code> if header "Wicket-Ajax" is set
	 * @see ServletWebRequest#isAjax()
	 */
	public static boolean isAjax() {
		Request req = getRequest();
		if (req instanceof ServletWebRequest) {
			return ((ServletWebRequest) req).isAjax();
		} else {
			return false;
		}
	}

	public static boolean isDeployment() {
		return RuntimeConfigurationType.DEPLOYMENT.equals(Application.get().getConfigurationType());
	}

	public static LinkedHashMap<String, Object> toMap(PageParameters params) {
		int indexed = params.getIndexedCount();
		List<NamedPair> named = params.getAllNamed();

		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>((indexed + named.size()) * 2);

		for (int i = 0; i < indexed; i++) {
			String index = Integer.toString(i);
			Object value = params.get(i).to(Object.class);

			Object prev = map.put(index, value);
			if (prev != null) {
				map.put(index, merge(prev, value));
			}
		}

		for (NamedPair p : named) {
			map.put(p.getKey(), p.getValue());
		}

		return map;
	}

	private static Object[] merge(Object objectOrObjectArray, Object object) {
		if (objectOrObjectArray.getClass().isArray()) {
			Object[] array = (Object[]) objectOrObjectArray;
			Object[] copy = new Object[array.length + 1];
			System.arraycopy(array, 0, copy, 0, array.length);
			copy[array.length] = object;
			return copy;
		} else {
			return new Object[] { objectOrObjectArray, object };
		}
	}

	/**
	 * replacement for {@link Classes}.resolveClass(String)
	 * 
	 * @param <T>
	 *            class type
	 * @param className
	 *            Class to resolve
	 * @return Resolved class
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> resolveClass(final String className) {
		if (className == null) {
			return null;
		}
		try {
			if (Application.exists()) {
				return (Class<T>) Application.get().getApplicationSettings().getClassResolver().resolveClass(className);
			}
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			log.warn("Could not resolve class: " + className);
			return null;
		}
	}

	/**
	 * replacement for {@link RequestUtils}.toAbsolutePath(String)
	 * 
	 * Calculates absolute path to url relative to another absolute url.
	 * 
	 * @param relativePagePath
	 *            path, relative to requestPath
	 * @return absolute path for given url
	 */
	public final static String toAbsolutePath(final String relativePagePath) {
		return RequestUtils.toAbsolutePath(getHttpServletRequest().getRequestURL().toString(), relativePagePath);
	}

	public static CssResourceReference css(Class<?> scope) {
		return new CssResourceReference(scope, scope.getSimpleName() + ".css");
	}

	public static CssResourceBehavior cssBehavior(final Class<?> scope, final String media) {
		return new CssResourceBehavior(scope, media);
	}

	public static JavaScriptResourceReference js(Class<?> scope) {
		return new JavaScriptResourceReference(scope, scope.getSimpleName() + ".css");
	}

}
