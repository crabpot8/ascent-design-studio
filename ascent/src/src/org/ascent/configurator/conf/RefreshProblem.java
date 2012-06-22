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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.Constraint;
import org.ascent.configurator.RefreshCore;
import org.ascent.configurator.RefreshEngine;
import org.ascent.configurator.UnsolvableException;
import org.ascent.configurator.conf.debug.ConstraintReference;
import org.ascent.configurator.conf.debug.DebuggerFactory;
import org.ascent.configurator.conf.debug.ExcludesDebugger;
import org.ascent.configurator.conf.debug.ProblemDebugger;
import org.ascent.configurator.conf.debug.RequiresDebugger;
import org.ascent.configurator.conf.debug.SelectDebugger;
import org.ascent.expr.BinaryExpression;
import org.ascent.expr.Cardinality;
import org.ascent.expr.Expression;
import org.ascent.util.ParsingUtil;


public class RefreshProblem implements DebuggerFactory{

	private List sourceItems_ = new ArrayList();

	private List targetItems_ = new ArrayList();

	private List disabledItems_ = new ArrayList();

	private List selectedItems_ = new ArrayList();

	private Map<Object, List> requiresMap_ = new HashMap<Object, List>();

	private Map<Object, List> excludesMap_ = new HashMap<Object, List>();

	private Map<Object, List> selectMap_ = new HashMap<Object, List>();

	private Map<Object, Cardinality> sourceMappedInstancesCountMap_ = new HashMap<Object, Cardinality>();

	private Map<Object, Cardinality> targetMappedInstancesCountMap_ = new HashMap<Object, Cardinality>();

	private Map<Object, List<Constraint>> sourceFeasibilityConstraintsMap_ = new HashMap<Object, List<Constraint>>();

	private Map<Object, List> feasibleTargetsMap_ = new HashMap<Object, List>();

	private Map<Object, List> inFeasibleTargetsMap_ = new HashMap<Object, List>();

	private Map<Object, Map> sourceVariableValuesTable_ = new HashMap<Object, Map>();

	private Map<Object, Map> targetVariableValuesTable_ = new HashMap<Object, Map>();

	private Map<Object, Integer> variablesTable_ = new HashMap<Object, Integer>();

	private Map<Object, Map<String, Object>> injectionValues_ = new HashMap<Object, Map<String, Object>>();

	private Map<String, Expression> variableDefinitions_ = new HashMap<String, Expression>();

	private Map<Object, Map<String, Expression>> contextVariableDefinitions_ = new HashMap<Object, Map<String, Expression>>();
	
	private Map<ConstraintReference, ProblemDebugger> debuggers_ = new HashMap<ConstraintReference, ProblemDebugger>();

	private List<ConfigurationAction> postConfigurationActions_ = new ArrayList<ConfigurationAction>();

	private List<ConfigurationAction> preConfigurationActions_ = new ArrayList<ConfigurationAction>();

	private Map<Object,List> sourceMappingExpressionsTable_ = new HashMap<Object, List>();
	
	private Map<Object,Map> targetResourceConstraints_ = new HashMap<Object, Map>();
	
	private Expression goalFunction_;

	private boolean maximizeGoal_;

	private boolean zeroUndefinedVariables_ = true;
	
	
	
	public boolean getZeroUndefinedVariables() {
		return zeroUndefinedVariables_;
	}

	public void setZeroUndefinedVariables(boolean zeroUndefinedVariables) {
		zeroUndefinedVariables_ = zeroUndefinedVariables;
	}

	/**
	 * @return the excludesMap
	 */
	public Map<Object, List> getExcludesMap() {
		return excludesMap_;
	}

	/**
	 * @param excludesMap
	 *            the excludesMap to set
	 */
	public void setExcludesMap(Map<Object, List> excludesMap) {
		excludesMap_ = excludesMap;
	}

	/**
	 * @return the feasibleTargetsMap
	 */
	public Map<Object, List> getFeasibleTargetsMap() {
		return feasibleTargetsMap_;
	}

	/**
	 * @param feasibleTargetsMap
	 *            the feasibleTargetsMap to set
	 */
	public void setFeasibleTargetsMap(Map<Object, List> feasibleTargetsMap) {
		feasibleTargetsMap_ = feasibleTargetsMap;
	}

	
	
	public Map<Object, List> getInFeasibleTargetsMap() {
		return inFeasibleTargetsMap_;
	}

	public void setInFeasibleTargetsMap(Map<Object, List> inFeasibleTargetsMap) {
		inFeasibleTargetsMap_ = inFeasibleTargetsMap;
	}
	
	public List getInFeasibleTargets(Object src){
		return getOrCreateList(src, inFeasibleTargetsMap_);
	}

	/**
	 * @return the requiresMap
	 */
	public Map<Object, List> getRequiresMap() {
		return requiresMap_;
	}

