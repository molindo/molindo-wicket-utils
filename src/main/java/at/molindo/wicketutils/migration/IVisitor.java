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
import org.apache.wicket.util.visit.IVisit;

public abstract class IVisitor<T extends Component> implements org.apache.wicket.util.visit.IVisitor<T, Object> {
	/**
	 * Value to return to continue a traversal.
	 */
	public static final Object CONTINUE_TRAVERSAL = null;

	/**
	 * A generic value to return to continue a traversal, but if the component
	 * is a container, don't visit its children.
	 */
	public static final Object CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER = new Object();

	/**
	 * A generic value to return to stop a traversal.
	 */
	public static final Object STOP_TRAVERSAL = new Object();

	/**
	 * Called at each component in a traversal.
	 * 
	 * @param component
	 *            The component
	 * @return CONTINUE_TRAVERSAL (null) if the traversal should continue, or a
	 *         non-null return value for the traversal method if it should stop.
	 *         If no return value is useful, the generic non-null value
	 *         STOP_TRAVERSAL can be used.
	 */
	public abstract Object component(T component);

	@Override
	public void component(T component, IVisit<Object> visit) {
		Object o = component(component);

		if (o == CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER) {
			visit.dontGoDeeper();
		} else if (o != CONTINUE_TRAVERSAL) {
			visit.stop(o);
		}
	}

}