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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.choco.debug.ChocoCoreInternalConstraintDebugger;
import org.ascent.choco.debug.ChocoInternalBasicConstraintDebugger;
import org.ascent.configurator.conf.debug.Conflict;
import org.ascent.configurator.conf.debug.ConstraintReference;
import org.ascent.expr.BinaryExpression;

import choco.AbstractProblem;
import choco.Constraint;
import choco.ContradictionException;
import choco.Problem;
import choco.Solver;
import choco.integer.IntDomainVar;
import choco.integer.IntVar;
import choco.integer.search.AssignVar;
import choco.integer.search.DecreasingDomain;
import choco.integer.search.IncreasingDomain;
import choco.integer.search.MinDomain;
import choco.integer.search.StaticVarOrder;

public class RefreshMatrixCore extends AbstractRefreshChocoCore {

	public class UndefinedAttributeException extends RuntimeException {

		public UndefinedAttributeException(String arg0) {
			super(arg0);
		}

	}

	public class UndefinedSourceItemException extends RuntimeException {

		public UndefinedSourceItemException(String arg0) {
			super(arg0);
		}

	}

	public class UndefinedTargetItemException extends RuntimeException {

		public UndefinedTargetItemException(String arg0) {
			super(arg0);
		}

	}

	public class UnsupportedOperationException extends RuntimeException {

		public UnsupportedOperationException(String arg0) {
			super(arg0);
		}

	}

	public class UnsupportedExpressionType extends RuntimeException {

		public UnsupportedExpressionType(String arg0) {
			super(arg0);
		}

	}

	// use the source item to lookup a table with mappings to targets
	private Map<Object, Map<Object, IntVar>> mappingTable_ = new HashMap<Object, Map<Object, IntVar>>();



	private List requiredSources_ = new ArrayList();

	
	public void setSetsToMap(List srcs, List trgs) {
		problem_ = makeProblem();

		sourceItems_ = srcs;
		targetItems_ = trgs;
		for (Object src : srcs) {
			Map targettable = new HashMap<Object, IntVar>(19);
			for (Object trg : trgs) {
				IntDomainVar mapvar = problem_.makeBoundIntVar(
						src + "->" + trg, 0, 1);
				targettable.put(trg, mapvar);
			}
			mappingTable_.put(src, targettable);
		}
	}

	public AbstractProblem makeProblem() {
		return new Problem();
	}

	public void setSetsToMap(Object[] srcs, Object[] trgs) {
		setSetsToMap(Arrays.asList(srcs), Arrays.asList(trgs));
	}
	
	

	public void mapTo(Object src, Object trg) {
		try {
			Map ttable = getTargetTable(src);
			((IntDomainVar)ttable.get(trg)).setVal(1);
		}catch (Exception e) {}
	}

	@Override
	public IntDomainVar createObservation(Object item, boolean obs) {
		return super.createObservation(item, obs);
	}

	@Override
	public IntDomainVar createObservation(Object item) {
		return super.createObservation(item);
	}

	public List getTargets(Object src) {
		Map<Object, IntVar> targettable = getTargetTable(src);
		List valid = new ArrayList();
		for (Object trg : targettable.keySet()) {
			IntVar v = targettable.get(trg);
			if (((IntDomainVar) v).getVal() == 1) {
				valid.add(trg);
			}
		}
		return valid;
	}

	public Map<Object, IntVar> getTargetTable(Object src) {
		return mappingTable_.get(src);
	}

