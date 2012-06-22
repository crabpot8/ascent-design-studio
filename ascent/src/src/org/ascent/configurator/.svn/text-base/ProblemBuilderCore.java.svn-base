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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.configurator.conf.debug.ConstraintReference;
import org.ascent.expr.BinaryExpression;
import org.ascent.expr.Cardinality;
import org.ascent.expr.ConstantIntegerExpression;
import org.ascent.expr.Expression;

public class ProblemBuilderCore extends DelegatingCore {
	private RefreshProblem problem_ = new RefreshProblem();

	public ProblemBuilderCore(RefreshCore core) {
		super(core);
	}

	public RefreshProblem getProblem() {
		return problem_;
	}

	public void setProblem(RefreshProblem problem) {
		problem_ = problem;
	}

	@Override
	public ConstraintReference addExcludesMappingConstraint(Object src,
			Object req) {
		problem_.getExcludes(src).add(req);
		return super.addExcludesMappingConstraint(src, req);
	}

	@Override
	public ConstraintReference addRequiresMappingConstraint(Object src,
			Object req) {
		problem_.getRequires(src).add(req);
		return super.addRequiresMappingConstraint(src, req);
	}

	@Override
	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int min, int max) {
		problem_.addSelectMappingConstraint(src, set, min, max);
		return super.addSelectMappingConstraint(src, set, min, max);
	}

	@Override
	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int exactcard) {
		problem_.addSelectMappingConstraint(src, set, exactcard, exactcard);
		return super.addSelectMappingConstraint(src, set, exactcard);
	}

	@Override
	public ConstraintReference addSourceMappedCardinalityConstraint(Object src,
			int min, int max) {
		problem_.getSourceMappedInstancesCountMap().put(src,
				new Cardinality(min, max));
		return super.addSourceMappedCardinalityConstraint(src, min, max);
	}

	@Override
	public ConstraintReference addSourceMappingConstraint(Object src,
			BinaryExpression expression) {
		problem_.getSourceMappingExpressions(src).add(expression);
		return super.addSourceMappingConstraint(src, expression);
	}

	@Override
	public ConstraintReference addTargetIntResourceConstraint(Object target,
			Object var, int val) {
		problem_.getTargetResourceConstraintsMap(target).put(var, val);
		return super.addTargetIntResourceConstraint(target, var, val);
	}

	@Override
	public ConstraintReference addTargetMappedSourcesCardinalityConstraint(
			Object target, int min, int max) {
		problem_.getTargetMappedInstancesCountMap().put(target,
				new Cardinality(min, max));
		return super.addTargetMappedSourcesCardinalityConstraint(target, min,
				max);
	}

	@Override
	public ConstraintReference addTargetMappedSourcesConstraint(Object target,
			int exactmapped) {
		problem_.getTargetMappedInstancesCountMap().put(target,
				new Cardinality(exactmapped, exactmapped));
		return super.addTargetMappedSourcesConstraint(target, exactmapped);
	}

	@Override
	public void createIntVariable(Object ctx, Object variable, String op,
			Expression exp) {
		problem_.getContextVariableDefinitions(ctx).put("" + variable, exp);
		super.createIntVariable(ctx, variable, op, exp);
	}

	@Override
	public void createIntVariable(Object variable, String op, Expression exp) {
		problem_.getVariableDefinitions().put("" + variable, exp);
		super.createIntVariable(variable, op, exp);
	}

	@Override
	public Map getContextVariableValues(Object ctx) {
		return super.getContextVariableValues(ctx);
	}

	@Override
	public boolean getMaximizeOptimizationFunction() {
		return super.getMaximizeOptimizationFunction();
	}

	@Override
	public List getSourceTargets(Object src) {
		return super.getSourceTargets(src);
	}

	@Override
	public Map getSourceVariableValues(Object source) {
		return super.getSourceVariableValues(source);
	}

	@Override
	public Map getTargetVariableValues(Object target) {
		return super.getTargetVariableValues(target);
	}

	@Override
	public Object getVariableValue(Object ctx, String var) {
		return super.getVariableValue(ctx, var);
	}

	@Override
	public Object getVariableValue(String var) {
		return super.getVariableValue(var);
	}

	@Override
	public boolean getZeroUndefinedVariables() {
		return super.getZeroUndefinedVariables();
	}

	@Override
	public void mapTo(Object src, Object trg) {
		problem_.getFeasibleTargets(src).clear();
		problem_.getFeasibleTargets(src).add(trg);
		super.mapTo(src, trg);
	}

	@Override
	public Map<Object, List> nextMapping() {
		return super.nextMapping();
	}

	@Override
	public Map<Object, List> nextMappingWithinTimeLimit(int time) {
		return super.nextMappingWithinTimeLimit(time);
	}

	@Override
	public ConstraintReference requireAllMapped() {
		for (Object o : problem_.getSourceItems()) {
			problem_.getSourceMappedInstancesCountMap().put(o,
					new Cardinality(1, Integer.MAX_VALUE));
		}
		return super.requireAllMapped();
	}

	@Override
	public ConstraintReference requireAllMappedExactlyOnce() {
		for (Object o : problem_.getSourceItems()) {
			problem_.getSourceMappedInstancesCountMap().put(o,
					new Cardinality(1, 1));
		}
		return super.requireAllMappedExactlyOnce();
	}

	@Override
	public ConstraintReference requireMapped(Object src) {
		problem_.getSourceMappedInstancesCountMap().put(src,
				new Cardinality(1, Integer.MAX_VALUE));
		return super.requireMapped(src);
	}

	@Override
	public ConstraintReference requireNotMapped(Object src) {
		problem_.getSourceMappedInstancesCountMap().put(src,
				new Cardinality(0, 0));
		return super.requireNotMapped(src);
	}

	@Override
	public void setIntVariableValue(Object variable, int value) {
		problem_.getVariableDefinitions().put("" + variable,
				new ConstantIntegerExpression(1));
		super.setIntVariableValue(variable, value);
	}

	@Override
	public void setInValidTargets(Object src, List invalid) {
		problem_.getInFeasibleTargets(src).addAll(invalid);
		super.setInValidTargets(src, invalid);
	}

	@Override
	public void setMaximizeOptimizationFunction(
			boolean maximizeOptimizationFunction) {
		problem_.setMaximizeGoal(maximizeOptimizationFunction);
		super.setMaximizeOptimizationFunction(maximizeOptimizationFunction);
	}

	@Override
	public void setOptimizationFunction(Expression exp) {
		problem_.setGoalFunction(exp);
		super.setOptimizationFunction(exp);
	}

	@Override
	public void setResourceConstraints(int[][] sourceresourceconsumption,
			int[][] targetresourceavail) {

		List targets = problem_.getTargetItems();
		for(int i = 0; i < targets.size(); i++){
			Object t = targets.get(i);
			for(int j = 0; j < targetresourceavail.length; j++){
				problem_.getTargetResourceConstraintsMap(t).put(""+j, targetresourceavail[i][j]);
			}
		}
		
		List srcs = problem_.getSourceItems();
		for(int i = 0; i < srcs.size(); i++){
			Object t = srcs.get(i);
			for(int j = 0; j < sourceresourceconsumption.length; j++){
				problem_.getSourceVariableValues(t).put(""+j, sourceresourceconsumption[i][j]);
			}
		}
		
		super.setResourceConstraints(sourceresourceconsumption,
				targetresourceavail);
	}

	@Override
	public void setSetsToMap(List srcs, List trgs) {
		problem_.setSourceItems(srcs);
		problem_.setTargetItems(trgs);
		super.setSetsToMap(srcs, trgs);
	}

	@Override
	public void setSetsToMap(Object[] srcs, Object[] trgs) {
		ArrayList a1 = new ArrayList(srcs.length);
		a1.addAll(Arrays.asList(srcs));
		ArrayList a2 = new ArrayList(trgs.length);
		a2.addAll(Arrays.asList(trgs));
		setSetsToMap(a1, a2);
		super.setSetsToMap(a1, a2);
	}

	@Override
	public void setSourceVariableValues(Object source, Map values) {
		problem_.getSourceVariableValuesTable().put(source, values);
		super.setSourceVariableValues(source, values);
	}

	@Override
	public void setSourceVariableValues(Object source, Object[] values) {
		for (int i = 0; i < values.length; i += 2) {
			problem_.getSourceVariableValues(source).put(values[0], values[1]);
		}
		super.setSourceVariableValues(source, values);
	}

	@Override
	public void setTargetVariableValues(Object target, Map values) {
		getTargetVariableValues(target).putAll(values);
		super.setTargetVariableValues(target, values);
	}

	@Override
	public void setTargetVariableValues(Object target, Object[] values) {
		for (int i = 0; i < values.length; i += 2) {
			problem_.getTargetVariableValues(target).put(values[0], values[1]);
		}
		super.setTargetVariableValues(target, values);
	}

	@Override
	public void setValidTargets(Object src, List valid) {
		problem_.getFeasibleTargets(src).addAll(valid);
		super.setValidTargets(src, valid);
	}

	@Override
	public void setZeroUndefinedVariables(boolean zeroUndefinedVariables) {
		problem_.setZeroUndefinedVariables(zeroUndefinedVariables);
		super.setZeroUndefinedVariables(zeroUndefinedVariables);
	}

}