	public List getOrCreateList(Object key, Map map) {
		List l = (List) map.get(key);
		if (l == null) {
			l = new ArrayList();
			map.put(key, l);
		}
		return l;
	}

	public Map getOrCreateMap(Object key, Map map) {
		Map l = (Map) map.get(key);
		if (l == null) {
			l = new HashMap();
			map.put(key, l);
		}
		return l;
	}
	
	public Map getTargetResourceConstraintsMap(Object target){
		return getOrCreateMap(target, targetResourceConstraints_);
	}

	public List getRequires(Object o) {
		return getOrCreateList(o, requiresMap_);
	}

	public List getExcludes(Object o) {
		return getOrCreateList(o, excludesMap_);
	}

	public List getFeasibleTargets(Object o) {
		return getOrCreateList(o, feasibleTargetsMap_);
	}
	
	public List getSourceMappingExpressions(Object src){
		return getOrCreateList(src, sourceMappingExpressionsTable_);
	}

	/**
	 * @param requiresMap
	 *            the requiresMap to set
	 */
	public void setRequiresMap(Map<Object, List> requiresMap) {
		requiresMap_ = requiresMap;
	}

	/**
	 * @return the selectMap
	 */
	public Map<Object, List> getSelectMap() {
		return selectMap_;
	}

	/**
	 * @param selectMap
	 *            the selectMap to set
	 */
	public void setSelectMap(Map<Object, List> selectMap) {
		selectMap_ = selectMap;
	}

	/**
	 * @return the sourceFeasibilityConstraintsMap
	 */
	public Map<Object, List<Constraint>> getSourceFeasibilityConstraintsMap() {
		return sourceFeasibilityConstraintsMap_;
	}

	/**
	 * @param sourceFeasibilityConstraintsMap
	 *            the sourceFeasibilityConstraintsMap to set
	 */
	public void setSourceFeasibilityConstraintsMap(
			Map<Object, List<Constraint>> sourceFeasibilityConstraintsMap) {
		sourceFeasibilityConstraintsMap_ = sourceFeasibilityConstraintsMap;
	}

	/**
	 * @return the sourceItems
	 */
	public List getSourceItems() {
		return sourceItems_;
	}

	/**
	 * @param sourceItems
	 *            the sourceItems to set
	 */
	public void setSourceItems(List sourceItems) {
		sourceItems_ = sourceItems;
	}

	/**
	 * @param sourceItems
	 *            the sourceItems to set
	 */
	public void setSourceItems(Object[] sourceItems) {
		setSourceItems(Arrays.asList(sourceItems));
	}

	/**
	 * @return the sourceMappedInstancesCountMap
	 */
	public Map<Object, Cardinality> getSourceMappedInstancesCountMap() {
		return sourceMappedInstancesCountMap_;
	}

	/**
	 * @param sourceMappedInstancesCountMap
	 *            the sourceMappedInstancesCountMap to set
	 */
	public void setSourceMappedInstancesCountMap(
			Map<Object, Cardinality> sourceMappedInstancesCountMap) {
		sourceMappedInstancesCountMap_ = sourceMappedInstancesCountMap;
	}

	/**
	 * @return the targetItems
	 */
	public List getTargetItems() {
		return targetItems_;
	}

	/**
	 * @param targetItems
	 *            the targetItems to set
	 */
	public void setTargetItems(List targetItems) {
		targetItems_ = targetItems;
	}

	/**
	 * @param targetItems
	 *            the targetItems to set
	 */
	public void setTargetItems(Object[] targetItems) {
		setTargetItems(Arrays.asList(targetItems));
	}

	/**
	 * @return the targetMappedInstancesCountMap
	 */
	public Map<Object, Cardinality> getTargetMappedInstancesCountMap() {
		return targetMappedInstancesCountMap_;
	}

	/**
	 * @param targetMappedInstancesCountMap
	 *            the targetMappedInstancesCountMap to set
	 */
	public void setTargetMappedInstancesCountMap(
			Map<Object, Cardinality> targetMappedInstancesCountMap) {
		targetMappedInstancesCountMap_ = targetMappedInstancesCountMap;
	}

	/**
	 * @return the goalFunction
	 */
	public Expression getGoalFunction() {
		return goalFunction_;
	}

	/**
	 * @param goalFunction
	 *            the goalFunction to set
	 */
	public void setGoalFunction(Expression goalFunction) {
		goalFunction_ = goalFunction;
	}

	/**
	 * @return the maximizeGoal
	 */
	public boolean isMaximizeGoal() {
		return maximizeGoal_;
	}

	/**
	 * @param maximizeGoal
	 *            the maximizeGoal to set
	 */
	public void setMaximizeGoal(boolean maximizeGoal) {
		maximizeGoal_ = maximizeGoal;
	}

