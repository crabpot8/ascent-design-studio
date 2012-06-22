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



/**
 * 
 * This class is the interface to the RefreshSolvers for
 * deriving a way of mapping one or more elements from a
 * source set to one or more elements in a target set. The
 * mapping is governed by a series of local, dependency,
 * and global constraints.
 * 
 * This class is the interface to the core solver in Refresh.
 * Different solvers can be swapped in and out to alter the
 * solution speed/properties and tailor the solving for specific
 * problem types and instances. All users of the solver should
 * write code against this interface and not specific implementations.
 * 
 * The basic usage of the solver is to set a series of source and
 * target items and then to place constraints on how source items
 * can be mapped to targets. For example:
 * 
 * <code>
 *  RefreshCore core = new RefreshMatrixCore();
 *	core.setSetsToMap(
 *          new String[] { "a", "b", "c" }, 
 *          new String[] { "1", "2", "3" });
 *	core.setTargetVariableValues("1", new Object[]{"RAM",1});
 *	core.setTargetVariableValues("2", new Object[]{"RAM",2});
 *	core.setTargetVariableValues("3", new Object[]{"RAM",3});
 *	core.createIntVariable("TotalRAM", "=", 
 *        ExpressionParser.getExpression("Target.RAM.Sum + 10"));
 *	core.requireAllMappedExactlyOnce();
 *	core.addRequiresMappingConstraint("a", "b");
 *	core.addExcludesMappingConstraint("b", "c");
 *	core.setValidTargets("a", Arrays.asList(new Object[]{"2","3"}));
 *	core.setValidTargets("c", Arrays.asList(new Object[]{"2","3"}));
 *	Map<Object, List> sol = core.nextMapping();
 * </code>
 * 
 * This example defines three source items, <code>a,b,c</code>,
 * that need to be mapped to one of three target items,
 * <code>1,2,3</code>. A <code>RAM</code> variable is defined
 * for each target. A global variable called <code>TotalRAM</code>
 * is defined and bound to be equal to the sum of the target's
 * RAM variables. A constraint that <code>b</code> cannot be 
 * mapped to the same target as <code>c</code> is added. Another
 * constraint requiring <code>a</code> be mapped to the same
 * target as <code>a</code> is added. Finally, valid and invalid
 * target items that <code>a</code> and <code>c</code> can be
 * mapped to are set. The call to <code>nextMapping()</code>
 * invokes the solver to return a solution.
 */
public interface RefreshCore {

	public static final String SUM_PREFIX = "sum_of_";

	public static final String PRESENCE_PREFIX = "in_solution_";

	/**
	 * If ZeroUndefinedVariables is set to true,
	 * it causes any source or target variables that 
	 * are referenced by a constraint to be set to
	 * zero if they are undefined.
	 * 
	 * 
	 * @return zeroUndefinedVariables
	 */
	public abstract boolean getZeroUndefinedVariables();

	/**
	 * If ZeroUndefinedVariables is set to true,
	 * it causes any source or target variables that 
	 * are referenced by a constraint to be set to
	 * zero if they are undefined.
	 * 
	 * @param zeroUndefinedVariables
	 *            the zeroUndefinedVariables to set
	 */
	public abstract void setZeroUndefinedVariables(
			boolean zeroUndefinedVariables);

	/**
	 * If MaximizeOptimizationFunction is set to true,
	 * the solver will attempt to produce the highest
	 * value for the optimization function. Otherwise,
	 * it will produce the lowest value.
	 * 
	 * @return the maximizeOptimizationFunction
	 */
	public abstract boolean getMaximizeOptimizationFunction();

	
	/**
	 * If MaximizeOptimizationFunction is set to true,
	 * the solver will attempt to produce the highest
	 * value for the optimization function. Otherwise,
	 * it will produce the lowest value.
	 * 
	 * @param maximizeOptimizationFunction
	 *            the maximizeOptimizationFunction to set
	 */
	public abstract void setMaximizeOptimizationFunction(
			boolean maximizeOptimizationFunction);

