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

import org.ascent.choco.debug.ChocoInternalBasicConstraintDebugger;
import org.ascent.configurator.conf.debug.ConstraintReference;
import org.ascent.expr.BinaryExpression;
import org.ascent.expr.ConstantIntegerExpression;
import org.ascent.expr.Expression;
import org.ascent.expr.MappedToExpression;
import org.ascent.expr.SourceVariableSumExpression;
import org.ascent.expr.SourceVariableValueExpression;
import org.ascent.expr.TargetVariableValueExpression;
import org.ascent.expr.VariableSumExpression;
import org.ascent.expr.VariableValueExpression;
import org.ascent.util.ParsingUtil;

import choco.AbstractProblem;
import choco.Constraint;
import choco.Problem;
import choco.Solver;
import choco.Var;
import choco.integer.IntDomainVar;
import choco.integer.IntExp;
import choco.integer.IntVar;


public abstract class AbstractRefreshChocoCore extends AbstractRefreshCore implements ChocoRefreshCore {

	public class ChocoConstraintReference implements ConstraintReference {
		private Constraint[] constraints_;
		private String id_;
		private Object context_;

		public ChocoConstraintReference(String id, Constraint constraint) {
			this(id, null, constraint);
		}

		public ChocoConstraintReference(String id, Object context,
				Constraint constraint) {
			super();
			id_ = id;
			context_ = context;
			constraints_ = new Constraint[] { constraint };
		}

		public ChocoConstraintReference(String id, Object context,
				Constraint[] constraints) {
			super();
			id_ = id;
			context_ = context;
			constraints_ = constraints;
		}

		public Constraint[] getConstraints() {
			return constraints_;
		}

		public void setConstraints(Constraint[] constraints) {
			constraints_ = constraints;
		}

		public String getId() {
			return id_;
		}

		public void setId(String id) {
			id_ = id;
		}

		public Object getContext() {
			return context_;
		}

		public void setContext(Object context) {
			context_ = context;
		}

		public String toString(){
			return id_;
		}
	}

	

	protected boolean solved_ = false;
	
	private boolean debug_ = false;

	protected AbstractProblem problem_;

	protected List sourceItems_ = new ArrayList();

	protected List targetItems_ = new ArrayList();

	protected Map<Object, Var> variableTable_ = new HashMap<Object, Var>();
	
	protected Map<Object,List> lastMapping_ = null;

	protected Map<Object, Map<Object, IntDomainVar>> contextVariables_ = new HashMap<Object, Map<Object, IntDomainVar>>();

	protected Map<Object, Map<Object, Var>> targetVariableTable_ = new HashMap<Object, Map<Object, Var>>();

	protected Map<Object, Map<Object, Var>> sourcetVariableTable_ = new HashMap<Object, Map<Object, Var>>();

	protected Map<Object, IntDomainVar> selectVariables_ = new HashMap<Object, IntDomainVar>();
	
	protected Map<Object, IntDomainVar> deSelectVariables_ = new HashMap<Object, IntDomainVar>();
	
	protected Map<Object, IntDomainVar> observationVariables_ = new HashMap<Object, IntDomainVar>();
	
	protected Map<Constraint, ConstraintReference> constraintReferences_ = new HashMap<Constraint, ConstraintReference>();

	
	private boolean useBranchAndBound_ = false;

	private IntExp optimizationFunction_;

	private boolean maximizeOptimizationFunction_ = true;
	
	protected Map<Object, List<IntDomainVar>> errorBitsBySource_ = new HashMap<Object, List<IntDomainVar>>();
	protected Map<IntDomainVar, ChocoInternalBasicConstraintDebugger> debuggers_ = new HashMap<IntDomainVar, ChocoInternalBasicConstraintDebugger>();
	protected List<IntDomainVar> errorBits_ = new ArrayList<IntDomainVar>();

	

	/**
	 * @return the maximizeOptimizationFunction
	 */
	public boolean getMaximizeOptimizationFunction() {
		return maximizeOptimizationFunction_;
	}

	/**
	 * @param maximizeOptimizationFunction
	 *            the maximizeOptimizationFunction to set
	 */
	public void setMaximizeOptimizationFunction(
			boolean maximizeOptimizationFunction) {
		maximizeOptimizationFunction_ = maximizeOptimizationFunction;
	}

