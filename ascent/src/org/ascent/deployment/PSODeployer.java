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
import org.ascent.pso.Pso;

public class PSODeployer implements DeploymentPlanner {

	private double globalLearningRate_ = 2;
	private double localLearningRate_ = 0.5;
	private double intertia_ = 1;
	private int maxVelocity_ = 4;
	private int totalParticles_ = 20;
	private int searchIterations_ = 20;
	private boolean useNetworkGravity_ = false;

	public DeploymentPlan deploy(DeploymentConfig problem) {

		Pso pso = new Pso(problem);
		pso.setTotalParticles(totalParticles_);
		pso.setVelocityMax(maxVelocity_);
		pso.setLocalLearningRate(localLearningRate_);
		pso.setGlobalLearningRate(globalLearningRate_);
		pso.setIterations(searchIterations_);

		
		VectorSolution sol = pso.solve(problem.getFitnessFunction());
		
		DeploymentPlan plan = problem.getDeploymentPlan(sol);
		
		if(useNetworkGravity_){
			NetworkGravityOptimizer opt = new NetworkGravityOptimizer();
			opt.optimize(plan);
		}
		
		return plan;
	}

	public double getGlobalLearningRate() {
		return globalLearningRate_;
	}

	public void setGlobalLearningRate(double globalLearningRate) {
		globalLearningRate_ = globalLearningRate;
	}

	public double getLocalLearningRate() {
		return localLearningRate_;
	}

	public void setLocalLearningRate(double localLearningRate) {
		localLearningRate_ = localLearningRate;
	}

	public double getIntertia() {
		return intertia_;
	}

	public void setIntertia(double intertia) {
		intertia_ = intertia;
	}

	public int getMaxVelocity() {
		return maxVelocity_;
	}

	public void setMaxVelocity(int maxVelocity) {
		maxVelocity_ = maxVelocity;
	}

	public int getTotalParticles() {
		return totalParticles_;
	}

	public void setTotalParticles(int totalParticles) {
		totalParticles_ = totalParticles;
	}

	public int getSearchIterations() {
		return searchIterations_;
	}

	public void setSearchIterations(int searchIterations) {
		searchIterations_ = searchIterations;
	}

	public boolean isUseNetworkGravity() {
		return useNetworkGravity_;
	}

	public void setUseNetworkGravity(boolean useNetworkGravity) {
		useNetworkGravity_ = useNetworkGravity;
	}

}
