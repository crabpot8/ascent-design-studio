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

import org.ascent.expr.Expression;


public class GoalHandler extends AbstractExpressionCreatorHandler {

	public class InvalidGoalDeclarationException extends RuntimeException {
		public InvalidGoalDeclarationException(String msg){
			super(msg);
		}
	}
	
	public static final String MAXIMIZE_PREFIX = "maximize";
	public static final String MINIMIZE_PREFIX = "minimize";

	/* (non-Javadoc)
	 * @see org.refresh.core.conf.AbstractExpressionCreatorHandler#handle(org.refresh.core.conf.RefreshProblem, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void handle(RefreshProblem problem, String context, String directive, String argstr) {
		boolean max = true;
		argstr = argstr.trim();
		if(argstr.startsWith(MAXIMIZE_PREFIX))
			argstr = argstr.substring(MAXIMIZE_PREFIX.length());
		else if(argstr.startsWith(MINIMIZE_PREFIX)){
			argstr = argstr.substring(MINIMIZE_PREFIX.length());
			max = false;
		}
		super.handle(problem, context, directive, argstr);
		problem.setMaximizeGoal(max);
	}



	/* (non-Javadoc)
	 * @see org.refresh.core.conf.AbstractExpressionCreatorHandler#handle(org.refresh.core.conf.RefreshProblem, java.lang.String, java.lang.String, java.lang.String, org.refresh.core.expr.Expression)
	 */
	@Override
	public void handle(RefreshProblem problem, String context, String directive, String argstr, Expression expr) {
		problem.setGoalFunction(expr);
	}
	
	

}
