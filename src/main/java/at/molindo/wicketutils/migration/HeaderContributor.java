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

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.IHeaderContributor;

/**
 * This class is meant for easy migration only. Don't use it for new classes
 */
public class HeaderContributor extends Behavior {
	private static final long serialVersionUID = 1L;

	private final IHeaderContributor _headerContributor;

	public HeaderContributor() {
		this(null);
	}

	/**
	 * overwrite {@link HeaderContributor} directly instead of
	 * {@link IHeaderContributor}
	 */
	@Deprecated
	public HeaderContributor(IHeaderContributor headerContributor) {
		_headerContributor = headerContributor;
	}

	@Override
	public final void renderHead(Component component, IHeaderResponse response) {
		renderHead(response);
	}

	public void renderHead(IHeaderResponse response) {
		if (_headerContributor != null) {
			_headerContributor.renderHead(response);
		}
	}

}
