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
package at.molindo.wicketutils.dev;

import java.lang.reflect.Field;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainerWithAssociatedMarkup;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * adds an {@link AttributeModifier} to every component with asscociated markup,
 * i.e., objects of any class extending
 * {@link WebMarkupContainerWithAssociatedMarkup}.
 * 
 * @author stf@molindo.at
 */
public final class MarkupSourceAttributeAppender implements IComponentInstantiationListener {

	private final String attribute;

	public MarkupSourceAttributeAppender(String attribute) {

		if (attribute == null) {
			throw new NullPointerException("attribute");
		}
		this.attribute = attribute;
	}

	@Override
	public void onInstantiation(final Component component) {
		if (component instanceof WebMarkupContainerWithAssociatedMarkup) {
			component.add(new AttributeModifier(attribute, true, new MarkupSourceModel(
					(WebMarkupContainerWithAssociatedMarkup) component)));
		}
	}

	private static final class MarkupSourceModel extends LoadableDetachableModel<CharSequence> {

		private static final long serialVersionUID = 1L;

		private final WebMarkupContainerWithAssociatedMarkup component;

		private MarkupSourceModel(WebMarkupContainerWithAssociatedMarkup component) {
			this.component = component;
		}

		@Override
		protected CharSequence load() {
			MarkupStream stream = component.getAssociatedMarkupStream(false);
			if (stream == null || stream.getResource() == null) {
				return AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE;
			}

			String resource = toString(stream.getResource());

			if (component instanceof Fragment) {
				resource += " [wicket:fragment=" + getFragmentId((Fragment) component) + "]";
			}

			return resource;
		}

		private String getFragmentId(Fragment fragment) {
			Field field;
			try {
				field = Fragment.class.getDeclaredField("markupId");
				field.setAccessible(true);
				return (String) field.get(fragment);
			} catch (NoSuchFieldException e) {
				throw new WicketRuntimeException("field 'markupId' of Fragment doesn't exist", e);
			} catch (Exception e) {
				return "<unknown>";
			}
		}

		/**
		 * @param resource
		 *            never null
		 * @return a string describing this resource's location
		 */
		private String toString(IResourceStream resource) {
			return resource.toString();
		}
	}
}