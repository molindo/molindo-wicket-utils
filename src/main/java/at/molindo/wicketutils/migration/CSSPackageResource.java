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

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class CSSPackageResource {

	private CSSPackageResource() {
	}

	public static HeaderContributor getHeaderContribution(final Class<?> scope, final String name, final String media) {
		return getHeaderContribution(new CssResourceReference(scope, name), media);

	}

	public static HeaderContributor getHeaderContribution(Class<?> scope, String name) {
		return getHeaderContribution(new CssResourceReference(scope, name));
	}

	private static HeaderContributor getHeaderContribution(final ResourceReference reference) {
		return new HeaderContributor() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(IHeaderResponse response) {
				response.renderCSSReference(reference);
			}

		};
	}

	public static HeaderContributor getHeaderContribution(final ResourceReference reference, final String media) {
		return new HeaderContributor() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(IHeaderResponse response) {
				response.renderCSSReference(reference, media);
			}

		};
	}

}
