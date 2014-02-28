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

import java.util.Map;

import org.apache.wicket.util.template.PackageTextTemplate;

import at.molindo.utils.io.StreamUtils;

public class TemplateUtils {

	private TemplateUtils() {
	}

	public static String stringFromFileSuffix(Class<?> scope, String suffix) {
		return string(scope, scope.getSimpleName() + suffix);
	}

	public static String string(Class<?> clazz, String fileName) {
		return string(new PackageTextTemplate(clazz, fileName));

	}

	public static String string(Class<?> clazz, String fileName, Map<String, ?> variables) {
		return string(new PackageTextTemplate(clazz, fileName), variables);
	}

	public static String string(PackageTextTemplate template) {
		try {
			return template.asString();
		} finally {
			StreamUtils.close(template);
		}
	}

	public static String string(PackageTextTemplate template, Map<String, ?> variables) {
		try {
			return template.asString(variables);
		} finally {
			StreamUtils.close(template);
		}
	}

}
