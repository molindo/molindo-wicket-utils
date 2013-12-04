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

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;

public abstract class MockRenderCallback implements IMockRequestCallback<String> {

	private boolean _failOnStatefulPages = true;

	@Override
	public final String call() {
		// add component to mock page
		MockRenderPage page = new MockRenderPage();
		Component component = newComponent("mock");
		page.add(component);

		// render page
		page.renderPage();

		if (isFailOnStatefulPages() && !page.isPageStateless()) {
			throw new WicketRuntimeException("no stateful components allowed");
		}

		// close response and get output
		WicketUtils.getWebResponse().close();
		return ((MockHttpServletResponse) WicketUtils.getServletWebResponse().getContainerResponse()).getDocument();
	}

	protected abstract Component newComponent(String id);

	public boolean isFailOnStatefulPages() {
		return _failOnStatefulPages;
	}

	public MockRenderCallback setFailOnStatefulPages(boolean failOnStatefulPages) {
		_failOnStatefulPages = failOnStatefulPages;
		return this;
	}

}
