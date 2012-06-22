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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.conf.debug.ConstraintReference;
import org.ascent.expr.BinaryExpression;

import choco.Constraint;
import choco.Solver;
import choco.integer.IntDomainVar;
import choco.integer.IntVar;

public class RefreshVectorCore extends AbstractRefreshChocoCore {

	private Map<Object, IntDomainVar> mappingVariables_ = new HashMap<Object, IntDomainVar>();
	private Map<Object, IntDomainVar> presenceVariables_ = new HashMap<Object, IntDomainVar>();

	@Override
	public Map<Object, List> nextMapping() {
		boolean foundsol = false;

		if (!solved_ && solve()) {
			foundsol = true;
		} else if (solved_) {
			if (problem_.nextSolution() == Boolean.TRUE) {
				foundsol = true;
			}
		}

		solved_ = true;

		if (foundsol) {
			Map<Object, List> solution = new HashMap<Object, List>();
			for (Object src : sourceItems_) {
				List valid = getTargets(src);
				solution.put(src, valid);
			}
			return solution;
		}

		return null;
	}

	public void mapTo(Object src, Object trg) {
		try {
			int index = getTargetIndex(trg);
			mappingVariables_.get(src).setVal(index);
		} catch (Exception e) {
		}
	}

	@Override
	public Map<Object, List> nextMappingWithinTimeLimit(int time) {
		boolean foundsol = false;

		if (!solved_ && solve(time)) {
			foundsol = true;
		} else if (solved_) {
			if (problem_.nextSolution() == Boolean.TRUE) {
				foundsol = true;
			}
		}

		solved_ = true;

		if (foundsol) {
			Map<Object, List> solution = new HashMap<Object, List>();
			for (Object src : sourceItems_) {
				List valid = getTargets(src);
				solution.put(src, valid);
			}
			return solution;
		}

		return null;
	}

	protected boolean solve(int maxtime) {
		Solver s = getTimeLimitedSolver(maxtime);
		s.launch();
		if (s.isEncounteredLimit())
			return false;
		else
			return problem_.isFeasible();
	}

	protected boolean solve() {
		Solver s = getUnlimitedSolver();
		s.launch();
		if (s.isEncounteredLimit())
			return false;
		else
			return problem_.isFeasible();
	}

	@Override
	public void setSetsToMap(List srcs, List trgs) {
		super.setSetsToMap(srcs, trgs);

		int max = trgs.size();
		for (Object o : srcs) {
			IntDomainVar mapvar = problem_.makeBoundIntVar(o + "_mapvar", 0,
					max);
			mappingVariables_.put(o, mapvar);
		}
	}

	private ArrayList<IntDomainVar> cohostvars = new ArrayList<IntDomainVar>();

	public IntDomainVar getCoHostingVariable(Object src1, Object src2) {
		IntDomainVar s1map = getMappingVariable(src1);
		IntDomainVar s2map = getMappingVariable(src2);
		IntDomainVar cohost = problem_.makeBoundIntVar(src1 + "_cohosted_"
				+ src2, 0, 1);

		Constraint samehost = problem_.eq(s1map, s2map);
		Constraint cohosted = problem_.eq(cohost, 1);
		Constraint cohostedc = problem_.implies(samehost, cohosted);

		problem_.post(cohostedc);

		problem_.post(problem_.implies(problem_.neq(s1map, s2map), problem_.eq(
				cohost, 0)));
		problem_.post(problem_.implies(problem_.eq(cohost, 0), problem_.neq(
				s1map, s2map)));
		problem_.post(problem_.implies(problem_.eq(cohost, 1), problem_.eq(
				s1map, s2map)));

		cohostvars.add(cohost);
		return cohost;
	}

	public IntDomainVar getMappingVariable(Object src) {
		return mappingVariables_.get(src);
	}