	/**
	 * @return the sourceVariableValuesTable
	 */
	public Map<Object, Map> getSourceVariableValuesTable() {
		return sourceVariableValuesTable_;
	}
	
	public Map getSourceVariableValues(Object src){
		return getOrCreateMap(src, sourceVariableValuesTable_);
	}
	
	public Map getTargetVariableValues(Object trg){
		return getOrCreateMap(trg, targetVariableValuesTable_);
	}

	/**
	 * @param sourceVariableValuesTable
	 *            the sourceVariableValuesTable to set
	 */
	public void setSourceVariableValuesTable(
			Map<Object, Map> sourceVariableValuesTable) {
		sourceVariableValuesTable_ = sourceVariableValuesTable;
	}

	public Map<Object, Map> getTargetVariableValuesTable() {
		return targetVariableValuesTable_;
	}

	public Map getTargetVariables(Object target) {
		return getOrCreateMap(target, targetVariableValuesTable_);
	}

	public void setTargetVariableValuesTable(
			Map<Object, Map> targetVariableValuesTable) {
		targetVariableValuesTable_ = targetVariableValuesTable;
	}

	public Map<Object, Integer> getVariablesTable() {
		return variablesTable_;
	}

	public void setVariablesTable(Map<Object, Integer> variablesTable) {
		variablesTable_ = variablesTable;
	}
	
	public void addSelectMappingConstraint(Object src, List othersrcs, int min, int max){
		othersrcs.add(0,new Cardinality(min,max));
		getSelectMap().put(src, othersrcs);
	}

	/**
	 * @return the injectionValues
	 */
	public Map<String, Object> getInjectionValues(Object src) {
		return injectionValues_.get(src);
	}
	
	/**
	 * @return the injectionValues
	 */
	public Map<Object, Map<String,Object>> getInjectionValues() {
		return injectionValues_;
	}

	/**
	 * @param injectionValues
	 *            the injectionValues to set
	 */
	public void setInjectionValues(Object src,
			Map<String, Object> injectionValues) {
		injectionValues_.put(src, injectionValues);
	}

	public List getDisabledItems() {
		return disabledItems_;
	}

	public void setDisabledItems(List disabledItems) {
		disabledItems_ = disabledItems;
	}

	public List getSelectedItems() {
		return selectedItems_;
	}

	public void setSelectedItems(List selectedItems) {
		selectedItems_ = selectedItems;
	}

	public List<ConfigurationAction> getPostConfigurationActions() {
		return postConfigurationActions_;
	}

	public void setPostConfigurationActions(
			List<ConfigurationAction> configurationActions) {
		postConfigurationActions_ = configurationActions;
	}

	public List<ConfigurationAction> getPreConfigurationActions() {
		return preConfigurationActions_;
	}

	public void setPreConfigurationActions(
			List<ConfigurationAction> preConfigurationActions) {
		preConfigurationActions_ = preConfigurationActions;
	}

	public Map<String, Expression> getVariableDefinitions() {
		return variableDefinitions_;
	}

	public void setVariableDefinitions(
			Map<String, Expression> variableDefinitions) {
		variableDefinitions_ = variableDefinitions;
	}

	public Map<String, Expression> getContextVariableDefinitions(Object ctx) {
		Map<String, Expression> ctxvars = contextVariableDefinitions_.get(ctx);
		if (ctxvars == null) {
			ctxvars = new HashMap<String, Expression>();
			contextVariableDefinitions_.put(ctx, ctxvars);
		}
		return ctxvars;
	}

	public Map<Object, Map<String, Expression>> getContextVariableDefinitions() {
		return contextVariableDefinitions_;
	}

	public void setContextVariableDefinitions(
			Map<Object, Map<String, Expression>> contextVariableDefinitions) {
		contextVariableDefinitions_ = contextVariableDefinitions;
	}

	public void inject(RefreshEngine engine) {
		engine.setProblem(this);

		RefreshCore core = engine.getCore();
		if (targetItems_.size() == 0)
			throw new UnsolvableException("No target items were specified.");
		if (sourceItems_.size() == 0)
			throw new UnsolvableException("No source items were specified.");

		for (ConfigurationAction act : getPreConfigurationActions()) {
			act.setRefreshEngine(engine);
			act.run();
		}

		inject(core);
		
		for (ConfigurationAction act : getPostConfigurationActions()) {
			act.setRefreshEngine(engine);
			act.run();
		}
	}
	
