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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.RefreshEngine;
import org.eclipse.gmt.gems.model.intelligence.KBConstraintFactory;
import org.eclipse.gmt.gems.model.intelligence.KBFactory;
import org.eclipse.gmt.gems.model.intelligence.RefreshKB;
import org.eclipse.gmt.gems.model.intelligence.SimpleMapTypeSystem;


public class EvaluateHandler implements ConfigDirectiveHandler {

	public static final String FEATURE_KEY = "___feature";

	public class AddEvaluationAction implements ConfigurationAction {
		private RefreshEngine refreshEngine_;

		private String expression_;

		private String expressionType_;

		private String context_;

		public AddEvaluationAction(String context, String expressionType,
				String expression) {
			super();
			context_ = context;
			expressionType_ = expressionType;
			expression_ = expression;
		}

		public RefreshEngine getRefreshEngine() {
			return refreshEngine_;
		}

		public void setRefreshEngine(RefreshEngine refreshEngine) {
			refreshEngine_ = refreshEngine;
		}

		public String getExpression() {
			return expression_;
		}

		public void setExpression(String expression) {
			expression_ = expression;
		}

		public String getExpressionType() {
			return expressionType_;
		}

		public void setExpressionType(String expressionType) {
			expressionType_ = expressionType;
		}

		public void run() {
			RefreshProblem p = refreshEngine_.getProblem();
			List feasible = p.getFeasibleTargetsMap().get(context_);
			if (feasible == null) {
				feasible = new ArrayList();
				feasible.addAll(p.getTargetItems());
				p.getFeasibleTargetsMap().put(context_, feasible);
			}
			RefreshKB kb = getKb();
			Map ctx = p.getSourceVariableValuesTable().get(context_);
			if (ctx == null)
				ctx = new HashMap();

			for (Object o : feasible) {
				Map vals = p.getTargetVariableValuesTable().get(o);
				if (vals == null)
					vals = new HashMap();

				vals.put(FEATURE_KEY, o);
				kb.add(vals);
			}
			kb.add(SimpleMapTypeSystem.MAP_OBJECT_TYPE, "ValidTargets",
					SimpleMapTypeSystem.MAP_OBJECT_TYPE, KBConstraintFactory
							.getInstance().createConstraint(expressionType_,
									expression_));

			List valid = kb.validTargets(ctx, "ValidTargets");
			feasible.clear();
			if (valid != null) {
				for (Object o : valid) {
					Map mv = (Map) o;
					feasible.add(mv.get(FEATURE_KEY));
				}
			}
		}
	}

	public class InvalidEvaluateDeclarationException extends RuntimeException {
		public InvalidEvaluateDeclarationException(String msg) {
			super(msg);
		}
	}

	private RefreshKB kb_;

	public void handle(RefreshProblem problem, String context,
			String directive, String argstr) {

		argstr = argstr.trim();

		int start = argstr.indexOf("{");
		if (start == 0) {
			throw new InvalidEvaluateDeclarationException(
					"An Evaluate declaration must have the form "
							+ "EXPR-TYPE { EXPRESSION } and the provided Evaluate declaration is missing an EXPR-TYPE. Provided expression: "
							+ argstr);
		} else if (start < 0) {
			throw new InvalidEvaluateDeclarationException(
					"An Evaluate declaration must have the form "
							+ "EXPR-TYPE { EXPRESSION } and the provided Evaluate declaration is missing a \"{\". Provided expression: "
							+ argstr);
		}
		int end = argstr.indexOf("}", start + 1);
		if (end < 2) {
			throw new InvalidEvaluateDeclarationException(
					"An Evaluate declaration must have the form "
							+ "EXPR-TYPE { EXPRESSION } and the provided Evaluate declaration is missing a \"}\". Provided expression: "
							+ argstr);
		}

		String type = argstr.substring(0, start);
		String expr = argstr.substring(start + 1, end);

		AddEvaluationAction act = new AddEvaluationAction(context.trim(), type,
				expr);
		problem.getPreConfigurationActions().add(act);
	}

	public RefreshKB getKb() {
		if (kb_ == null)
			kb_ = KBFactory.getInstance().createKB();
		return kb_;
	}

	public void setKb(RefreshKB kb) {
		kb_ = kb;
	}
}
