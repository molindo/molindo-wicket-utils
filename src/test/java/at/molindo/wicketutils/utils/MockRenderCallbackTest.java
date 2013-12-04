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

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.junit.Test;

public class MockRenderCallbackTest {

	@Test
	public void render() {
		DummyApplication testApp = new DummyApplication();
		try {
			testApp.getMarkupSettings().setStripWicketTags(true);
			testApp.getMarkupSettings().setStripComments(true);

			String output = MockUtils.withNewRequest(testApp, new MockRenderCallback() {

				@Override
				public void configure(MockRequest request) {
				}

				@Override
				protected Component newComponent(String id) {
					return new Label(id, "Hello World");
				}

			});

			assertEquals("Hello World", output);
		} finally {
			testApp.close();
		}
	}

	@Test(expected = WicketRuntimeException.class)
	public void renderStateful() {
		DummyApplication testApp = new DummyApplication();
		try {
			testApp.getMarkupSettings().setStripWicketTags(true);
			testApp.getMarkupSettings().setStripComments(true);

			// throw WicketRuntimeException
			MockUtils.withNewRequest(testApp, new MockRenderCallback() {

				@Override
				public void configure(MockRequest request) {
				}

				@Override
				protected Component newComponent(String id) {
					return new Label(id, "Hello World").add(new AjaxEventBehavior("on click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onEvent(AjaxRequestTarget target) {
						}
					});
				}

			});
		} finally {
			testApp.close();
		}
	}
}
