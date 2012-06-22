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

package org.ascent.mmkp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.binpacking.Dependencies;
import org.ascent.binpacking.DependencyManager;
import org.ascent.binpacking.OptimizationFunction;
import org.ascent.binpacking.StateProvider;
import org.ascent.binpacking.BinState;
import org.ascent.binpacking.Util;
import org.ascent.binpacking.ValueFunction;

public abstract class MMKPCore {

	private Map<Object, SetState> setsByItem_ = new HashMap<Object, SetState>();
	private Collection solution_;
	private List<SetState> setStates_;
	private ValueFunction valueFunction_;
	private ValueFunction<Collection> optimizationFunction_;
	private DependencyManager dependencyManager_;
	private StateProvider stateProvider_;
	private Collection initialSolution_;
	private BinState target_;

	public abstract SetState choosePermutationSet();

	public abstract Object chooseAlternateItem(SetState permset);

	public abstract boolean done();

	public void init() {
	}

	public void preIterate() {
	}

	public void postIterate() {
	}

	public MMKPCore(ValueFunction valueFunction,
			ValueFunction<Collection> optimizationFunction,
			DependencyManager dependencyManager, StateProvider stateProvider,
			Collection initialSolution, BinState target) {
		super();
		valueFunction_ = valueFunction;
		optimizationFunction_ = optimizationFunction;
		dependencyManager_ = dependencyManager;
		stateProvider_ = stateProvider;
		initialSolution_ = initialSolution;
		target_ = target;
	}

	public void updateCurrentSolution(Collection sol) {
		solution_ = sol;
		for (Object o : solution_) {
			SetState st = getSet(o);
			Object old = st.getCurrentItem();
			getSet(o).setCurrentItem(o);
			Util.remove(stateProvider_.getSourceState(old), target_);
			Util.insert(stateProvider_.getSourceState(o), target_);
		}
	}

	public void solve() {
		solution_ = getInitialSolution();

		while (!done()) {
			preIterate();
			iterate();
			postIterate();
		}
	}

	public void iterate() {
		SetState set = choosePermutationSet();
		if (set != null) {
			Object old = set.getCurrentItem();
			Object perm = chooseAlternateItem(set);
			if (perm != null && perm != set.getCurrentItem()) {
				Collection nsol = new ArrayList(solution_);

				if (swapIn(nsol, set, perm, set.getCurrentItem())) {

					double s1val = getValue(solution_);
					double s2val = getValue(nsol);

					if (s2val > s1val) {
						updateCurrentSolution(nsol);
					}
				}
			}
		}
	}

	public boolean swapIn(Collection solution, SetState st, Object newitem,
			Object olditem) {
		List toremove = new ArrayList();
		List toadd = new ArrayList();
		getAuxilliaryItems(solution, st, newitem, toremove, toadd);

		for (Object o : toremove) {
			Object alt = getAlternateItem(getSet(o), solution, toadd, toremove);

			if (alt == null)
				return false;

			solution.remove(o);
			solution.add(o);
		}
		for (Object o : toadd) {
			solution.add(o);
		}

		return true;
	}

	public SetState getSet(Object src) {
		return setsByItem_.get(src);
	}

	public void getAuxilliaryItems(Collection solution, SetState st,
			Object item, List toremove, List toadd) {
		Dependencies deps = dependencyManager_.getDependencies(item);

		toadd.add(item);
		Util.collectRequired(deps, toadd, dependencyManager_);
		Util.collectExcluded(toadd, toremove, dependencyManager_);
	}

	public Object getAlternateItem(SetState st, Collection solution,
			List toadd, List toremove) {
		for (Object o : st.getSet()) {
			if (o != st.getCurrentItem() && !toremove.contains(o)) {
				List required = new ArrayList();
				List excluded = new ArrayList();
				Util.collectRequired(dependencyManager_.getDependencies(o),
						required, dependencyManager_);
				required.add(o);
				Util.collectExcluded(required, excluded, dependencyManager_);

				List rcopy = new ArrayList(required);
				List ecopy = new ArrayList(excluded);
				required.retainAll(toremove);
				excluded.retainAll(toadd);
				ecopy.removeAll(toremove);
				ecopy.retainAll(solution);
				if (required.size() == 0 && excluded.size() == 0
						&& ecopy.size() == 0) {
					toadd.addAll(rcopy);
					return o;
				}

			}
		}

		return null;
	}

	public double getValue(Collection sol) {
		return optimizationFunction_.getValue(sol);
	}
	
	public double getItemValue(Object item) {
		return valueFunction_.getValue(item);
	}

	public void setSets(List<List> sets) {
		setStates_ = new ArrayList<SetState>(sets.size());
		for (int i = 0; i < sets.size(); i++) {
			List l = sets.get(i);
			SetState st = new SetState(l, i);
			for (Object o : l) {
				setsByItem_.put(o, st);
			}
			setStates_.add(st);
		}
	}

	public ValueFunction getValueFunction() {
		return valueFunction_;
	}

	public void setValueFunction(ValueFunction valueFunction) {
		valueFunction_ = valueFunction;
	}

	public ValueFunction<Collection> getOptimizationFunction() {
		return optimizationFunction_;
	}

	public void setOptimizationFunction(
			ValueFunction<Collection> optimizationFunction) {
		optimizationFunction_ = optimizationFunction;
	}

	public DependencyManager getDependencyManager() {
		return dependencyManager_;
	}

	public void setDependencyManager(DependencyManager dependencyManager) {
		dependencyManager_ = dependencyManager;
	}

	public Collection getInitialSolution() {
		return initialSolution_;
	}

	public void setInitialSolution(Collection initialSolution) {
		initialSolution_ = initialSolution;
	}

	public List<SetState> getSetStates() {
		return setStates_;
	}

	public void setSetStates(List<SetState> setStates) {
		setStates_ = setStates;
	}

	public StateProvider getStateProvider() {
		return stateProvider_;
	}

	public void setStateProvider(StateProvider stateProvider) {
		stateProvider_ = stateProvider;
	}

}
