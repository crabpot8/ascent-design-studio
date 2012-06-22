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

import org.ascent.expr.BinaryExpression;
import org.ascent.expr.Expression;
import org.ascent.expr.VariableValueExpression;


public class DefineVarHandler implements ConfigDirectiveHandler {

	public class InvalidVariableDeclarationException extends RuntimeException {
		public InvalidVariableDeclarationException(String msg) {
			super(msg);
		}
	}

	public void handle(RefreshProblem problem, String context,
			String directive, String argstr) {
		
		Expression expr = ExpressionParser.getExpression(argstr);
		
		if (expr == null || !(expr instanceof BinaryExpression) || !((BinaryExpression)expr).getOperation().equals("="))
			throw new InvalidVariableDeclarationException(
					"A variable declaration must use the form \"VAR = VALUE\" and the declaration \""
							+ argstr
							+ "\" in context \""
							+ context
							+ "\" does not.");
		
		BinaryExpression bexp = (BinaryExpression)expr;
		Expression valexpr = bexp.getRightHandSide();
		String var = context.trim()+"."+((VariableValueExpression)bexp.getLeftHandSide()).getVariable();

		problem.getContextVariableDefinitions(context).put(var, valexpr);
		
	}

}