	@Override
	public ConstraintReference addExcludesMappingConstraint(Object src,
			Object req) {
		IntDomainVar s1map = getMappingVariable(src);
		IntDomainVar s2map = getMappingVariable(req);
		Constraint con = problem_.implies(problem_.gt(s1map, 0), problem_.neq(
				s1map, s2map));

		postConstraint(src, src
				+ " must be cannot mapped to the same source as " + req, con);

		return createConstraintReference(src, src
				+ " cannot be mapped to the same source as " + req, con);
	}

	@Override
	public ConstraintReference addRequiresMappingConstraint(Object src,
			Object req) {
		IntDomainVar s1map = getMappingVariable(src);
		IntDomainVar s2map = getMappingVariable(req);
		Constraint con = problem_.implies(problem_.gt(s1map, 0), problem_.eq(
				s1map, s2map));

		postConstraint(src, src + " must be mapped to the same source as "
				+ req, con);

		return createConstraintReference(src, src
				+ " must be mapped to the same source as " + req, con);
	}

	@Override
	public ConstraintReference addSelectMappingConstraint(Object src1,
			List set, int min, int max) {

		IntDomainVar[] cohosts = new IntDomainVar[set.size()];
		for (int i = 0; i < set.size(); i++) {
			Object src2 = set.get(i);
			cohosts[i] = getCoHostingVariable(src1, src2);
		}

		Constraint con = problem_.geq(problem_.sum(cohosts), min);
		Constraint con2 = problem_.leq(problem_.sum(cohosts), max);

		postConstraint(src1, "Between [" + min + ".." + max + "] of " + set
				+ " may be mapped to the same node as " + src1, con);
		postConstraint(src1, "Between [" + min + ".." + max + "] of " + set
				+ " may be mapped to the same node as " + src1, con2);

		return createConstraintReference(src1, "Between [" + min + ".." + max
				+ "] of " + set + " may be mapped to the same node as " + src1,
				new Constraint[] { con, con2 });
	}

	@Override
	public ConstraintReference addSelectMappingConstraint(Object src1,
			List set, int exactcard) {
		IntDomainVar[] cohosts = new IntDomainVar[set.size()];
		for (int i = 0; i < set.size(); i++) {
			Object src2 = set.get(i);
			cohosts[i] = getCoHostingVariable(src1, src2);
		}

		IntDomainVar mapvar = getMappingVariable(src1);
		Constraint con = problem_.implies(problem_.gt(mapvar, 0), problem_.eq(
				problem_.sum(cohosts), exactcard));

		postConstraint(src1, "Between [" + exactcard + ".." + exactcard
				+ "] of " + set + " may be mapped to the same node as " + src1,
				con);

		return createConstraintReference(src1, "Between [" + exactcard + ".."
				+ exactcard + "] of " + set
				+ " may be mapped to the same node as " + src1, con);
	}

	@Override
	public ConstraintReference addSourceMappedCardinalityConstraint(Object src,
			int min, int max) {

		if (min > 1 || max > 1 || max != min)
			throw new UnsupportedConstraintException(
					"The RefreshVectorCore only supports source the mapped cardinality expressions [0..0] and [1..1]");

		IntDomainVar mapvar = getMappingVariable(src);

		Constraint con = null;
		if (max == 1)
			con = problem_.geq(mapvar, 0);
		else if (max == 0)
			con = problem_.eq(mapvar, 0);

		postConstraint(src, src + " must be mapped to between [" + min + ".."
				+ max + "] targets", con);

		return createConstraintReference(src, src
				+ " must be mapped to between [" + min + ".." + max
				+ "] targets", con);
	}

	@Override
	public ConstraintReference addSourceMappingConstraint(Object src,
			BinaryExpression expression) {

		throw new UnsupportedConstraintException(
				"The RefreshVectorCore does not yet support source mapping constraints");

	}

	@Override
	public ConstraintReference addTargetIntResourceConstraint(Object target,
			Object var, int val) {

		throw new UnsupportedConstraintException(
				"The RefreshVectorCore does not yet support target int resource constraints");

	}

