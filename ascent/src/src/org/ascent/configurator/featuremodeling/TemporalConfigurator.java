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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import choco.Constraint;
import choco.Problem;
import choco.integer.IntDomainVar;

public class TemporalConfigurator {

	private int timeSteps_;
	private Problem csp_ = new Problem();
	private List<Feature> allFeatures_ = new ArrayList<Feature>();
	private Map<Feature, List<IntDomainVar>> temporalSelectionVars_ = new HashMap<Feature, List<IntDomainVar>>();
	private Map<Feature, List<IntDomainVar>> temporalChangeVars_ = new HashMap<Feature, List<IntDomainVar>>();
	private List<IntDomainVar> pathLengthVars_ = new ArrayList<IntDomainVar>();
	private IntDomainVar pathLengthVar_;

	public TemporalConfigurator(int timeSteps) {
		super();
		timeSteps_ = timeSteps;
	}
	
	/**
	 * This method is called to solve the temporal
	 * configuration problem and return the list of
	 * feature selections for each time step. This
	 * method should only be called after setEndGoal
	 * has been called and setFeatureModelAtTimeStep
	 * has been called for each time step. 
	 * 
	 * @return a list containing a feature selection
	 *         for each time step
	 */
	public List<List<Feature>> configure(){
		csp_.minimize(pathLengthVar_, false);
		
		List<List<Feature>> configurations = new ArrayList<List<Feature>>(timeSteps_);
		for(int i = 0; i < timeSteps_; i++){
			List<Feature> sel = new ArrayList<Feature>();
			configurations.add(sel);
			
			for(Feature f : allFeatures_){
				IntDomainVar ft = getSelectionStateVar(f, i);
				if(ft.getVal() == 1){
					sel.add(f);
				}
			}
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

		IntDomainVar rootsel = getSelectionStateVar(root, timestep);

		for (Feature f : root.getRequiredChildren()) {
			IntDomainVar fsel = getSelectionStateVar(f, timestep);
			csp_.post(csp_.implies(csp_.eq(rootsel, 1), csp_.eq(fsel, 1)));
			csp_.post(csp_.implies(csp_.eq(fsel, 1), csp_.eq(rootsel, 1)));
			setFeatureModelAtTimeStep(f, timestep, traversed);
		}
		for (Feature f : root.getOptionalChildren()) {
			IntDomainVar fsel = getSelectionStateVar(f, timestep);
			csp_.post(csp_.implies(csp_.eq(fsel, 1), csp_.eq(rootsel, 1)));
			setFeatureModelAtTimeStep(f, timestep, traversed);
		}

		IntDomainVar[] xchildren = new IntDomainVar[root.getXorChildren()
				.size()];
		if (xchildren.length > 0) {
			for (int i = 0; i < xchildren.length; i++) {
				Feature f = root.getXorChildren().get(i);
				xchildren[i] = getSelectionStateVar(f, timestep);
				setFeatureModelAtTimeStep(f, timestep, traversed);
			}
			for (int i = 0; i < xchildren.length; i++) {
				for (int j = 0; j < xchildren.length; j++) {
					if (i == j)
						continue;
					csp_.post(csp_.implies(csp_.eq(xchildren[i], 1), csp_.eq(
							xchildren[j], 0)));
				}
			}
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
	public IntDomainVar getSelectionStateVar(Feature f, int timestep) {
		return getTemporalSelectionVars(f).get(timestep);
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
		List<IntDomainVar> cvars = getTemporalChangeVars(f);

		if (coeffs.length != cvars.size())
			throw new RuntimeException(
					"There must be exactly (timesteps - 1) change coefficients");

		IntDomainVar[] valvars = new IntDomainVar[coeffs.length];
		for (int i = 0; i < valvars.length; i++) {
			IntDomainVar val = csp_.makeBoundIntVar(f.getName() + "_change_at_"
					+ i, 0, coeffs[i]);
			csp_.post(csp_.eq(csp_.mult(coeffs[i], cvars.get(i)), val));
			valvars[i] = val;
		}

		csp_.post(csp_.lt(csp_.sum(valvars), max + 1));
	}

	/**
	 * This method sets the set of selected features at the starting
	 * time step. Each feature in the map will be set to selected
	 * or deselected based on its value in the map.
	 * 
	 * Note: You must call setFeatureModelAtTimeStep(...) for each time
	 * step before calling this method!
	 * @param state - the desired configuration state at the time step
	 */
	public void setStartingConfiguration(Map<Feature,Boolean> state){
		setConfigurationStateAtTimeStep(state, 0);
	}
	
	/**
	 * This method sets the set of selected features at a given
	 * time step. Each feature in the map will be set to selected
	 * or deselected based on its value in the map.
	 * 
	 * Note: You must call setFeatureModelAtTimeStep(...) for each time
	 * step before calling this method!
	 * @param state - the desired configuration state at the time step
	 */
	public void setConfigurationStateAtTimeStep(Map<Feature,Boolean> state, int timestep){
		for(Feature f : allFeatures_){
			int sel = (state.get(f) != null && state.get(f))? 1 : 0;
			IntDomainVar f0 = getSelectionStateVar(f, timestep);
			csp_.post(csp_.eq(f0, sel));
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
				IntDomainVar ft = getTemporalSelectionVars(f).get(i);
				int selstate = (state.get(f)) ? 1 : 0;
				Constraint ftsel = csp_.eq(ft, selstate);
				if (con != null) {
					con = csp_.and(con, ftsel);
				} else {
					con = ftsel;
				}
			}

			IntDomainVar satvar = csp_.makeBoundIntVar("sat_at_" + i, 0, 1);
			csp_.post(csp_.implies(con, csp_.eq(satvar, 1)));
			csp_.post(csp_.implies(csp_.not(con), csp_.eq(satvar, 0)));
			pathLengthVars_.add(satvar);
		}

		IntDomainVar[] sats = pathLengthVars_.toArray(new IntDomainVar[0]);
		pathLengthVar_ = csp_.makeBoundIntVar("path_length", 0, sats.length);
		csp_.post(csp_.eq(csp_.sum(sats), pathLengthVar_));
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
	public List<IntDomainVar> getTemporalChangeVars(Feature f) {
		List<IntDomainVar> vars = temporalChangeVars_.get(f);

		if (vars == null) {
			List<IntDomainVar> selvars = getTemporalSelectionVars(f);
			vars = new ArrayList<IntDomainVar>();
			temporalChangeVars_.put(f, vars);

			for (int i = 0; i < timeSteps_ - 1; i++) {
				IntDomainVar var = csp_.makeBoundIntVar(f.getName()
						+ "_changed_from:" + i + "_to_" + (i + 1), 0, 1);
				IntDomainVar t1 = selvars.get(i);
				IntDomainVar t2 = selvars.get(i + 1);
				csp_.post(csp_.implies(csp_.eq(t1, t2), csp_.eq(var, 0)));
				csp_.post(csp_.implies(csp_.not(csp_.eq(t1, t2)), csp_.eq(var,
						1)));
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
	public List<IntDomainVar> getTemporalSelectionVars(Feature f) {
		List<IntDomainVar> vars = temporalSelectionVars_.get(f);
		if (vars == null) {
			allFeatures_.add(f);
			vars = new ArrayList<IntDomainVar>();
			temporalSelectionVars_.put(f, vars);

			for (int i = 0; i < timeSteps_; i++) {
				vars
						.add(csp_.makeBoundIntVar(f.getName() + "_time:" + i,
								0, 1));
			}
		}
		return vars;
	}
}
