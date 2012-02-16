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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Test;

public class MockUtilsTest {

	@Test
	public void withSession() throws Exception {
		WebApplication testApp = newTestApp();

		assertFalse(Application.exists());
		assertFalse(Session.exists());
		assertFalse(RequestCycle.get() != null);

		String stringResource = MockUtils.withRequest(testApp, new MockRequestCallback<String>() {

			@Override
			public String call() {
				// some basic testing
				assertTrue(Application.exists());
				assertTrue(Session.exists());
				assertTrue(RequestCycle.get() != null);

				return new StringResourceModel("someResource", null, "default value").getString();
			}

		});
		assertEquals("default value", stringResource);

		String url = MockUtils.withRequest(testApp, new MockRequestCallback<String>() {

			@Override
			public String call() {
				return RequestCycle.get().urlFor(WebPage.class, null).toString();
			}

		});
		assertEquals("./", url);

		Locale locale = MockUtils.withRequest(testApp, new IMockRequestCallback<Locale>() {

			@Override
			public void configure(MockRequest request) {
				request.setLocale(Locale.GERMAN);
			}

			@Override
			public Locale call() {
				return Session.get().getLocale();
			}

		});
		assertEquals(Locale.GERMAN, locale);

		assertFalse(Application.exists());
		assertFalse(Session.exists());
		assertFalse(RequestCycle.get() != null);
	}

	public WebApplication newTestApp() {
		WebApplication testApp = new TestApp();
		testApp.setWicketFilter(MockUtils.newMockFilter(testApp));
		return testApp;
	}

	@Test
	public void render() {
		WebApplication testApp = newTestApp();
		testApp.getMarkupSettings().setStripWicketTags(true);

		String output = MockUtils.withRequestAndNewSession(testApp, new MockRenderCallback() {

			@Override
			public void configure(MockRequest request) {
			}

			@Override
			protected Component newComponent(String id) {
				return new Label(id, "Hello World");
			}

		});

		assertEquals("Hello World", output);
	}

	public static class TestApp extends WebApplication {

		@Override
		public Class<? extends Page> getHomePage() {
			return WebPage.class;
		}

	}
}
