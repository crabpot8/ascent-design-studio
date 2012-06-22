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

import org.ascent.configurator.conf.debug.ConstraintReference;
import org.ascent.expr.BinaryExpression;
import org.ascent.expr.Expression;


public class CoreAdapter implements RefreshCore {

	public ConstraintReference addExcludesMappingConstraint(Object src,
			Object req) {
		return null;
	}

	public ConstraintReference addRequiresMappingConstraint(Object src,
			Object req) {
		return null;
	}

	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int min, int max) {
		return null;
	}

	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int exactcard) {
		return null;
	}

	public ConstraintReference addSourceMappedCardinalityConstraint(Object src,
			int min, int max) {
		return null;
	}

	public ConstraintReference addSourceMappingConstraint(Object src,
			BinaryExpression expression) {
		return null;
	}

	public ConstraintReference addTargetIntResourceConstraint(Object target,
			Object var, int val) {
		return null;
	}

	public ConstraintReference addTargetMappedSourcesCardinalityConstraint(
			Object target, int min, int max) {
		return null;
	}

	public ConstraintReference addTargetMappedSourcesConstraint(Object target,
			int exactmapped) {
		return null;
	}

	public void createIntVariable(Object variable, String op, Expression exp) {
	}

	public void createIntVariable(Object ctx, Object variable, String op,
			Expression exp) {
	}

	public Map getContextVariableValues(Object ctx) {
		return null;
	}

	public boolean getMaximizeOptimizationFunction() {
		return false;
	}

	public List getSourceTargets(Object src) {
		return null;
	}

	public Map getSourceVariableValues(Object source) {
		return null;
	}

	public Map getTargetVariableValues(Object target) {
		return null;
	}

	public Object getVariableValue(String var) {
		return null;
	}

	public Object getVariableValue(Object ctx, String var) {
		return null;
	}

	public boolean getZeroUndefinedVariables() {
		return false;
	}

	public void mapTo(Object src, Object trg) {
	}

	public Map<Object, List> nextMapping() {
		return null;
	}

	public Map<Object, List> nextMappingWithinTimeLimit(int time) {
		return null;
	}

	public ConstraintReference requireAllMapped() {
		return null;
	}

	public ConstraintReference requireAllMappedExactlyOnce() {
		return null;
	}

	public ConstraintReference requireMapped(Object src) {
		return null;
	}

	public ConstraintReference requireNotMapped(Object src) {
		return null;
	}

	public void setInValidTargets(Object src, List invalid) {
	}

	public void setIntVariableValue(Object variable, int value) {
	}

	public void setMaximizeOptimizationFunction(
			boolean maximizeOptimizationFunction) {
	}

	public void setOptimizationFunction(Expression exp) {
	}

	public void setResourceConstraints(int[][] sourceresourceconsumption,
			int[][] targetresourceavail) {
	}

	public void setSetsToMap(List srcs, List trgs) {
	}

	public void setSetsToMap(Object[] srcs, Object[] trgs) {
	}

	public void setSourceVariableValues(Object source, Map values) {
	}

	public void setSourceVariableValues(Object source, Object[] values) {
	}

	public void setTargetVariableValues(Object target, Map values) {
	}

	public void setTargetVariableValues(Object target, Object[] values) {
	}

	public void setValidTargets(Object src, List valid) {
	}

	public void setZeroUndefinedVariables(boolean zeroUndefinedVariables) {
	}

}