	public void setResourceConstraints(int[][] sourceresourceconsumption,
			int[][] targetresourceavail) {
		throw new UnsupportedConstraintException(
				"The RefreshVectorCore does not yet support target int resource constraints");
	}

	@Override
	public ConstraintReference addTargetMappedSourcesCardinalityConstraint(
			Object target, int min, int max) {
		throw new UnsupportedConstraintException(
				"The RefreshVectorCore does not yet support target mapped sources constraints");

	}

	@Override
	public ConstraintReference addTargetMappedSourcesConstraint(Object target,
			int exactmapped) {
		throw new UnsupportedConstraintException(
				"The RefreshVectorCore does not yet support target mapped sources constraints");

	}

	public int getTargetIndex(Object trg) {
		for (int i = 0; i < targetItems_.size(); i++) {
			if (targetItems_.get(i).equals(trg))
				return i + 1;
		}

		return 0;
	}

	@Override
	public IntDomainVar getOrCreateMappedToVariable(Object src, Object trg) {
		IntDomainVar mapvar = getMappingVariable(src);
		int index = getTargetIndex(trg);
		IntDomainVar mapto = problem_.makeBoundIntVar(
				src + "_mapped_to_" + trg, 0, 1);
		problem_.post(problem_.implies(problem_.eq(mapvar, index), problem_.eq(
				mapto, 1)));
		problem_.post(problem_.implies(problem_.neq(mapvar, index), problem_
				.eq(mapto, 0)));
		return mapto;
	}

	@Override
	public IntVar getOrCreatePresenceVariable(Object item) {
		IntDomainVar presence = presenceVariables_.get(item);
		if (presence == null) {
			presence = problem_.makeBoundIntVar(item + "_presence", 0, 1);
			IntDomainVar mapvar = getMappingVariable(item);
			problem_.post(problem_.implies(problem_.gt(mapvar, 0), problem_.eq(
					presence, 1)));
			problem_.post(problem_.implies(problem_.eq(mapvar, 0), problem_.eq(
					presence, 0)));
		}
		return presence;
	}

	@Override
	public List getTargets(Object src) {
		return getSourceTargets(src);
	}

	@Override
	public ConstraintReference requireAllMappedExactlyOnce() {
		Constraint[] cons = new Constraint[sourceItems_.size()];

		for (int i = 0; i < sourceItems_.size(); i++) {
			Object src = sourceItems_.get(i);
			IntDomainVar mapvar = getMappingVariable(src);
			cons[i] = problem_.geq(mapvar, 1);
			postConstraint(src, src + " must be mapped to a target", cons[i]);
		}

		return createConstraintReference(null,
				"all elements must be mapped to a target", cons);
	}

	@Override
	public void setInValidTargets(Object src, List invalid) {
	}

	@Override
	public void setValidTargets(Object src, List valid) {
	}

	public Map<Object, IntVar> getTargetTable(Object src) {
		return null;
	}

	public List getSourceTargets(Object src) {
		IntDomainVar mapvar = getMappingVariable(src);
		int val = mapvar.getVal();
		ArrayList targets = new ArrayList();
		if (val > 0)
			targets.add(targetItems_.get(val - 1));
		return targets;
	}

	public ConstraintReference requireMapped(Object src) {
		IntDomainVar mapvar = getMappingVariable(src);
		Constraint con = problem_.geq(mapvar, 1);
		postConstraint(src, src + " must be mapped", con);
		return createConstraintReference(src, src
				+ " must be mapped to a target", con);
	}

	public ConstraintReference requireNotMapped(Object src) {
		IntDomainVar mapvar = getMappingVariable(src);
		Constraint con = problem_.eq(mapvar, 0);
		postConstraint(src, src + " must not be mapped", con);
		return createConstraintReference(src, src
				+ " must not be mapped to a target", con);
	}

}