	/**
	 * The OptimizationFunction determines the goal
	 * that the solver attempts to maximize or minimize
	 * when there are multiple possible solutions. By
	 * default, the OptimizationFunction is set to null
	 * and the solver arbitrarily picks a solution. The
	 * OptimizationFunction allows the solver to pick
	 * the solution that optimizes your goals.
	 * 
	 * @param optimizationFunction
	 *            the optimizationFunction to set
	 */
	public abstract void setOptimizationFunction(Expression exp);

	/**
	 * This sets the source items and target items that the
	 * solver is mapping. Each item in the source set will
	 * be associated with one or more items in the target set.
	 * 
	 * @param srcs the source set
	 * @param trgs the target set
	 */
	public abstract void setSetsToMap(List srcs, List trgs);

	/**
	 * This sets the source items and target items that the
	 * solver is mapping. Each item in the source set will
	 * be associated with one or more items in the target set.
	 * 
	 * @param srcs the source set
	 * @param trgs the target set
	 */
	public abstract void setSetsToMap(Object[] srcs, Object[] trgs);
	
	/**
	 * This function requires the solver to map the 
	 * specified source item to the specified target
	 * item. 
	 * @param src the source item
	 * @param trg the target item to map the source
	 *            item to
	 */
	public abstract void mapTo(Object src, Object trg);

	/**
	 * This method sets the variable values for a target item. The map
	 * of provided values must use the variable names as keys and the
	 * values are the variable values. 
	 * 
	 * @param target the target item to set variable values for
	 * @param values the values of the target item's variables
	 */
	public abstract void setTargetVariableValues(Object target, Map values);

	/**
	 * This method sets the variable values for a target item. The array
	 * of provided values must alternate between variable names and
	 * keys (i.e. [myVar, myVal, myVar1, myVal1,...]). 
	 * 
	 * @param target the target item to set variable values for
	 * @param values the values of the target item's variables
	 */
	public abstract void setTargetVariableValues(Object target, Object[] values);

	/**
	 * This method sets the variable values for a source item. The map
	 * of provided values must use the variable names as keys and the
	 * values are the variable values. 
	 * 
	 * @param source the source item to set variable values for
	 * @param values the values of the source item's variables
	 */
	public abstract void setSourceVariableValues(Object source, Map values);

	/**
	 * This method sets the variable values for a source item. The array
	 * of provided values must alternate between variable names and
	 * keys (i.e. [myVar, myVal, myVar1, myVal1,...]). 
	 * 
	 * @param source the target item to set variable values for
	 * @param values the values of the source item's variables
	 */
	public abstract void setSourceVariableValues(Object source, Object[] values);

	/**
	 * This method solves the constraint satisfaction problem
	 * and derives a new and valid mapping of the source items
	 * to the target items. If an optimization function was set,
	 * the mapping returned will be optimal.
	 * 
	 * @return the next solution or null if no solution exists
	 */
	public abstract Map<Object, List> nextMapping();

	/**
	 * This method solves the constraint satisfaction problem
	 * and derives a new and valid mapping of the source items
	 * to the target items within the specified time limit. If
	 * the time limit is reached before a solution, null is 
	 * returned. If an optimization function was set,
	 * the mapping returned will be optimal.
	 * 
	 * @return the next solution or null if no solution exists
	 */
	public abstract Map<Object, List> nextMappingWithinTimeLimit(int time);

	/**
	 * This method restricts a source item so that it can
	 * only be mapped to one of the target items in the
	 * provided set. 
	 * 
	 * @param src the source item to restrict the mapping for
	 * @param valid the valid targets that the source item can be 
	 *              mapped to
	 */
	public abstract void setValidTargets(Object src, List valid);

	/**
	 * This method excludes a source item from being mapped to the
	 * specified targets.
	 * 
	 * @param src the source item to restrict the mapping for
	 * @param invalid the targets that the item cannot be mapped to
	 */
	public abstract void setInValidTargets(Object src, List invalid);

