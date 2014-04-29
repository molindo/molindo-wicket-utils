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
package at.molindo.wicketutils.migration.ex;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.RequestHandlerStack.ReplaceHandlerException;

public class AbortException extends ReplaceHandlerException {

	public AbortException() {
		super(null, true);
		throw new WicketRuntimeException("not implemented");
	}

	private static final long serialVersionUID = 1L;

	/**
	 * @see java.lang.Throwable#fillInStackTrace()
	 */
	@Override
	public synchronized Throwable fillInStackTrace() {
		// we do not need a stack trace, so to speed things up just return null
		return null;
	}

}
