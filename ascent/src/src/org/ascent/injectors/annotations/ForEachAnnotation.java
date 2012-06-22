 /**************************************************************************
 * Copyright 2008 Jules White                                              *
 *                                                                         *
 * Licensed under the Apache License, Version 2.0 (the "License");         *
 * you may not use this file except in compliance with the License.        *
 * You may obtain a copy of the License at                                 *
 *                                                                         *
 * http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                         *
 * Unless required by applicable law or agreed to in writing, software     *
 * distributed under the License is distributed on an "AS IS" BASIS,       *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.*
 * See the License for the specific language governing permissions and     *
 * limitations under the License.                                          *
 **************************************************************************/

package org.ascent.injectors.annotations;

import java.util.List;
import java.util.Map;

import org.ascent.injectors.TemplateInjector;


public class ForEachAnnotation extends AbstractAnnotation {

	public static final String TYPE = "for-each";

	public static final String LIST_OPTION = "list";

	public static final String AUTO_TRIM_COMMAS_OPTION = "trim-commas";

	private boolean autoTrimCommas_ = true;

	public ForEachAnnotation() {
		super(TYPE);
		addRequiredOption("list");
	}

	public boolean getAutoTrimCommas() {
		return autoTrimCommas_;
	}

	public void setAutoTrimCommas(boolean autoTrimCommas) {
		autoTrimCommas_ = autoTrimCommas;
	}

	@Override
	protected String handleImpl(String template, Map options, Map bindings, Map<String,Map> global) {
		String listname = (String) options.get(LIST_OPTION);
		List list = (List) bindings.get(listname);
		String result = "";

		TemplateInjector injector = getTemplateInjector();
		for (Object o : list) {
			result += injector.inject(template, o);
		}

		boolean trim = autoTrimCommas_;
		if (options.get(AUTO_TRIM_COMMAS_OPTION) != null) {
			trim = Boolean.parseBoolean(""
					+ options.get(AUTO_TRIM_COMMAS_OPTION));
		}

		if (trim && result.trim().endsWith(",")) {
			result = result.trim();
			result = result.substring(0, result.length()-1);
		}

		return result;
	}

}
