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

import java.util.StringTokenizer;

import org.ascent.expr.BinaryExpression;
import org.ascent.expr.ConstantIntegerExpression;
import org.ascent.expr.Expression;
import org.ascent.expr.SourceVariableSumExpression;
import org.ascent.expr.SourceVariableValueExpression;
import org.ascent.expr.TargetVariableSumExpression;
import org.ascent.expr.TargetVariableValueExpression;
import org.ascent.util.ParsingUtil;

public abstract class AbstractExpressionCreatorHandler implements
		ConfigDirectiveHandler {

	public static final String TARGET_PREFIX = "Target.";

	public static final String SOURCE_PREFIX = "Source.";

	public static final String SUM_POSTFIX = ".Sum";

	private String OPS = "=<>-+!";

	public void handle(RefreshProblem problem, String context,
			String directive, String argstr) {
		StringTokenizer tk = new StringTokenizer(argstr.trim(), OPS, true);
		String[] rfeatures = new String[tk.countTokens()];
		int i = 0;
		while (tk.hasMoreTokens()) {
			rfeatures[i] = tk.nextToken().trim();
			i++;
		}

		String op = "";
		Expression curr = null;
		for (String val : rfeatures) {
			if (OPS.indexOf(val) > -1) {
				op += val;
			} else {
				Expression expr = getExpression(val);
				if (curr != null) {
					curr = new BinaryExpression(curr, expr, op);
				} else {
					curr = expr;
				}
			}
		}

		handle(problem, context, directive, argstr, curr);
	}

	public abstract void handle(RefreshProblem problem, String context,
			String directive, String argstr, Expression expr);

	public Expression getExpression(String expr) {
		Expression cexpr = null;

		if (expr.startsWith(TARGET_PREFIX)) {
			if (expr.endsWith(SUM_POSTFIX)) {
				String var = expr.substring(TARGET_PREFIX.length(), expr
						.length()
						- SUM_POSTFIX.length());
				cexpr = new TargetVariableSumExpression(var);
			} else {
				String var = expr.substring(TARGET_PREFIX.length());
				cexpr = new TargetVariableValueExpression(var);
			}
		} else if (expr.startsWith(SOURCE_PREFIX)) {
			
			if (expr.endsWith(SUM_POSTFIX)) {
				String var = expr.substring(SOURCE_PREFIX.length(), expr
						.length()
						- SUM_POSTFIX.length());
				cexpr = new SourceVariableSumExpression(var);
			} else {
				String var = expr;
				cexpr = new SourceVariableValueExpression(var
						.substring(SOURCE_PREFIX.length()));
			}
		} else {

			Integer i = ParsingUtil.toInt(expr.trim());
			cexpr = new ConstantIntegerExpression(i);

		}
		return cexpr;
	}

}
