/**************************************************************************
 * Copyright 2009 Jules White                                              *
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

package org.ascent.configurator.featuremodeling;

import static choco.Choco.and;
import static choco.Choco.eq;
import static choco.Choco.implies;
import static choco.Choco.lt;
import static choco.Choco.makeIntVar;
import static choco.Choco.mult;
import static choco.Choco.not;
import static choco.Choco.sum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

public class TemporalConfigurator {

	private int timeSteps_;
	private CPModel csp_ = new CPModel();
	private List<Feature> allFeatures_ = new ArrayList<Feature>();
	private Map<Feature, List<IntegerVariable>> temporalSelectionVars_ = new HashMap<Feature, List<IntegerVariable>>();
	private Map<Feature, List<IntegerVariable>> temporalChangeVars_ = new HashMap<Feature, List<IntegerVariable>>();
	private List<IntegerVariable> pathLengthVars_ = new ArrayList<IntegerVariable>();
	private IntegerVariable pathLengthVar_;

	public TemporalConfigurator(int timeSteps) {
		super();
		timeSteps_ = timeSteps;
	}

	/**
	 * This method is called to solve the temporal configuration problem and
	 * return the list of feature selections for each time step. This method
	 * should only be called after setEndGoal has been called and
	 * setFeatureModelAtTimeStep has been called for each time step.
	 * 
	 * @return a list containing a feature selection for each time step
	 */
	public List<List<Feature>> configure() {
		return configure(false,20000);
	}
	
	/**
	 * This method is called to solve the temporal configuration problem and
	 * return the list of feature selections for each time step. This method
	 * should only be called after setEndGoal has been called and
	 * setFeatureModelAtTimeStep has been called for each time step.
	 * 
	 * 
	 * @param minpathlength - whether or not to try and optimize the path length
	 * @return a list containing a feature selection for each time step
	 */
	public List<List<Feature>> configure(boolean minpathlength, int timelimit) {
		Solver s = new CPSolver();
		s.read(csp_);
		
		if(timelimit > 0)
			s.setTimeLimit(timelimit);
		
		if(minpathlength)
			s.maximize(true);
		else
			s.solve();

		if(!s.isFeasible()){
			return null;
		}
		
		List<List<Feature>> configurations = new ArrayList<List<Feature>>(
				timeSteps_);
		for (int i = 0; i < timeSteps_; i++) {
			List<Feature> sel = new ArrayList<Feature>();
			configurations.add(sel);

			for (Feature f : allFeatures_) {
				IntegerVariable ft = getSelectionStateVar(f, i);
				if (s.getVar(ft).getVal() == 1) {
					sel.add(f);
				}
			}
			
			//As soon as we have reached the end state, we can
			//stop
			if(s.getVar(pathLengthVars_.get(i)).getVal() == 1)
				break;
		}

		return configurations;
	}

	/**
	 * This method sets the feature selection rules (feature model constraints)
	 * for the specified time step.
	 * 
	 * @param root
	 *            - the root feature of the feature model
	 * @param timestep
	 *            - the time step to set the feature constraints for
	 */
	public void setFeatureModelAtTimeStep(Feature root, int timestep) {
		setFeatureModelAtTimeStep(root, timestep, new ArrayList<Feature>());
	}

	// Internal implementation of the above method.
	protected void setFeatureModelAtTimeStep(Feature root, int timestep,
			List<Feature> traversed) {
		if (traversed.contains(root))
			return;
		else
			traversed.add(root);

		IntegerVariable rootsel = getSelectionStateVar(root, timestep);

		for (Feature f : root.getRequiredChildren()) {
			IntegerVariable fsel = getSelectionStateVar(f, timestep);
			csp_.addConstraint(implies(eq(rootsel, 1), eq(fsel, 1)));
			csp_.addConstraint(implies(eq(fsel, 1), eq(rootsel, 1)));
			setFeatureModelAtTimeStep(f, timestep, traversed);
		}
		for (Feature f : root.getOptionalChildren()) {
			IntegerVariable fsel = getSelectionStateVar(f, timestep);
			csp_.addConstraint(implies(eq(fsel, 1), eq(rootsel, 1)));
			setFeatureModelAtTimeStep(f, timestep, traversed);
		}

		IntegerVariable[] xchildren = new IntegerVariable[root.getXorChildren()
				.size()];
		if (xchildren.length > 0) {
			for (int i = 0; i < xchildren.length; i++) {
				Feature f = root.getXorChildren().get(i);
				xchildren[i] = getSelectionStateVar(f, timestep);
				csp_.addConstraint(implies(eq(xchildren[i],1),eq(rootsel,1)));
				setFeatureModelAtTimeStep(f, timestep, traversed);
			}
			for (int i = 0; i < xchildren.length; i++) {
				for (int j = 0; j < xchildren.length; j++) {
					if (i == j)
						continue;
					csp_.addConstraint(implies(eq(xchildren[i], 1), eq(
							xchildren[j], 0)));
				}
			}
			//We must require at least one of the xors to be selected
			csp_.addConstraint(implies(eq(rootsel,1),eq(sum(xchildren),1)));
		}
	}

	/**
	 * Returns the feature selection variable that represents whether or not the
	 * feature is selected at the given time step.
	 * 
	 * @param f
	 *            - the feature
	 * @param timestep
	 *            - the time step
	 * @return a variable that has value 1 if the feature is selected at the
	 *         given time step
	 */
	public IntegerVariable getSelectionStateVar(Feature f, int timestep) {
		return getTemporalSelectionVars(f).get(timestep);
	}

	/**
	 * This method bounds the sum of the costs of the changes across all
	 * features between any two time steps. If a feature's selection state at
	 * time step i does not match the selection state at i+1, the cost coeffs[i]
	 * is incurred. The sum of these incurred costs across all features must be
	 * less than or equal to maxperstep.
	 * 
	 * This method is used to place a cost on adding/removing features from the
	 * selection between any two time steps. The cost of adding/removing a
	 * feature can change over time by setting the values in coeffs to different
	 * values.
	 * 
	 * @param f
	 * @param coeffs
	 * @param max
	 */
	public void boundChangeCoefficientPerStep(int[][] coeffs, int maxperstep) {
		for (int j = 0; j < timeSteps_ - 1; j++) {
			IntegerVariable[] costs = new IntegerVariable[allFeatures_.size()];
			for (int i = 0; i < allFeatures_.size(); i++) {
				Feature f = allFeatures_.get(i);
				IntegerVariable cvar = getTemporalChangeVars(f).get(j);

				// Should this be coeffs[i][j+1]?
				// I mean..should I be using the cost of the change
				// on the feature model at time step j instead?
				IntegerVariable costvar = makeIntVar("", 0, coeffs[i][j + 1]);
				csp_.addConstraint(eq(mult(coeffs[i][j + 1], cvar), costvar));
				costs[i] = costvar;
			}
			csp_.addConstraint(lt(sum(costs), maxperstep + 1));
		}
	}

	/**
	 * This method bounds the sum of the costs of the changes across all
	 * features. If a feature's selection state at time step i does not match
	 * the selection state at i+1, the cost coeffs[i] is incurred. The sum of
	 * these incurred costs across all features must be less than or equal to
	 * max.
	 * 
	 * This method is used to place a cost on adding/removing features from the
	 * selection. The cost of adding/removing a feature can change over time by
	 * setting the values in coeffs to different values.
	 * 
	 * @param f
	 * @param coeffs
	 * @param max
	 */
	public void boundChangeCoefficientOnFeatures(int[][] coeffs, int max) {
		IntegerVariable[] costs = new IntegerVariable[allFeatures_.size()];
		for (int i = 0; i < allFeatures_.size(); i++) {
			Feature f = allFeatures_.get(i);
			costs[i] = createChangeCoefficientOnFeature(f, coeffs[i]);
		}
		csp_.addConstraint(lt(sum(costs), max + 1));
	}

	/**
	 * This method bounds the sum of the costs of the changes on a feature. If a
	 * feature's selection state at time step i does not match the selection
	 * state at i+1, the cost coeffs[i] is incurred. The sum of these incurred
	 * costs must be less than or equal to max.
	 * 
	 * This method is used to place a cost on adding/removing features from the
	 * selection. The cost of adding/removing a feature can change over time by
	 * setting the values in coeffs to different values.
	 * 
	 * @param f
	 * @param coeffs
	 * @param max
	 */
	public void boundChangeCoefficientOnFeature(Feature f, int[] coeffs, int max) {
		IntegerVariable cost = createChangeCoefficientOnFeature(f, coeffs);
		csp_.addConstraint(lt(cost, max + 1));
	}

	/**
	 * This method creates a variable boudn to the sum of the costs of the
	 * changes on a feature. If afeature's selection state at time step i does
	 * not match the selection state at i+1, the cost coeffs[i] is incurred. The
	 * returned variable's value is equal to the sum of these costs.
	 * 
	 * This method is used to place a cost on adding/removing features from the
	 * selection. The cost of adding/removing a feature can change over time by
	 * setting the values in coeffs to different values.
	 * 
	 * @param f
	 *            - the feature
	 * @param coeffs
	 *            - the cost coefficients
	 */
	public IntegerVariable createChangeCoefficientOnFeature(Feature f,
			int[] coeffs) {
		List<IntegerVariable> cvars = getTemporalChangeVars(f);

		if (coeffs.length != cvars.size())
			throw new RuntimeException(
					"There must be exactly (timesteps - 1) change coefficients");

		IntegerVariable[] valvars = new IntegerVariable[coeffs.length];
		for (int i = 0; i < valvars.length; i++) {
			IntegerVariable val = makeIntVar(f.getName() + "_change_at_" + i,
					0, coeffs[i]);
			csp_.addConstraint(eq(mult(coeffs[i], cvars.get(i)), val));
			valvars[i] = val;
		}

		int sum = 0;
		for (int c : coeffs)
			sum += c;
		IntegerVariable changecoeff = makeIntVar(f.getName() + "_change_sum",
				0, sum);
		csp_.addConstraint(eq(sum(valvars), changecoeff));
		return changecoeff;
	}

	/**
	 * This method sets the set of selected features at the starting time step.
	 * Each feature in the map will be set to selected or deselected based on
	 * its value in the map.
	 * 
	 * Note: You must call setFeatureModelAtTimeStep(...) for each time step
	 * before calling this method!
	 * 
	 * @param state
	 *            - the desired configuration state at the time step
	 */
	public void setStartingConfiguration(Map<Feature, Boolean> state) {
		setConfigurationStateAtTimeStep(state, 0);
	}

	/**
	 * This method sets the set of selected features at a given time step. Each
	 * feature in the map will be set to selected or deselected based on its
	 * value in the map.
	 * 
	 * Note: You must call setFeatureModelAtTimeStep(...) for each time step
	 * before calling this method!
	 * 
	 * @param state
	 *            - the desired configuration state at the time step
	 */
	public void setConfigurationStateAtTimeStep(Map<Feature, Boolean> state,
			int timestep) {
		for (Feature f : allFeatures_) {
			if (state.get(f) != null) {
				int sel = (state.get(f)) ? 1 : 0;
				IntegerVariable f0 = getSelectionStateVar(f, timestep);
				csp_.addConstraint(eq(f0, sel));
			}
		}
	}

	/**
	 * This a set of feature selection states that specifies an end goal of the
	 * configuration. Each key is a feature and the corresponding value is a
	 * boolean indicating whether or not the feature should be selected in the
	 * end state.
	 * 
	 * This is intended to be called after the features are declared...but it
	 * should work even if they haven't been declared yet.
	 * 
	 * @param state
	 *            - the set of feature selection states to reach
	 */
	public void setEndGoal(Map<Feature, Boolean> state) {

		for (int i = 0; i < timeSteps_; i++) {
			Constraint con = null;
			for (Feature f : state.keySet()) {
				IntegerVariable ft = getTemporalSelectionVars(f).get(i);
				int selstate = (state.get(f)) ? 1 : 0;
				Constraint ftsel = eq(ft, selstate);
				if (con != null) {
					con = and(ftsel, con);
				} else {
					con = ftsel;
				}
			}

			IntegerVariable satvar = makeIntVar("sat_at_" + i, 0, 1);
			csp_.addConstraint(implies(eq(satvar, 1), con));
			csp_.addConstraint(implies(not(con), eq(satvar, 0)));
			pathLengthVars_.add(satvar);
		}

		IntegerVariable[] sats = pathLengthVars_
				.toArray(new IntegerVariable[0]);
		pathLengthVar_ = makeIntVar("path_length", 1, sats.length,
				"cp:objective");
		csp_.addConstraint(eq(sum(sats), pathLengthVar_));
	}

	/**
	 * This method gets or creates a list of variables to capture changes in
	 * selection state of a feature between two time steps. If the selection
	 * state at time i does not match the selection state at time i+1, the
	 * variable at position i will have value 1 and 0 otherwise.
	 * 
	 * @param f
	 *            - the feature
	 * @return the change variables
	 */
	public List<IntegerVariable> getTemporalChangeVars(Feature f) {
		List<IntegerVariable> vars = temporalChangeVars_.get(f);

		if (vars == null) {
			List<IntegerVariable> selvars = getTemporalSelectionVars(f);
			vars = new ArrayList<IntegerVariable>();
			temporalChangeVars_.put(f, vars);

			for (int i = 0; i < timeSteps_ - 1; i++) {
				IntegerVariable var = makeIntVar(f.getName() + "_changed_from:"
						+ i + "_to_" + (i + 1), 0, 1);
				IntegerVariable t1 = selvars.get(i);
				IntegerVariable t2 = selvars.get(i + 1);
				csp_.addConstraint(implies(eq(t1, t2), eq(var, 0)));
				csp_.addConstraint(implies(not(eq(t1, t2)), eq(var, 1)));
				vars.add(var);
			}
		}

		return vars;
	}

	/**
	 * This method gets or creates a set of variables representing the selection
	 * state of the feature over the entire time series. The ith variable will
	 * have value 1 if the feature is selected at time step i and zero
	 * otherwise.
	 * 
	 * @param f
	 *            - the feature
	 * @return the set of selection variables for the time steps
	 */
	public List<IntegerVariable> getTemporalSelectionVars(Feature f) {
		List<IntegerVariable> vars = temporalSelectionVars_.get(f);
		
		boolean isdec = f.getXorChildren().size() == 0 && f.getRequiredChildren().size() == 0;
		
		if (vars == null) {
			allFeatures_.add(f);
			vars = new ArrayList<IntegerVariable>();
			temporalSelectionVars_.put(f, vars);

			for (int i = 0; i < timeSteps_; i++) {
				IntegerVariable v = makeIntVar(f.getName() + "_time:" + i, 0,
						1, "cp:decision");
				
				if(isdec)
					csp_.addVariable("cp:decision", v);
				else 
					csp_.addVariable("cp:decision", v);
				vars.add(v);
			}
		}
		return vars;
	}
}
