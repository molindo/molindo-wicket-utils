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
package at.molindo.wicketutils.migration;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class JavaScriptPackageResource {

	JavaScriptPackageResource() {
	}

	public static HeaderContributor getHeaderContribution(final String location) {
		return new HeaderContributor() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(IHeaderResponse response) {
				response.render(JavaScriptHeaderItem.forUrl(location));
			}
		};
	}

	public static HeaderContributor getHeaderContribution(final Class<?> scope, final String name) {
		return getHeaderContribution(new PackageResourceReference(scope, name));
	}

	public static HeaderContributor getHeaderContribution(final ResourceReference ref) {
		return new HeaderContributor() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(IHeaderResponse response) {
				response.render(JavaScriptHeaderItem.forReference(ref));
			}
		};
	}

}