	/**
	 * This method requires a specific source item to be mapped
	 * in the final solution. By default, source items are
	 * not required to be mapped. If no source items are required
	 * to be mapped, the solver WILL NOT map anything. The source
	 * item will be mapped to at least 1 and possibly more targets.
	 * 
	 * @param src the source item that must be mapped in the solution
	 * @return a reference to the constraint that can be used for debugging
	 */
	public ConstraintReference requireMapped(Object src);
	
	/**
	 * This method requires a specific source item NOT be mapped
	 * in the final solution. 
	 * 
	 * @param src the source item that must NOT be mapped in the solution
	 * @return a reference to the constraint that can be used for debugging
	 */
	public ConstraintReference  requireNotMapped(Object src);
	
	/**
	 * This method requires that the solution map every source 
	 * item to a target item. By default, source items are
	 * not required to be mapped. If no source items are required
	 * to be mapped, the solver WILL NOT map anything. This method
	 * makes sure EVERYTHING gets mapped to at least one target item.
	 * 
	 * @return a reference to the constraint that can be used for debugging
	 */
	public ConstraintReference  requireAllMapped();

	/**
	 * This method requires that the solution map every source 
	 * item to a target item. By default, source items are
	 * not required to be mapped. If no source items are required
	 * to be mapped, the solver WILL NOT map anything. This method
	 * makes sure EVERYTHING gets mapped to EXACTLY one target item.
	 * 
	 * @return a reference to the constraint that can be used for debugging
	 */
	public ConstraintReference  requireAllMappedExactlyOnce();

	/**
	 * This method ensures that the number of target items
	 * that a source item is mapped to satisfies the specified
	 * cardinality constraint. For example, this method can
	 * be used to ensure that an item gets mapped to between
	 * 3 and 5 target items.
	 * 
	 * @param src the source item that the mapping cardinality is 
	 *            being set for
	 * @param min the minimum number of targets to map the item to
	 * @param max the maximum number of targets to map the item to
	 * @return a reference to the constraint that can be used for debugging
	 */
	public ConstraintReference  addSourceMappedCardinalityConstraint(Object src,
			int min, int max);

	/**
	 * This method ensures that the number of source items
	 * that are mapped to a specific target item satisfies the specified
	 * cardinality constraint. For example, this method can
	 * be used to ensure that a target gets between
	 * 3 and 5 source items mapped to it.
	 * 
	 * @param target the target item that the mapping cardinality is 
	 *            being set for
	 * @param min the minimum number of sources to map to the target
	 * @param max the maximum number of sources to map to the target
	 * @return a reference to the constraint that can be used for debugging
	 */
	public ConstraintReference  addTargetMappedSourcesCardinalityConstraint(
			Object target, int min, int max);

	/**
	 * This method ensures that the number of source items
	 * that are mapped to a specific target item satisfies the specified
	 * cardinality constraint. For example, this method can
	 * be used to ensure that a target gets between
	 * 3 and 5 source items mapped to it.
	 * 
	 * @param target the target item that the mapping cardinality is 
	 *            being set for
	 * @param exactmapped the exact number of sources to map to the target
	 * @return a reference to the constraint that can be used for debugging
	 */
	public ConstraintReference  addTargetMappedSourcesConstraint(Object target,
			int exactmapped);

