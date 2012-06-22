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

package org.ascent.deployment;

import org.ascent.VectorSolution;
import org.ascent.genetic.GeneticAlgorithm;
import org.ascent.pso.Pso;

public class GeneticDeployer implements DeploymentPlanner {

	private int populationSize_ = 20;
	private int totalGenerations_ = 20;
	private boolean useNetworkGravity_ = false;

	public DeploymentPlan deploy(DeploymentConfig problem) {

		GeneticAlgorithm ga = new GeneticAlgorithm(problem);
		ga.setCrossOver(populationSize_/10);
		ga.setPopulationSize(populationSize_);
		ga.setGenerations(totalGenerations_);

		VectorSolution sol = ga.solve(problem.getFitnessFunction());

		DeploymentPlan plan = problem.getDeploymentPlan(sol);

		if (useNetworkGravity_) {
			NetworkGravityOptimizer opt = new NetworkGravityOptimizer();
			opt.optimize(plan);
		}

		return plan;
	}

	public int getPopulationSize() {
		return populationSize_;
	}

	public void setPopulationSize(int populationSize) {
		populationSize_ = populationSize;
	}

	public int getTotalGenerations() {
		return totalGenerations_;
	}

	public void setTotalGenerations(int totalGenerations) {
		totalGenerations_ = totalGenerations;
	}

	public boolean isUseNetworkGravity() {
		return useNetworkGravity_;
	}

	public void setUseNetworkGravity(boolean useNetworkGravity) {
		useNetworkGravity_ = useNetworkGravity;
	}

}