	/**
	 * @return the optimizationFunction
	 */
	public IntExp getOptimizationFunction() {
		return optimizationFunction_;
	}

	/**
	 * @param optimizationFunction
	 *            the optimizationFunction to set
	 */
	public void setOptimizationFunction(Expression exp) {
		optimizationFunction_ = getIntExpr(null, null, exp);
	}

	public void setOptimizationFunction(IntExp optimizationFunction) {
		optimizationFunction_ = optimizationFunction;
	}

	/**
	 * @return the useBranchAndBound
	 */
	public boolean getUseBranchAndBound() {
		return useBranchAndBound_;
	}

	/**
	 * @param useBranchAndBound
	 *            the useBranchAndBound to set
	 */
	public void setUseBranchAndBound(boolean useBranchAndBound) {
		useBranchAndBound_ = useBranchAndBound;
	}

	public void setSetsToMap(List srcs, List trgs) {
		problem_ = new Problem();

		sourceItems_ = srcs;
		targetItems_ = trgs;
	}

	public void setSetsToMap(Object[] srcs, Object[] trgs) {
		setSetsToMap(Arrays.asList(srcs), Arrays.asList(trgs));
	}

	public Object getVariableValue(String var) {
		return ((IntDomainVar) variableTable_.get(var)).getVal();
	}

	public Object getVariableValue(Object ctx, String var) {
		return ((IntDomainVar) getContextVariables(ctx).get(var)).getVal();
	}


	protected Solver getTimeLimitedSolver(int maxtime) {
		Solver solver = getSolver();
		solver.setTimeLimit(maxtime);
		solver.generateSearchSolver(problem_);
		return solver;
	}

	protected Solver getNodeLimitedSolver(int maxnodes) {
		Solver solver = getSolver();
		solver.setNodeLimit(maxnodes);
		solver.generateSearchSolver(problem_);
		return solver;
	}

	protected Solver getUnlimitedSolver() {
		Solver s = getSolver();
		s.generateSearchSolver(problem_);
		return s;
	}

	protected Solver getSolver() {
		Solver solver = problem_.getSolver();
		solver.setDoMaximize(maximizeOptimizationFunction_);
		if(optimizationFunction_ != null){
			IntVar v = problem_.makeBoundIntVar("opt__criteria", -100000, 100000);
			problem_.post(problem_.eq(v, optimizationFunction_));
			solver.setObjective(v);
			solver.setRestart(useBranchAndBound_);
		}
		solver.setFirstSolution(optimizationFunction_ == null);

		return solver;
	}

	public Map<Object, List> nextMapping() {
		lastMapping_ = nextMapping(getUnlimitedSolver());
		return lastMapping_;
	}

	public Map<Object, List> nextMappingWithinTimeLimit(int time) {
		lastMapping_ = nextMapping(getTimeLimitedSolver(time));
		return lastMapping_;
	}

	public Map<Object, List> nextMappingWithinNodeLimit(int nodes) {
		lastMapping_ =  nextMapping(getNodeLimitedSolver(nodes));
		return lastMapping_;
	}

	
	
	public Map<Object, List> getLastMapping() {
		return lastMapping_;
	}

	public void setLastMapping(Map<Object, List> lastMapping) {
		lastMapping_ = lastMapping;
	}

	public Map<Object, List> nextMapping(Solver s) {
		boolean foundsol = false;

		if (!solved_) {
			s.launch();
			if (s.isEncounteredLimit())
				foundsol = false;
			else
				foundsol = problem_.isFeasible();
		}
		
		// if (optimizationFunction_ != null) {
		// if (solved_) {
		// return null;
		// }
		//
		// solved_ = true;
		// IntVar v = problem_.makeBoundIntVar("opt__criteria", -100000,
		// 100000);
		// problem_.post(problem_.eq(v, optimizationFunction_));
		// if (maximizeOptimizationFunction_) {
		// foundsol = problem_.maximize(v, useBranchAndBound_);
		// } else {
		// foundsol = problem_.minimize(v, useBranchAndBound_);
		// }
		// } else {
		// if (!solved_) {
		// solved_ = true;
		// foundsol = problem_.solve();
		// } else {
		// foundsol = problem_.nextSolution();
		// }
		// }
		if (foundsol) {
			Map<Object, List> solution = new HashMap<Object, List>();
			for (Object src : sourceItems_) {
				List valid = getTargets(src);
				solution.put(src, valid);
			}
			return solution;
		} else {
			return null;
		}
	}
	
