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
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public final class JavaScriptResourceBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	private final JavaScriptResourceReference _reference;

	public JavaScriptResourceBehavior(final Class<?> scope) {
		if (scope == null) {
			throw new NullPointerException("scope");
		}
		_reference = WicketUtils.js(scope);
	}

	public JavaScriptResourceBehavior(final Class<?> scope, final String name) {
		if (scope == null) {
			throw new NullPointerException("scope");
		}
		if (name == null) {
			throw new NullPointerException("name");
		}
		_reference = WicketUtils.js(scope, name);
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response) {
		response.render(JavaScriptHeaderItem.forReference(_reference));
	}

	public JavaScriptResourceReference getResourceReference() {
		return _reference;
	}

}
