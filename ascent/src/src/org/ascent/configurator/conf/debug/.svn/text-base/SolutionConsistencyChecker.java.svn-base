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


package org.ascent.configurator.conf.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.expr.Cardinality;


public class SolutionConsistencyChecker {

	private RefreshProblem problem_;

	public SolutionConsistencyChecker(RefreshProblem problem) {
		super();
		problem_ = problem;
	}

	public RefreshProblem getProblem() {
		return problem_;
	}

	public void setProblem(RefreshProblem problem) {
		problem_ = problem;
	}

	public void assertConsistent(Map solution) {
		assert solution != null;

		checkResourceConstraintsConsistency(solution);
		checkSourceItemPresence(solution);
		checkTargetItemPresence(solution);
		checkRequiresConstraintConsistency(solution);
		checkExcludesConstraintConsistency(solution);
		checkSelectConstraintConsistency(solution);
		checkTargetMappedInstancesConsistency(solution);
		checkSourceMappedInstancesConsistency(solution);
		checkFeasibilityConstraintConsistency(solution);
	}

	public void checkResourceConstraintsConsistency(Map<Object, List> sol) {
		Map<Object, List> targetMaps = new HashMap<Object, List>();
		for (Object o : sol.keySet()) {
			List l = sol.get(o);
			for (Object t : l) {
				List smap = (List) targetMaps.get(t);
				if (smap == null) {
					smap = new ArrayList();
					targetMaps.put(t, smap);
				}
				smap.add(o);
			}
		}

		List<int[]> tsizes = new ArrayList<int[]>();
		for (Object target : problem_.getTargetItems()) {
			int size = problem_.getTargetResourceConstraintsMap(target).size();
			int[] sizes = new int[size];
			for (int i = 0; i < sizes.length; i++) {
				sizes[i] = ((Integer) problem_.getTargetResourceConstraintsMap(
						target).get("" + i)).intValue();
			}
			tsizes.add(sizes);
		}

		for (int i = 0; i < problem_.getTargetItems().size(); i++) {
			Object target = problem_.getTargetItems().get(i);
			List srcs = targetMaps.get(target);
			if (srcs != null) {
				int[] size = tsizes.get(i);
				for (Object src : srcs) {
					for (int j = 0; j < size.length; j++) {
						size[j] -= ((Integer) problem_.getSourceVariableValues(
								src).get("" + j)).intValue();

						if (!(size[j] >= 0))
							throw new RuntimeException();
					}
				}
			}
		}
	}

	/*
	 * This method
	 */
	public void checkSourceItemPresence(Map<Object, List> sol) {
		for (Object o : sol.keySet())
			assert problem_.getSourceItems().contains(o);
	}

	/*
	 * This method
	 */
	public void checkTargetItemPresence(Map<Object, List> sol) {
		for (Object o : sol.keySet()) {
			Collection mapped = sol.get(o);
			for (Object t : mapped)
				if (!problem_.getTargetItems().contains(t))
					throw new RuntimeException();
		}
	}

	public void checkExcludesConstraintConsistency(Map<Object, List> sol) {
		for (Object o : sol.keySet()) {
			Collection exc = problem_.getExcludes(o);
			Collection trg = sol.get(o);
			for (Object t : trg)
				if (exc.contains(t))
					throw new RuntimeException();
		}
	}

	public void checkRequiresConstraintConsistency(Map<Object, List> sol) {
		for (Object o : sol.keySet()) {
			Collection req = problem_.getRequires(o);
			Collection trg = sol.get(o);
			for (Object t : req)
				if (!trg.contains(t))
					throw new RuntimeException();
		}
	}

	public void checkSourceMappedInstancesConsistency(Map<Object, List> sol) {
		for (Object o : sol.keySet()) {
			Cardinality card = problem_.getSourceMappedInstancesCountMap().get(
					o);
			if (card != null) {
				Collection trg = sol.get(o);

				int total = trg.size();
				if (!(total >= card.getMin()))
					throw new RuntimeException();
				if (!(total <= card.getMax()))
					throw new RuntimeException();
			}

		}
	}

	public void checkFeasibilityConstraintConsistency(Map<Object, List> sol) {
		for (Object o : sol.keySet()) {
			Collection fes = problem_.getFeasibleTargets(o);
			if (fes != null) {
				if (fes.size() > 0) {
					Collection trg = sol.get(o);
					for (Object t : trg)
						if (!fes.contains(t))
							throw new RuntimeException();
				}
			}
		}
	}

	public void checkTargetMappedInstancesConsistency(Map<Object, List> sol) {
		for (Object o : problem_.getTargetItems()) {
			Cardinality card = problem_.getTargetMappedInstancesCountMap().get(
					o);
			int total = getTotalTargetMappings(o, sol);
			if (card != null) {
				if (!(total >= card.getMin()))
					throw new RuntimeException();
				if (!(total <= card.getMax()))
					throw new RuntimeException();
			}

		}
	}

	public int getTotalTargetMappings(Object trg, Map<Object, List> sol) {
		int count = 0;
		for (Object o : sol.keySet()) {
			Collection trgs = sol.get(o);
			if (trgs.contains(trg))
				count++;
		}
		return count;
	}

	public void assertNoDuplicateMappings(Map<Object, List> sol) {
		for (Object o : sol.keySet()) {
			Collection trgs = sol.get(o);
			HashSet set = new HashSet();
			set.addAll(trgs);
			if (!(set.size() == trgs.size()))
				throw new RuntimeException();
		}
	}

	public void checkSelectConstraintConsistency(Map<Object, List> sol) {
		for (Object o : sol.keySet()) {
			Collection sel = problem_.getSelectMap().get(o);
			if (sel != null) {
				if (sel.size() > 0) {
					Iterator iter = sel.iterator();
					Cardinality card = (Cardinality) iter.next();
					Collection trg = sol.get(o);
					int total = 0;
					while (iter.hasNext()) {
						Object so = iter.next();
						if (trg.contains(so))
							total++;
					}
					if (!(total >= card.getMin()))
						throw new RuntimeException();
					if (!(total <= card.getMax()))
						throw new RuntimeException();
				}
			}
		}
	}
}