	/**
	 * This method specifies a resource constraint that must
	 * be observed when mapping source items to the target.
	 * For example, a 'memory' resource constraint can be applied
	 * to a node. This constraint would ensure that the total
	 * value of the memory attributes of all sources mapped to the
	 * target were less than the value of the memory attribute
	 * on the target.
	 * @param target the target to apply the resource constraint to
	 * @param var the name of the attribute that is the resource 
	 * @param val the amount of the resource on the target
	 * @return a reference for debugging
	 */
	public ConstraintReference  addTargetIntResourceConstraint(Object target,
			Object var, int val);
	
	
//	/**
//	 * This method specifies a *shared* resource constraint that must
//	 * be observed when mapping source items to the target.
//	 * For example, a 'bandwidth' shared resource constraint can be applied
//	 * to a group of nodes. This constraint would ensure that the total
//	 * value of the bandwidth consumed by all sources mapped to this
//	 * set of nodes does not exceed the value of the shared resource.
//	 * 
//	 * @param target the target to apply the resource constraint to
//	 * @param var the name of the attribute that is the resource 
//	 * @param val the amount of the resource on the target
//	 * @return a reference for debugging
//	 */
//	public ConstraintReference  addTargetIntSharedResourceConstraint(List targets,
//			Object var, int val);
	
	
	/**
	 * This method sets a global resource constraint that cannot
	 * be violated by the mapping of sources to targets. For all
	 * targets, the sum of the resource demands of the components
	 * deployed to the target cannot exceed the amount of each
	 * resource available on the target. 
	 * 
	 * <code>
	 * sourceresourceconsumption[i][j] = //amount of resource j consumed
	 *                                   //by the ith source
	 * targetresourceavail[i][j] = //amount of resource j available on the
	 *                             //the ith target                                 
	 * </code>
	 * 
	 * @param sourceresourceconsumption - the resource consumption values 
	 *                                    for the sources
	 * @param targetresourceavail - the available resources on each node
	 */
	public void setResourceConstraints(int[][] sourceresourceconsumption, int[][] targetresourceavail);

	/**
	 * This method returns a table of the attributes for a target.
	 * The keys are the attribute names and the values are the
	 * attribute values.
	 * 
	 * @param target the target to obtain the attribute values for
	 * @return the attribute values of the target
	 */
	public abstract Map getTargetVariableValues(Object target);

	/**
	 * This method returns a table of the attributes for a source.
	 * The keys are the attribute names and the values are the
	 * attribute values.
	 * 
	 * @param source the source to obtain the attribute values for
	 * @return the attribute values of the source
	 */
	public abstract Map getSourceVariableValues(Object source);

	/**
	 * This method adds a constraint that ensures that if the
	 * specified source object is mapped to a target the other
	 * specified source object is also mapped to the same target.
	 * In other words, mapping <code>src</code> to a target
	 * requires also mapping <code>req</code> to the target.
	 * @param src the source item that requires another item
	 * @param req the source item that is required by the other item
	 * @return a reference for debugging
	 */
	public ConstraintReference  addRequiresMappingConstraint(Object src, Object req);

	/**
	 * This method adds a constraint that ensures that if the
	 * specified source object is mapped to a target that between
	 * min and max of the items in the provided list of source
	 * items are also mapped to the target. 
	 * In other words, mapping <code>src</code> to a target
	 * requires also mapping <code>min..max</code> of the source
	 * items in <code>set</code> to the target.
	 * @param src the source item that requires some set of other source items
	 * @param set the source item set that is required by the other item
	 * @param min the minimum number of the source items that must be mapped to the target
	 * @param max the maximum number of the source items that must be mapped to the target
	 * @return a reference for debugging
	 */
	public ConstraintReference  addSelectMappingConstraint(Object src, List set,
			int min, int max);

	/**
	 * This method adds a constraint that ensures that if the
	 * specified source object is mapped to a target that between
	 * min and max of the items in the provided list of source
	 * items are also mapped to the target. 
	 * In other words, mapping <code>src</code> to a target
	 * requires also mapping <code>min..max</code> of the source
	 * items in <code>set</code> to the target.
	 * @param src the source item that requires some set of other source items
	 * @param set the source item set that is required by the other item
	 * @param exactcard the exact number of the source items that must
	 *                  be mapped to the same target
	 * @return a reference for debugging
	 */
	public ConstraintReference  addSelectMappingConstraint(Object src, List set,
			int exactcard);

	/**
	 * This method adds a constraint that ensures that if the
	 * specified source object is mapped to a target the other
	 * specified source object is NOT mapped to the same target.
	 * In other words, mapping <code>src</code> to a target
	 * excludes mapping <code>req</code> to the target.
	 * @param src the source item that excludes another item
	 * @param req the source item that is excluded by the other item
	 * @return a reference for debugging
	 */
	public ConstraintReference  addExcludesMappingConstraint(Object src, Object req);