	public List<ConstraintReference> debug() {

		IntDomainVar errors = problem_.makeBoundIntVar("total errors", 0,
				errorBits_.size());
		problem_.post(problem_.eq(errors, problem_.sum(errorBits_
				.toArray(new IntDomainVar[0]))));
		setOptimizationFunction(errors);
		setMaximizeOptimizationFunction(false);
		nextMapping();

		System.out.println("Total Errors " + errors.getVal());

		ArrayList<ConstraintReference> result = new ArrayList<ConstraintReference>(
				errorBits_.size());
		for (Object o : sourceItems_) {
			// result.add(new Conflict(o,o+" was required"));
			List<IntDomainVar> bits = getErrorBitsForSource(o);
			boolean diagnose = false;
			for (IntDomainVar d : bits) {
				if (d.getVal() == 1) {
					diagnose = true;
					break;
				}
			}
			if (diagnose) {
				System.out.println(o
						+ " was a required item but could not be mapped b/c");
				for (IntDomainVar d : bits) {

					if (d.getVal() == 1) {
						ChocoInternalBasicConstraintDebugger debugger = debuggers_
								.get(d);
						Constraint con = debugger.getConstraint();
						ConstraintReference ref = constraintReferences_
								.get(con);
						result.add(ref);
					}

				}
			}
		}

		return result;
	}

	public abstract List getTargets(Object src);

	public Constraint getSourceMappingConstraint(Object src, Object trg,
			IntVar mapvar, BinaryExpression expr) {
		Constraint c = problem_.eq(mapvar, 1);
		return problem_.implies(c, getConstraint(src, trg, expr
				.getLeftHandSide(), expr.getRightHandSide(), expr
				.getOperation()));
	}

	public Constraint getConstraint(Expression lhs, Expression rhs, String op) {

		if (op.equals("=")) {
			return problem_.eq(getIntExpr(lhs), getIntExpr(rhs));
		} else if (op.equals("!=")) {
			return problem_.neq(getIntExpr(lhs), getIntExpr(rhs));
		} else if (op.equals("<")) {
			return problem_.lt(getIntExpr(lhs), getIntExpr(rhs));
		} else if (op.equals("=<")) {
			return problem_.leq(getIntExpr(lhs), getIntExpr(rhs));
		} else if (op.equals(">")) {
			return problem_.gt(getIntExpr(lhs), getIntExpr(rhs));
		} else if (op.equals(">=")) {
			return problem_.geq(getIntExpr(lhs), getIntExpr(rhs));
		} else {
			throw new UnsupportedOperationException("The operation \"" + op
					+ "\" is not supported.");
		}
	}

	public Constraint getConstraint(Object src, Object trg, Expression lhs,
			Expression rhs, String op) {

		if (op.equals("=")) {
			return problem_.eq(getIntExpr(src, trg, lhs), getIntExpr(src, trg,
					rhs));
		} else if (op.equals("!=")) {
			return problem_.neq(getIntExpr(src, trg, lhs), getIntExpr(src, trg,
					rhs));
		} else if (op.equals("<")) {
			return problem_.lt(getIntExpr(src, trg, lhs), getIntExpr(src, trg,
					rhs));
		} else if (op.equals("=<")) {
			return problem_.leq(getIntExpr(src, trg, lhs), getIntExpr(src, trg,
					rhs));
		} else if (op.equals(">")) {
			return problem_.gt(getIntExpr(src, trg, lhs), getIntExpr(src, trg,
					rhs));
		} else if (op.equals(">=")) {
			return problem_.geq(getIntExpr(src, trg, lhs), getIntExpr(src, trg,
					rhs));
		} else {
			throw new UnsupportedOperationException("The operation \"" + op
					+ "\" is not supported.");
		}
	}

