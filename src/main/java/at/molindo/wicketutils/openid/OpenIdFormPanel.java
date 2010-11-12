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

package at.molindo.wicketutils.openid;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.UrlValidator;

public class OpenIdFormPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public OpenIdFormPanel(String id) {
		super(id);

		add(new OpenIdForm("form"));
	}

	public class OpenIdForm extends Form<String> {

		private static final long serialVersionUID = 1L;
		private final Model<String> _openIdModel;

		public OpenIdForm(String id) {
			super(id);
			setOutputMarkupId(true);

			add(new AjaxFormSubmitBehavior(this, "submit") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					OpenIdForm.this.onSubmit();
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					OpenIdForm.this.onError();
					target.addComponent(OpenIdForm.this);
				}
			});

			TextField<String> field = new TextField<String>("openid", _openIdModel = new Model<String>());
			field.setRequired(true);
			field.setLabel(new ResourceModel("openId", "Open ID"));
			field.add(new UrlValidator(new String[] { "http", "https" }));

			add(new SimpleFormComponentLabel("label", field));

			add(field);

		}

		@Override
		protected void onError() {
		}

		@Override
		protected void onSubmit() {
			OpenIdSession.get().redirect(_openIdModel.getObject());
		}
	}

}
