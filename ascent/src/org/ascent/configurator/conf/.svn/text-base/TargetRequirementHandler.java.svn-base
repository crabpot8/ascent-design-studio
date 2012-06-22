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
import java.util.List;

import org.ascent.configurator.Constraint;
import org.ascent.expr.BinaryExpression;
import org.ascent.expr.Expression;


public class TargetRequirementHandler extends AbstractExpressionCreatorHandler {

	public class MalformedConstraintException extends RuntimeException {
		public MalformedConstraintException(String msg) {
			super(msg);
		}
	}

	
	public void handle(RefreshProblem problem, String context,
			String directive, String argstr, Expression curr) {
		

		if (!(curr instanceof BinaryExpression)) {
			throw new MalformedConstraintException(
					"A constraint must specify at least one variable, one comparison, and one comparison value. The constraint \""
							+ argstr
							+ "\" does not specify all of these elements.");
		}

		List<Constraint> cons = problem.getSourceFeasibilityConstraintsMap()
				.get(context);
		if (cons == null) {
			cons = new ArrayList<Constraint>();
		}
		cons.add(new Constraint((BinaryExpression) curr));
		problem.getSourceFeasibilityConstraintsMap().put(context, cons);
	}

}