	public void setIntVariableValue(Object variable, int value) {
		IntVar var = problem_.makeConstantIntVar(value);
		variableTable_.put(variable, var);
	}

	public void createIntVariable(Object variable, String op, Expression exp) {
		IntVar var = problem_.makeBoundIntVar("" + variable, -1000, 1000);
		variableTable_.put(variable, var);
		problem_.post(problem_.eq(var, getIntExpr(null, null, exp)));
	}

	public void createIntVariable(Object ctx, Object variable, String op,
			Expression exp) {
		IntDomainVar var = problem_.makeBoundIntVar("" + ctx + "." + variable,
				-1000, 1000);
		variableTable_.put(variable, var);
		getContextVariables(ctx).put("" + variable, var);
		problem_.post(problem_.eq(var, getIntExpr(null, null, exp)));
	}

	public Map<Object, IntDomainVar> getContextVariables(Object ctx) {
		Map<Object, IntDomainVar> ctxvars = contextVariables_.get(ctx);
		if (ctxvars == null) {
			ctxvars = new HashMap<Object, IntDomainVar>();
			contextVariables_.put(ctx, ctxvars);
		}
		return ctxvars;
	}

	public Map getContextVariableValues(Object ctx) {
		Map<Object, IntDomainVar> ctxvars = getContextVariables(ctx);
		HashMap result = new HashMap(ctxvars.size());
		for (Object key : ctxvars.keySet()) {
			result.put(key, ctxvars.get(key).getVal());
		}
		return result;
	}

	public IntExp getIntExpr(Object src, Object trg, Expression exp) {
		if (exp instanceof BinaryExpression) {
			BinaryExpression bexp = (BinaryExpression) exp;
			Expression lhs = bexp.getLeftHandSide();
			Expression rhs = bexp.getRightHandSide();
			String op = bexp.getOperation();
			return getIntExpr(getIntExpr(src, trg, lhs), getIntExpr(src, trg,
					rhs), op);
		} else {
			if (exp instanceof VariableValueExpression) {
				Object var = ((VariableValueExpression) exp).getVariable();
				if (exp instanceof SourceVariableValueExpression) {
					return getOrCreateIntVariableValue(src, var,
							sourcetVariableTable_, sourceVariableValuesTable_);
				} else if (exp instanceof TargetVariableValueExpression) {
					return getOrCreateIntVariableValue(trg, var,
							targetVariableTable_, targetVariableValuesTable_);
				} else {
					return (IntExp) variableTable_
							.get(((VariableValueExpression) exp).getVariable());
				}
			} else if (exp instanceof VariableSumExpression) {
				Object var = ((VariableSumExpression) exp).getVariable();
				if (exp instanceof SourceVariableSumExpression) {
					return getOrCreateSummation("src_", var,
							sourcetVariableTable_, sourceVariableValuesTable_);
				} else {
					return getOrCreateSummation("trg_", var,
							targetVariableTable_, targetVariableValuesTable_);
				}
			} else if (exp instanceof ConstantIntegerExpression) {
				return problem_
						.makeConstantIntVar(((ConstantIntegerExpression) exp)
								.getValue());
			} else if (exp instanceof MappedToExpression){
				MappedToExpression mexp = (MappedToExpression)exp;
				return getOrCreateMappedToVariable(mexp.getSource(),mexp.getTarget());
			} else {
				throw new UnsupportedExpressionType("The \"" + exp
						+ "\" is not a known expression type.");
			}
		}
	}

	public IntExp getOrCreateSummation(Object key, Object var,
			Map<Object, Map<Object, Var>> avals,
			Map<Object, Map<Object, Object>> vals) {
		IntDomainVar idv = (IntDomainVar) variableTable_.get(key + SUM_PREFIX
				+ var);

		if (idv == null) {
			int count = 0;
			IntExp[] valvars = new IntExp[vals.keySet().size()];
			for (Object item : vals.keySet()) {
				IntVar pvar = getOrCreatePresenceVariable(item);
				int dval = getValue(item, var, vals);

				IntVar dvar = problem_.makeBoundIntVar(key + "demand_of_"
						+ item + "_" + var, 0, dval);
				problem_.post(problem_.eq(dvar, problem_.mult(dval, pvar)));
				valvars[count] = dvar;
				count++;
			}
			idv = problem_.makeBoundIntVar(key + SUM_PREFIX + var, -100000,
					100000);
			problem_.post(problem_.eq(idv, problem_.sum(valvars)));
			variableTable_.put(key + SUM_PREFIX + var, idv);
		}
		return idv;
	}

