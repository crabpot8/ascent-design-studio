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

package org.ascent.configurator.conf;

import java.util.HashMap;
import java.util.Map;

public class DefineHandler implements ConfigDirectiveHandler {

	public class InvalidVariableDeclarationException extends RuntimeException {
		public InvalidVariableDeclarationException(String msg) {
			super(msg);
		}
	}

	public void handle(RefreshProblem problem, String context,
			String directive, String argstr) {
		String[] parts = argstr.split("=");
		if (parts.length != 2)
			throw new InvalidVariableDeclarationException(
					"A variable declaration must use the form \"VAR = VALUE\" and the declaration \""
							+ argstr
							+ "\" in context \""
							+ context
							+ "\" does not.");
		String var = parts[0];
		String val = parts[1];

		///int ival = ParsingUtil.toInt(val);

		Map<Object, Object> defines = problem.getSourceVariableValuesTable()
				.get(context);
		if (defines == null) {
			defines = new HashMap<Object, Object>();
			problem.getSourceVariableValuesTable().put(context, defines);
		}
		defines.put(var.trim(), val);
	}

}