	public void inject(RefreshCore core) {
		
		if (targetItems_.size() == 0)
			throw new UnsolvableException("No target items were specified.");
		if (sourceItems_.size() == 0)
			throw new UnsolvableException("No source items were specified.");

		
		core.setSetsToMap(sourceItems_, targetItems_);
		
		for (Object src : requiresMap_.keySet()) {
			List requires = requiresMap_.get(src);
			if (requires != null && requires.size() > 0) {
				for (Object req : requires) {
					ConstraintReference ref = core.addRequiresMappingConstraint(src, req);
					debuggers_.put(ref,new RequiresDebugger(core,this,src,req));
				}
			}
		}

		for (Object src : excludesMap_.keySet()) {
			List requires = excludesMap_.get(src);
			if (requires != null && requires.size() > 0) {
				for (Object req : requires) {
					ConstraintReference ref = core.addExcludesMappingConstraint(src, req);
					debuggers_.put(ref,new ExcludesDebugger(core,this,src,req));
				}
			}
		}

		for (Object src : selectMap_.keySet()) {
			List requires = selectMap_.get(src);
			if (requires != null && requires.size() > 0) {
				Cardinality card = (Cardinality) requires.get(0);
				ArrayList rlist = new ArrayList();
				rlist.addAll(requires);
				rlist.remove(0);
				ConstraintReference ref = core.addSelectMappingConstraint(src, rlist, card.getMin(),
						card.getMax());
				debuggers_.put(ref, new SelectDebugger(core,this,src,rlist,card));
			}
		}

		for (Object src : sourceMappedInstancesCountMap_.keySet()) {
			Cardinality card = sourceMappedInstancesCountMap_.get(src);
			if (card != null) {
				core.addSourceMappedCardinalityConstraint(src, card.getMin(),
						card.getMax());
			}
		}

		for (Object src : targetMappedInstancesCountMap_.keySet()) {
			Cardinality card = targetMappedInstancesCountMap_.get(src);
			if (card != null) {
				core.addTargetMappedSourcesCardinalityConstraint(src, card
						.getMin(), card.getMax());
			}
		}

		for (Object src : sourceFeasibilityConstraintsMap_.keySet()) {
			List<Constraint> cons = sourceFeasibilityConstraintsMap_.get(src);
			if (cons != null) {
				for (Constraint con : cons) {
					core.addSourceMappingConstraint(src, (BinaryExpression) con
							.getExpression());
				}
			}
		}

		for (Object src : feasibleTargetsMap_.keySet()) {
			List valid = feasibleTargetsMap_.get(src);
			if (valid != null) {
				core.setValidTargets(src, valid);
			}
		}
		
		for (Object src : inFeasibleTargetsMap_.keySet()) {
			List invalid = inFeasibleTargetsMap_.get(src);
			if (invalid != null) {
				core.setInValidTargets(src, invalid);
			}
		}

		for (Object src : sourceVariableValuesTable_.keySet()) {
			Map values = sourceVariableValuesTable_
			.get(src);
			for(Object key : values.keySet()){
				core.getSourceVariableValues(src).put(key, ParsingUtil.toInt(values.get(key)));
			}
		}

		for (Object src : targetVariableValuesTable_.keySet()) {
			Map values = targetVariableValuesTable_
			.get(src);
			for(Object key : values.keySet()){
				core.getTargetVariableValues(src).put(key, ParsingUtil.toInt(values.get(key)));
			}
		}

		for (Object key : variablesTable_.keySet()) {
			core.setIntVariableValue(key, variablesTable_.get(key));
		}
		
		for (Object t : targetResourceConstraints_.keySet()){
			Map rescon = getTargetResourceConstraintsMap(t);
			for(Object r : rescon.keySet()){
				Object oval = rescon.get(r);
				Integer val = null;
				if(oval instanceof Integer){
					val = (Integer)oval;
				}
				else {
					val = ParsingUtil.toInt(oval);
				}
				core.addTargetIntResourceConstraint(t, r, val);
			}
		}

		for (String var : variableDefinitions_.keySet()) {
			core.createIntVariable(var, "=", variableDefinitions_.get(var));
		}

		for (Object ctx : contextVariableDefinitions_.keySet()) {
			Map<String, Expression> vars = getContextVariableDefinitions(ctx);
			for (String var : vars.keySet()) {
				core.createIntVariable(ctx, var, "=", vars.get(var));
			}
		}
		
		for(Object o : sourceMappingExpressionsTable_.keySet()){
			List l = getSourceMappingExpressions(o);
			for(Object e : l){
				BinaryExpression exp = (BinaryExpression)e;
				core.addSourceMappingConstraint(o, exp);
			}
		}
		
		core.setZeroUndefinedVariables(getZeroUndefinedVariables());
		
		for (Object o : selectedItems_) {
			core.requireMapped(o);
		}

		for (Object o : disabledItems_) {
			core.requireNotMapped(o);
		}

		if (goalFunction_ != null) {
			core.setOptimizationFunction(goalFunction_);
			core.setMaximizeOptimizationFunction(maximizeGoal_);
		}

	}

	
	public ProblemDebugger createDebugger(ConstraintReference ref) {
		return debuggers_.get(ref);
	}
	
}