	public int getValue(Object item, Object var,
			Map<Object, Map<Object, Object>> vals) {

		Map valtable = vals.get(item);
		if (valtable == null) {
			valtable = new HashMap();
			vals.put(item, valtable);
		}

		Object val = valtable.get(var);
		if (val == null && !getZeroUndefinedVariables()) {
			throw new UndefinedAttributeException("The attribute \"" + var
					+ "\" has not been defined for the item:\"" + item + "\".");
		}
		else if(val == null){
			val = 0;
		}
		int ival = (val != null && val instanceof Integer) ? ((Integer) val)
				.intValue() : ParsingUtil.toInt(val);
		return ival;
	}
	
	public int getValue(Object item, Object var,
			Map<Object, Map<Object, Object>> vals, int defval) {

		Map valtable = vals.get(item);
		if (valtable == null) {
			valtable = new HashMap();
			vals.put(item, valtable);
		}

		Object val = valtable.get(var);
		int ival = (val != null && val instanceof Integer) ? ((Integer) val)
				.intValue() : defval;
		return ival;
	}

	public abstract IntVar getOrCreatePresenceVariable(Object item);

	public IntVar getOrCreateIntVariableValue(Object item, Object var,
			Map<Object, Map<Object, Var>> avals,
			Map<Object, Map<Object, Object>> vals) {
		Map valvtable = avals.get(item);
		if (valvtable == null) {
			valvtable = new HashMap();
			avals.put(item, valvtable);
		}
		IntVar v = (IntVar) valvtable.get(var);
		if (v == null) {

			int ival = getValue(item, var, vals);
			v = problem_.makeConstantIntVar(ival);
			avals.get(item).put(var, v);
		}
		return v;
	}

	public IntExp getIntExpr(Expression exp) {
		return getIntExpr(null,null,exp);
	}

	public IntExp getIntExpr(IntExp lhs, IntExp rhs, String op) {
		if (op.equals("+")) {
			return problem_.plus(lhs, rhs);
		} else if (op.equals("-")) {
			return problem_.minus(lhs, rhs);
		} else if(op.equals("*")){
			IntVar var = problem_.makeBoundIntVar(lhs+"*"+rhs, -10000, 10000);
			IntVar a = problem_.makeBoundIntVar(lhs+"_val", -10000, 10000);
			problem_.post(problem_.eq(a, lhs));
			IntVar b= problem_.makeBoundIntVar(rhs+"_val", -10000, 10000);
			problem_.post(problem_.eq(b, rhs));
			problem_.times(var,a,b);	
			return var;
		} else {
			throw new UnsupportedOperationException("The operation \"" + op
					+ "\" is not supported.");
		}
	}
	
	public void postConstraint(Object src, String desc, Constraint con) {
		if (debug_) {
			ChocoInternalBasicConstraintDebugger debugger = new ChocoInternalBasicConstraintDebugger(
					this, problem_, src, "" + src + " " + con, desc, con);
			IntDomainVar dvar = debugger.getErrorBit();
			debuggers_.put(dvar, debugger);
			errorBits_.add(dvar);
			getErrorBitsForSource(src).add(dvar);
			problem_.post(problem_.or(con, problem_.eq(dvar, 1)));
		} else {
			problem_.post(con);
		}
	}
	
	public List<IntDomainVar> getErrorBitsForSource(Object src) {
		List<IntDomainVar> bits = errorBitsBySource_.get(src);
		if (bits == null) {
			bits = new ArrayList<IntDomainVar>();
			errorBitsBySource_.put(src, bits);
		}
		return bits;
	}

	protected ConstraintReference createConstraintReference(Object ctx,
			String id, Constraint con) {
		return createConstraintReference(ctx, id, new Constraint[]{con});
	}
	
	protected ConstraintReference createConstraintReference(Object ctx,
			String id, Constraint[] cons) {
		ChocoConstraintReference ref = new ChocoConstraintReference(id, ctx,
				cons);
		for(Constraint con : cons)
			constraintReferences_.put(con, ref);
		return ref;
	}
	