	public void setValidTargets(Object src, List valid) {
		Map<Object, IntVar> targettable = getTargetTable(src);
		if (targettable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");
		for (Object trg : targettable.keySet()) {
			if (!valid.contains(trg)) {
				problem_.post(problem_.eq(targettable.get(trg), 0));
			}
		}
	}

	public void setInValidTargets(Object src, List invalid) {
		Map<Object, IntVar> targettable = getTargetTable(src);
		if (targettable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");
		for (Object trg : invalid) {
			problem_.post(problem_.eq(targettable.get(trg), 0));
		}
	}

	public ConstraintReference requireMapped(Object src) {
		Map<Object, IntVar> targettable = getTargetTable(src);
		if (targettable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");
		IntVar[] vars = new IntVar[targettable.size()];
		int count = 0;
		for (Object trg : targettable.keySet()) {
			vars[count] = targettable.get(trg);
			count++;
		}
		IntDomainVar rvar = getOrCreatePresenceVariable(src);
		requiredSources_.add(src);
		Constraint con = problem_.eq(rvar, 1);
		// postConstraint(src, src+" is required but cannot be mapped", con);
		problem_.post(con);
		return createConstraintReference(src, "require mapped", con);
	}

	public ConstraintReference requireNotMapped(Object src) {
		Map<Object, IntVar> targettable = getTargetTable(src);
		if (targettable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");
		IntVar[] vars = new IntVar[targettable.size()];
		int count = 0;
		for (Object trg : targettable.keySet()) {
			vars[count] = targettable.get(trg);
			count++;
		}
		IntDomainVar pvar = getOrCreatePresenceVariable(src);
		Constraint con = problem_.eq(pvar, 0);
		// postConstraint(src, src+" cannot be mapped", con);
		problem_.post(con);
		return createConstraintReference(src, "require not mapped", con);
	}

	public ConstraintReference requireAllMappedExactlyOnce() {
		Constraint[] cons = new Constraint[mappingTable_.size()];
		int ccount = 0;
		for (Object src : mappingTable_.keySet()) {
			Map<Object, IntVar> targettable = getTargetTable(src);
			IntVar[] vars = new IntVar[targettable.size()];
			int count = 0;
			for (Object trg : targettable.keySet()) {
				vars[count] = targettable.get(trg);
				count++;
			}
			Constraint con = problem_.eq(problem_.sum(vars), 1);

			cons[ccount] = con;
			ccount++;
			problem_.post(con);
		}
		return createConstraintReference(null, "all mapped exactly once", cons);
	}

	public ConstraintReference addSourceMappedCardinalityConstraint(Object src,
			int min, int max) {
		Map<Object, IntVar> targettable = getTargetTable(src);
		if (targettable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");
		IntVar[] vars = new IntVar[targettable.size()];
		int count = 0;
		for (Object trg : targettable.keySet()) {
			vars[count] = targettable.get(trg);
			count++;
		}

		Constraint con = problem_.geq(problem_.sum(vars), min);
		Constraint con2 = problem_.leq(problem_.sum(vars), max);
		Constraint[] cons = new Constraint[] { con, con2 };
		problem_.post(con);
		problem_.post(con2);
		return createConstraintReference(src, "source mapped cardinality src:"
				+ src + " [" + min + ".." + max + "]", cons);
	}

	public ConstraintReference addTargetMappedSourcesCardinalityConstraint(
			Object target, int min, int max) {
		IntVar[] vars = new IntVar[mappingTable_.size()];

		int count = 0;
		for (Object src : mappingTable_.keySet()) {
			IntVar v = mappingTable_.get(src).get(target);
			vars[count] = v;
			count++;
		}

		Constraint con = problem_.leq(problem_.sum(vars), max);
		Constraint con2 = problem_.geq(problem_.sum(vars), min);
		Constraint[] cons = new Constraint[] { con, con2 };
		problem_.post(con);
		problem_.post(con2);
		return createConstraintReference(target,
				"target mapped cardinality src:" + target + " [" + min + ".."
						+ max + "]", cons);
	}

	public ConstraintReference addTargetMappedSourcesConstraint(Object target,
			int exactmapped) {
		IntVar[] vars = new IntVar[mappingTable_.size()];

		int count = 0;
		for (Object src : mappingTable_.keySet()) {
			IntVar v = mappingTable_.get(src).get(target);
			vars[count] = v;
			count++;
		}
		Constraint con = problem_.eq(problem_.sum(vars), exactmapped);
		problem_.post(con);
		return createConstraintReference(target,
				"target mapped sources target:" + target + " [" + exactmapped
						+ "]", con);
	}

	public ConstraintReference addTargetIntResourceConstraint(Object target,
			Object var, int val) {

		IntVar[] dvars = new IntVar[mappingTable_.size()];
		int count = 0;
		getTargetVariableValues(target).put(var, val);
		for (Object src : mappingTable_.keySet()) {
			IntVar v = mappingTable_.get(src).get(target);
			Integer value = (Integer) (sourceVariableValuesTable_.get(src))
					.get(var);
			int d = (value != null) ? value.intValue() : 0;
			IntVar dvar = problem_.makeBoundIntVar("" + src + "->" + target
					+ "(demands_of)->" + var, 0, d);
			problem_.post(problem_.eq(dvar, problem_.mult(d, v)));
			dvars[count] = dvar;
			count++;
		}
		Constraint con = problem_.leq(problem_.sum(dvars), val);
		problem_.post(con);
		return createConstraintReference(target,
				"target resource constraint target:" + target + " resource:"
						+ var + " val:" + val, con);
	}
	
	

	public void setResourceConstraints(int[][] sourceresourceconsumption,
			int[][] targetresourceavail) {
		throw new UnsupportedConstraintException("This constraint method has not been implemented yet.");
	}

	public ConstraintReference addSourceIntResourceRequirement(Object source,
			Object var, int val) {

		getSourceVariableValues(source).put(var, val);
		Map<Object, IntVar> tvars = mappingTable_.get(source);
		Constraint[] cons = new Constraint[tvars.size()];
		int count = 0;
		for (Object trg : tvars.keySet()) {
			IntVar v = tvars.get(trg);
			Integer value = (Integer) (targetVariableValuesTable_.get(trg))
					.get(var);
			int d = (value != null) ? value.intValue() : 0;

			Constraint con = problem_.geq(problem_.mult(val, v), d);
			problem_.post(con);
			cons[count] = con;
			count++;
		}

		return createConstraintReference(source, "source resource requirement:"
				+ source + " resource:" + var + " val:" + val, cons);
	}

	public ConstraintReference addRequiresMappingConstraint(Object src,
			Object req) {
		Map<Object, IntVar> stable = getTargetTable(src);
		Map<Object, IntVar> ttable = getTargetTable(req);

		if (stable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");
		if (ttable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ req
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

		List<Constraint> cons = new ArrayList<Constraint>();
		for (Object o : stable.keySet()) {
			IntVar sv = stable.get(o);
			IntVar tv = ttable.get(o);
			Constraint c = problem_.implies(problem_.eq(sv, 1), problem_.eq(tv,
					1));
			postConstraint(src, src + " requires " + req + " but " + req
					+ " can't be selected", c);
			cons.add(c);
		}
		return createConstraintReference(src, src + " requires " + req, cons
				.toArray(new Constraint[0]));
	}

	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int min, int max) {
		Map<Object, IntVar> stable = getTargetTable(src);

		if (stable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

		List<Constraint> cons = new ArrayList<Constraint>();
		for (Object o : stable.keySet()) {
			IntVar[] rvars = new IntVar[set.size()];
			for (int i = 0; i < set.size(); i++) {
				Object r = set.get(i);
				Map<Object, IntVar> ttable = getTargetTable(r);

				if (ttable == null)
					throw new UndefinedSourceItemException(
							"The source item "
									+ r
									+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

				if (ttable == null)
					throw new UnsolvableException("Cannot require that " + src
							+ " select [" + min + ".." + max + "] of " + set
							+ " b/c " + r + " is not a source feature.");
				rvars[i] = ttable.get(o);
			}
			IntVar sv = stable.get(o);

			Constraint c1 = problem_.implies(problem_.eq(sv, 1), problem_.geq(
					problem_.sum(rvars), min));

			Constraint c2 = problem_.implies(problem_.eq(sv, 1), problem_.leq(
					problem_.sum(rvars), max));

			Constraint combined = problem_.and(c1, c2);

			postConstraint(src, src + " is mapped with more/less than [" + min
					+ ".." + max + "] of " + set, combined);
			cons.add(combined);
		}
		return createConstraintReference(src, src + " select [" + min + ".."
				+ max + "] of " + set, cons.toArray(new Constraint[0]));
	}

	public ConstraintReference addSelectMappingConstraint(Object src, List set,
			int exactcard) {
		Map<Object, IntVar> stable = getTargetTable(src);

		if (stable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

		List<Constraint> cons = new ArrayList<Constraint>();
		for (Object o : stable.keySet()) {
			IntVar[] rvars = new IntVar[set.size()];
			for (int i = 0; i < set.size(); i++) {
				Object r = set.get(i);
				Map<Object, IntVar> ttable = getTargetTable(r);

				if (ttable == null)
					throw new UndefinedSourceItemException(
							"The source item "
									+ r
									+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

				rvars[i] = ttable.get(o);
			}
			IntVar sv = stable.get(o);
			Constraint con = problem_.implies(problem_.eq(sv, 1), problem_.eq(
					problem_.sum(rvars), exactcard));
			postConstraint(src, src + " allows between [" + exactcard + ".."
					+ exactcard + "] of " + set + " to be selected", con);
			cons.add(con);
		}
		return createConstraintReference(src, src + " select [" + exactcard
				+ ".." + exactcard + "] of " + set, cons
				.toArray(new Constraint[0]));
	}

	public ConstraintReference addExcludesMappingConstraint(Object src,
			Object req) {
		Map<Object, IntVar> stable = getTargetTable(src);
		Map<Object, IntVar> ttable = getTargetTable(req);

		if (stable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");
		if (ttable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ req
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

		List<Constraint> cons = new ArrayList<Constraint>();
		for (Object o : stable.keySet()) {
			IntVar sv = stable.get(o);
			IntVar tv = ttable.get(o);
			Constraint con = problem_.implies(problem_.eq(sv, 1), problem_.eq(
					tv, 0));
			postConstraint(src, src + " excludes " + req
					+ " from being selected but " + req + " is selected", con);
			cons.add(con);
		}
		return createConstraintReference(src, src + " excludes " + req, cons
				.toArray(new Constraint[0]));
	}

	public ConstraintReference addSourceMappingConstraint(Object src,
			BinaryExpression expression) {
		Map<Object, IntVar> targettable = getTargetTable(src);

		if (targettable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

		List<Constraint> cons = new ArrayList<Constraint>();
		for (Object o : targettable.keySet()) {
			IntVar mapvar = targettable.get(o);
			Constraint con = getSourceMappingConstraint(src, o, mapvar,
					expression);
			problem_.post(con);
			cons.add(con);
		}
		return createConstraintReference(src, src
				+ " source mapping constraint " + expression, cons
				.toArray(new Constraint[0]));
	}

	

	public IntDomainVar getOrCreatePresenceVariable(Object item) {
		IntDomainVar pvar = (IntDomainVar) variableTable_.get(PRESENCE_PREFIX
				+ item);
		if (pvar == null) {
			pvar = problem_.makeBoundIntVar(PRESENCE_PREFIX + item, 0, 1);

			IntVar[] vars = null;
			if (mappingTable_.get(item) != null) {
				Map<Object, IntVar> targettable = getTargetTable(item);
				vars = new IntVar[targettable.size()];
				int count = 0;
				for (Object key : targettable.keySet()) {
					vars[count] = targettable.get(key);
					count++;
				}
			} else {
				vars = new IntVar[mappingTable_.size()];
				int count = 0;
				for (Object key : mappingTable_.keySet()) {
					vars[count] = mappingTable_.get(key).get(item);
					count++;
				}
			}
			problem_.post(problem_.implies(problem_.gt(problem_.sum(vars), 0),
					problem_.eq(pvar, 1)));
			problem_.post(problem_.implies(problem_.eq(pvar, 1), problem_.gt(
					problem_.sum(vars), 0)));
			variableTable_.put(PRESENCE_PREFIX + item, pvar);
		}
		return pvar;
	}

	@Override
	public IntDomainVar getOrCreateMappedToVariable(Object src, Object trg) {
		Map<Object, IntVar> targettable = getTargetTable(src);

		if (targettable == null)
			throw new UndefinedSourceItemException(
					"The source item "
							+ src
							+ " is not defined yet and thus a source cardinality constraint cannot be bound to it.");

		return (IntDomainVar) targettable.get(trg);
	}

	public List getSourceTargets(Object src) {
		ArrayList result = new ArrayList();
		Map<Object, IntVar> ttable = getTargetTable(src);
		for (Object o : ttable.keySet()) {
			if (((IntDomainVar) ttable.get(o)).getVal() == 1) {
				result.add(o);
			}
		}
		return result;
	}
}