	/**
	 * This method adds a constraint that ensures that if the
	 * specified source object is mapped to a target the provided
	 * <code>BinaryExpression</code> holds true over the
	 * source and target item combination. For example, if
	 * the mapping constraint <code>src.name == target.name</code>
	 * was added to the source item, it would restrict the source
	 * item to only be mapped to targets with the same name. --NOTE--
	 * If possible, it is better to do these types of filtering
	 * steps ahead of time and use the setValidTargets or setInvalidTargets
	 * to reduce the targets that a source item can be applied to.
	 * This type of filtering can be accomplished via the
	 * <code>EvaluateHander</code> configuration directive.
	 * @see org.ascent.configurator.conf.EvaluateHandler
	 * 
	 * @param src the source item that the mapping constraint should 
	 *            be applied to
	 * @param expression the binary expression that must hold true 
	 *                   for the mapping of the source to a target
	 * @return a reference for debugging
	 */
	public ConstraintReference  addSourceMappingConstraint(Object src,
			BinaryExpression expression);

	/**
	 * This method defines a new global integer variable that
	 * can be referenced by constraints, optimization goals, and
	 * other variable definitions.
	 * @param variable the key for the variable 
	 * @param value the value for the variable
	 */
	public abstract void setIntVariableValue(Object variable, int value);

	/**
	 * This method defines a new global integer variable that
	 * can be referenced by constraints, optimization goals, and
	 * other variable definitions. The value for the variable is
	 * left unbound so that it can be set by the solver. The
	 * provided expression bounds the variable so that the solver
	 * can derive a value for it.
	 * 
	 * @param variable the key for the variable 
	 * @param op a comparison operator to bound the variable (<,>,=,etc.)
	 * @param exp an expression to bound the value of the variable by
	 *            the <code>op</code>
	 */
	public abstract void createIntVariable(Object variable, String op,
			Expression exp);

	/**
	 * This method defines a new integer variable in the scope of
	 * the source or target item that
	 * can be referenced by constraints, optimization goals, and
	 * other variable definitions. The value for the variable is
	 * left unbound so that it can be set by the solver. The
	 * provided expression bounds the variable so that the solver
	 * can derive a value for it.
	 * 
	 * @param ctx the source or target item to scope the variable to
	 * @param variable the key for the variable 
	 * @param op a comparison operator to bound the variable (<,>,=,etc.)
	 * @param exp an expression to bound the value of the variable by
	 *            the <code>op</code>
	 */
	public abstract void createIntVariable(Object ctx, Object variable, String op,
			Expression exp);

	/**
	 * After the solver has run (i.e. <code>nextMapping(..)</code>
	 * has been called), this method will return the list of
	 * targets that a specific source item was mapped to. If the
	 * source was not mapped to any targets, this method will return
	 * an empty list.
	 * 
	 * @param src
	 * @return
	 */
	public abstract List getSourceTargets(Object src);
	
    /**
     * This method returns the value bound to a specific variable.
     * Global variable values are obtained by passing in their
     * corresponding key. 
     * 
     * @param var the variable value to lookup
     * @return the value of the variable
     */
	public abstract Object getVariableValue(String var);
	
    /**
     * This method returns the value bound to a specific variable.
     * The specified source or target item context is used as
     * the scope to resolve the variable.
     * 
     * @param ctx the source or target item scope to resolve the variable in
     * @param var the variable value to lookup
     * @return the value of the variable
     */
	public abstract Object getVariableValue(Object ctx, String var);
	
    /**
     * This method returns a map of all variables and their
     * bound values that are in scope for a specific source or
     * target item.
     * 
     * @param ctx the source or target item scope to get a variable map for
     * @return a map with keys for each variable and values of the
     *         corresponding variable values
     */
	public abstract Map getContextVariableValues(Object ctx);
}