	public void debugObservations() {

		IntDomainVar[] dvars = new IntDomainVar[2 * sourceItems_.size()];
		for(int i = 0; i < sourceItems_.size(); i++){
			dvars[i] = selectVariables_.get(sourceItems_.get(i));
			if(dvars[i] == null){
				dvars[i] = problem_.makeBoundIntVar("select_"+sourceItems_.get(i), 0,1);
			}
		}
		for(int i = 0; i < sourceItems_.size(); i++){
			dvars[sourceItems_.size() + i] = deSelectVariables_.get(sourceItems_.get(i));
			if(dvars[sourceItems_.size() + i] == null){
				dvars[sourceItems_.size() + i] = problem_.makeBoundIntVar("deselect_"+sourceItems_.get(i), 0,1);
			}
		}
		IntDomainVar goal = problem_.makeBoundIntVar("goal", 0, sourceItems_.size());
		problem_.post(problem_.eq(goal, problem_.sum(dvars)));
		
		setOptimizationFunction(goal);
		setMaximizeOptimizationFunction(false);
		lastMapping_ = nextMapping();
	}
	
	public void debugObservations(int maxchanges) {

		IntDomainVar[] dvars = new IntDomainVar[2 * sourceItems_.size()];
		for(int i = 0; i < sourceItems_.size(); i++){
			dvars[i] = selectVariables_.get(sourceItems_.get(i));
			if(dvars[i] == null){
				dvars[i] = problem_.makeBoundIntVar("select_"+sourceItems_.get(i), 0,1);
			}
		}
		for(int i = 0; i < sourceItems_.size(); i++){
			dvars[sourceItems_.size() + i] = deSelectVariables_.get(sourceItems_.get(i));
			if(dvars[sourceItems_.size() + i] == null){
				dvars[sourceItems_.size() + i] = problem_.makeBoundIntVar("deselect_"+sourceItems_.get(i), 0,1);
			}
		}
		//IntDomainVar goal = problem_.makeBoundIntVar("goal", 0, sourceItems_.size());
		problem_.post(problem_.lt(problem_.sum(dvars),maxchanges));
		
		
		lastMapping_ = nextMapping();
	}
	
	public boolean debugObservations(int maxchanges, int maxtime) {

		IntDomainVar[] dvars = new IntDomainVar[2 * sourceItems_.size()];
		for(int i = 0; i < sourceItems_.size(); i++){
			dvars[i] = selectVariables_.get(sourceItems_.get(i));
			if(dvars[i] == null){
				dvars[i] = problem_.makeBoundIntVar("select_"+sourceItems_.get(i), 0,1);
			}
		}
		for(int i = 0; i < sourceItems_.size(); i++){
			dvars[sourceItems_.size() + i] = deSelectVariables_.get(sourceItems_.get(i));
			if(dvars[sourceItems_.size() + i] == null){
				dvars[sourceItems_.size() + i] = problem_.makeBoundIntVar("deselect_"+sourceItems_.get(i), 0,1);
			}
		}
		//IntDomainVar goal = problem_.makeBoundIntVar("goal", 0, sourceItems_.size());
		problem_.post(problem_.lt(problem_.sum(dvars),maxchanges));
		
		
		lastMapping_ = nextMappingWithinTimeLimit(maxtime);
		
		return lastMapping_ != null;
	}
	
	public IntDomainVar createObservation(Object item, boolean obs){
		int oi = (obs) ? 1 : 0;
		IntDomainVar ovar = createObservation(item);
		problem_.post(problem_.eq(ovar, oi));
		
		return ovar;
	}
	
	public IntDomainVar createObservation(Object item){
		IntVar presvar = getOrCreatePresenceVariable(item);
		IntDomainVar selvar = problem_.makeBoundIntVar(item+"_needs_to_be_selected", 0, 1);
		IntDomainVar deselvar = problem_.makeBoundIntVar(item+"_needs_to_be_deselected", 0, 1);
		IntDomainVar ovar = problem_.makeBoundIntVar(item+"_observation", 0, 1);
		
		observationVariables_.put(item, ovar);
		
		initSelectVariable(item, presvar, ovar, selvar, deselvar);
		initDeSelectVariable(item, presvar, ovar, selvar, deselvar);
		
		return ovar;
	}
	
