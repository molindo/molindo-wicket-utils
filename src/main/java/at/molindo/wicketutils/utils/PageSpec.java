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

import javax.annotation.Nonnull;

import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class PageSpec {

	public static PageSpec get(final Page page) {

		return new PageSpec(false) {

			@Override
			protected void setResponsePage(RequestCycle rc) {
				rc.setResponsePage(page);
			}

		};
	}

	public static PageSpec get(Class<? extends Page> pageClass) {
		return get(pageClass, null);
	}

	public static PageSpec get(final Class<? extends Page> pageClass, final PageParameters params) {

		return new PageSpec(true) {

			@Override
			protected void setResponsePage(RequestCycle rc) {
				rc.setResponsePage(pageClass, params);
			}
		};
	}

	private final boolean _bookmarkable;

	private PageSpec(boolean bookmarkable) {
		_bookmarkable = bookmarkable;
	}

	public boolean isBookmarkable() {
		return _bookmarkable;
	}

	public void setAsResponsePage(boolean redirect) {

		RequestCycle rc = RequestCycle.get();
		if (rc == null) {
			throw new WicketRuntimeException("no request cycle available");
		}
		setAsResponsePage(rc, redirect);
	}

	public void setAsResponsePage(@Nonnull RequestCycle rc, boolean redirect) {
		if (rc == null) {
			throw new NullPointerException("rc");
		}
		setResponsePage(rc);
	}

	protected abstract void setResponsePage(@Nonnull RequestCycle rc);
}
