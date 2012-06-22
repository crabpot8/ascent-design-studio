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
import org.ascent.expr.MappedToExpression;
import org.ascent.expr.SourceVariableSumExpression;
import org.ascent.expr.SourceVariableValueExpression;
import org.ascent.expr.TargetVariableSumExpression;
import org.ascent.expr.TargetVariableValueExpression;
import org.ascent.expr.VariableValueExpression;
import org.ascent.util.ParsingUtil;


public class ExpressionParser {

	public static final String TARGET_PREFIX = "Target.";

	public static final String SOURCE_PREFIX = "Source.";

	public static final String SUM_POSTFIX = ".Sum";
	
	public static final String MAPPED_TO_OP = ".MappedTo.";

	private String OPS = "=<>-+!";

	public static Expression getExpression(String expr) {
		StringTokenizer tk = new StringTokenizer(expr, "!=-+*/", true);
		return parseExpression(tk);
	}

	public static Expression parseExpression(StringTokenizer tk) {
		if (tk.countTokens() == 1) {
			return parseTerm(tk);
		} else if (tk.countTokens() == 2) {
			return null;
		} else if(tk.countTokens() > 2){
			Expression lhs = parseTerm(tk);
			String op = tk.nextToken().trim();
			Expression rhs = parseExpression(tk);
			return new BinaryExpression(lhs, rhs, op);
		}
		else {
			return null;
		}
	}

	public static Expression parseTerm(StringTokenizer tk) {
		String expr = tk.nextToken();
		expr = expr.trim();

		Expression cexpr = null;

		if(expr.indexOf(MAPPED_TO_OP) > 0){
			int start = expr.indexOf(MAPPED_TO_OP);
			String src = expr.substring(0,start);
			String trg = expr.substring(start+MAPPED_TO_OP.length());
			cexpr = new MappedToExpression(src,trg);
		}
		else if (expr.startsWith(TARGET_PREFIX)) {
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

			if (expr.startsWith("'")) {
				Integer i = ParsingUtil.toInt(expr.trim());
				cexpr = new ConstantIntegerExpression(i);
			} else if (!Character.isDigit(expr.charAt(0))) {
				cexpr = new VariableValueExpression(expr);
			} else {
				Integer i = ParsingUtil.toInt(expr.trim());
				cexpr = new ConstantIntegerExpression(i);
			}
		}
		return cexpr;
	}
}