	public void initSelectVariable(Object item, IntVar presvar, IntDomainVar ovar, IntDomainVar selvar, IntDomainVar deselvar){
		selectVariables_.put(item,selvar);
		
		//fi = 0 -> Si = 0
		problem_.post(problem_.implies(problem_.eq(presvar, 0),problem_.eq(selvar, 0)));
		
		//fi = 1 -> Oi xor Si
		problem_.implies(problem_.eq(selvar,1),problem_.eq(ovar,0));
		problem_.implies(problem_.eq(ovar,1), problem_.eq(selvar, 0));
		Constraint c1 = problem_.and(problem_.eq(ovar, 1), problem_.eq(selvar, 0));
		Constraint c2 = problem_.and(problem_.eq(ovar, 0), problem_.eq(selvar, 1));
		Constraint c3 = problem_.implies(problem_.eq(presvar,1),problem_.or(c1,c2));
		problem_.post(c3);
	}
	
	public void initDeSelectVariable(Object item, IntVar presvar, IntDomainVar ovar, IntDomainVar selvar, IntDomainVar deselvar){
		deSelectVariables_.put(item,deselvar);
		
		//fi = 1 -> Di = 0
		problem_.post(problem_.implies(problem_.eq(presvar, 1),problem_.eq(deselvar, 0)));
		
		//fi = 0 -> Oi xor Di
		problem_.implies(problem_.eq(deselvar,1),problem_.eq(ovar,1));
		problem_.implies(problem_.eq(ovar,0), problem_.eq(deselvar, 0));
		Constraint c1 = problem_.and(problem_.eq(ovar, 0), problem_.eq(deselvar, 0));
		Constraint c2 = problem_.and(problem_.eq(ovar, 1), problem_.eq(deselvar, 1));
		Constraint c3 = problem_.implies(problem_.eq(presvar,0),problem_.or(c1,c2));
		problem_.post(c3);
	}
	
	public boolean shouldSelect(Object item){
		IntDomainVar sel = selectVariables_.get(item);
		return sel != null && sel.getVal() == 1;
	}

	public boolean shouldDeSelect(Object item){
		IntDomainVar desel = deSelectVariables_.get(item);
		return desel != null && desel.getVal() == 1;
	}
	
	public boolean getDebug() {
		return debug_;
	}

	public void setDebug(boolean debug) {
		debug_ = debug;
	}
	
	public abstract IntDomainVar getOrCreateMappedToVariable(Object src, Object trg);

	public abstract void setValidTargets(Object src, List valid);

	public abstract void setInValidTargets(Object src, List invalid);

	public abstract ConstraintReference addSourceMappedCardinalityConstraint(
			Object src, int min, int max);

	public abstract ConstraintReference addTargetMappedSourcesCardinalityConstraint(
			Object target, int min, int max);

	public abstract ConstraintReference addTargetMappedSourcesConstraint(
			Object target, int exactmapped);

	public abstract ConstraintReference addTargetIntResourceConstraint(
			Object target, Object var, int val);

	public abstract ConstraintReference addSourceMappingConstraint(Object src,
			BinaryExpression expression);

	public abstract ConstraintReference addRequiresMappingConstraint(
			Object src, Object req);

	public abstract ConstraintReference addExcludesMappingConstraint(
			Object src, Object req);

	public abstract ConstraintReference addSelectMappingConstraint(Object src,
			List set, int min, int max);

	public abstract ConstraintReference addSelectMappingConstraint(Object src,
			List set, int exactcard);

	public ConstraintReference requireAllMapped() {
		Constraint[] cons = new Constraint[sourceItems_.size()];
		int count = 0;
		for (Object src : sourceItems_) {
			IntVar pres = getOrCreatePresenceVariable(src);
			Constraint con = problem_.geq(pres, 1);
			problem_.post(con);
			cons[count] = con;
			count++;
		}
		return createConstraintReference(null, "require all mapped", cons);
	}

	public abstract ConstraintReference requireAllMappedExactlyOnce();
}
