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

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.UrlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum WicketURLDecoder {
	QUERY_INSTANCE(UrlDecoder.QUERY_INSTANCE),

	PATH_INSTANCE(UrlDecoder.PATH_INSTANCE);

	private static final Logger log = LoggerFactory.getLogger(WicketURLDecoder.class);

	private final UrlDecoder _decoder;

	private WicketURLDecoder(UrlDecoder decoder) {
		if (decoder == null) {
			throw new NullPointerException("decoder");
		}
		_decoder = decoder;
	}

	public String decode(String s) {
		Application app = null;

		try {
			app = Application.get();
		} catch (WicketRuntimeException ignored) {
			log.warn("No current Application found - defaulting encoding to UTF-8");
		}
		return decode(s, app == null ? "UTF-8" : app.getRequestCycleSettings().getResponseRequestEncoding());
	}

	private String decode(String s, String enc) {
		return _decoder.decode(s, enc);
	}
}
