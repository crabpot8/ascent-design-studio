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


public class DelegatingCore implements RefreshCore {
	private RefreshCore core_;
	
	public DelegatingCore(RefreshCore core) {
		super();
		core_ = core;
	}

	public ConstraintReference addExcludesMappingConstraint(Object src,
			Object req) {
		return core_.addExcludesMappingConstraint(src, req);
	}

	public ConstraintReference addRequiresMappingConstraint(Object src,
			Object req) {
		return core_.addRequiresMappingConstraint(src, req);
	}

	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int min, int max) {
		return core_.addSelectMappingConstraint(src, set, min, max);
	}

	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int exactcard) {
		return core_.addSelectMappingConstraint(src, set, exactcard);
	}

	public ConstraintReference addSourceMappedCardinalityConstraint(Object src,
			int min, int max) {
		return core_.addSourceMappedCardinalityConstraint(src, min, max);
	}

	public ConstraintReference addSourceMappingConstraint(Object src,
			BinaryExpression expression) {
		return core_.addSourceMappingConstraint(src, expression);
	}

	public ConstraintReference addTargetIntResourceConstraint(Object target,
			Object var, int val) {
		return core_.addTargetIntResourceConstraint(target, var, val);
	}

	public ConstraintReference addTargetMappedSourcesCardinalityConstraint(
			Object target, int min, int max) {
		return core_.addTargetMappedSourcesCardinalityConstraint(target, min,
				max);
	}

	public ConstraintReference addTargetMappedSourcesConstraint(Object target,
			int exactmapped) {
		return core_.addTargetMappedSourcesConstraint(target, exactmapped);
	}

	public void createIntVariable(Object ctx, Object variable, String op,
			Expression exp) {
		core_.createIntVariable(ctx, variable, op, exp);
	}

	public void createIntVariable(Object variable, String op, Expression exp) {
		core_.createIntVariable(variable, op, exp);
	}

	public Map getContextVariableValues(Object ctx) {
		return core_.getContextVariableValues(ctx);
	}

	public boolean getMaximizeOptimizationFunction() {
		return core_.getMaximizeOptimizationFunction();
	}

	public List getSourceTargets(Object src) {
		return core_.getSourceTargets(src);
	}

	public Map getSourceVariableValues(Object source) {
		return core_.getSourceVariableValues(source);
	}

	public Map getTargetVariableValues(Object target) {
		return core_.getTargetVariableValues(target);
	}

	public Object getVariableValue(Object ctx, String var) {
		return core_.getVariableValue(ctx, var);
	}

	public Object getVariableValue(String var) {
		return core_.getVariableValue(var);
	}

	public boolean getZeroUndefinedVariables() {
		return core_.getZeroUndefinedVariables();
	}

	public void mapTo(Object src, Object trg) {
		core_.mapTo(src, trg);
	}

	public Map<Object, List> nextMapping() {
		return core_.nextMapping();
	}

	public Map<Object, List> nextMappingWithinTimeLimit(int time) {
		return core_.nextMappingWithinTimeLimit(time);
	}

	public ConstraintReference requireAllMapped() {
		return core_.requireAllMapped();
	}

	public ConstraintReference requireAllMappedExactlyOnce() {
		return core_.requireAllMappedExactlyOnce();
	}

	public ConstraintReference requireMapped(Object src) {
		return core_.requireMapped(src);
	}

	public ConstraintReference requireNotMapped(Object src) {
		return core_.requireNotMapped(src);
	}

	public void setIntVariableValue(Object variable, int value) {
		core_.setIntVariableValue(variable, value);
	}

	public void setInValidTargets(Object src, List invalid) {
		core_.setInValidTargets(src, invalid);
	}

	public void setMaximizeOptimizationFunction(
			boolean maximizeOptimizationFunction) {
		core_.setMaximizeOptimizationFunction(maximizeOptimizationFunction);
	}

	public void setOptimizationFunction(Expression exp) {
		core_.setOptimizationFunction(exp);
	}

	public void setResourceConstraints(int[][] sourceresourceconsumption,
			int[][] targetresourceavail) {
		core_.setResourceConstraints(sourceresourceconsumption,
				targetresourceavail);
	}

	public void setSetsToMap(List srcs, List trgs) {
		core_.setSetsToMap(srcs, trgs);
	}

	public void setSetsToMap(Object[] srcs, Object[] trgs) {
		core_.setSetsToMap(srcs, trgs);
	}

	public void setSourceVariableValues(Object source, Map values) {
		core_.setSourceVariableValues(source, values);
	}

	public void setSourceVariableValues(Object source, Object[] values) {
		core_.setSourceVariableValues(source, values);
	}

	public void setTargetVariableValues(Object target, Map values) {
		core_.setTargetVariableValues(target, values);
	}

	public void setTargetVariableValues(Object target, Object[] values) {
		core_.setTargetVariableValues(target, values);
	}

	public void setValidTargets(Object src, List valid) {
		core_.setValidTargets(src, valid);
	}

	public void setZeroUndefinedVariables(boolean zeroUndefinedVariables) {
		core_.setZeroUndefinedVariables(zeroUndefinedVariables);
	}
	
	
}
