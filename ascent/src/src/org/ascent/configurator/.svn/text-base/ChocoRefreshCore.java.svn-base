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

package org.ascent.configurator;

import java.util.List;
import java.util.Map;

import org.ascent.expr.BinaryExpression;
import org.ascent.expr.Expression;

import choco.Constraint;
import choco.Solver;
import choco.Var;
import choco.integer.IntExp;
import choco.integer.IntVar;


public interface ChocoRefreshCore extends RefreshCore {

	/**
	 * @return the optimizationFunction
	 */
	public abstract IntExp getOptimizationFunction();

	
	public abstract Map<Object, List> nextMapping(Solver s);

	public abstract Map<Object, IntVar> getTargetTable(Object src);

	public abstract Constraint getSourceMappingConstraint(Object src,
			Object trg, IntVar mapvar, BinaryExpression expr);

	public abstract Constraint getConstraint(Expression lhs, Expression rhs,
			String op);

	public abstract Constraint getConstraint(Object src, Object trg,
			Expression lhs, Expression rhs, String op);

	public abstract IntExp getIntExpr(Object src, Object trg, Expression exp);

	public abstract IntExp getOrCreateSummation(Object key, Object var,
			Map<Object, Map<Object, Var>> avals,
			Map<Object, Map<Object, Object>> vals);

	public abstract int getValue(Object item, Object var,
			Map<Object, Map<Object, Object>> vals);

	public abstract IntVar getOrCreatePresenceVariable(Object item);

	public abstract IntVar getOrCreateIntVariableValue(Object item, Object var,
			Map<Object, Map<Object, Var>> avals,
			Map<Object, Map<Object, Object>> vals);

	public abstract IntExp getIntExpr(Expression exp);

	public abstract IntExp getIntExpr(IntExp lhs, IntExp rhs, String op);

}
