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


package org.ascent.deployment;

import java.util.Comparator;

import org.ascent.VectorSolution;
import org.ascent.VectorSolutionComparator;
import org.ascent.pso.Pso;

public class BandwidthMinimizingPSODeploymentPlanner implements DeploymentPlanner{

	
	public DeploymentPlan deploy(DeploymentConfig conf) {
		return solveForMinimalBandwidthConsumption(conf);
	}

	public static DeploymentPlan solveForMinimalBandwidthConsumption(DeploymentConfig conf){
		NetworkBandwidthMinimizingPlanner problem = new NetworkBandwidthMinimizingPlanner(conf);
		
		double grate = 2;// the global learning rate
		double lrate = 0.5;// the local learning rate
		double intertia = 1;// the particle intertia impact
		int maxv = 4;// the max particle velocity
		int particles = 200;// the total number of particles
		int iterations = 20;// the total number of iterations per solver
							// invocation

		Pso pso = new Pso(problem);
		pso.setTotalParticles(particles);
		pso.setVelocityMax(maxv);
		pso.setLocalLearningRate(lrate);
		pso.setGlobalLearningRate(grate);
		pso.setIterations(20);

		Comparator<VectorSolution> comp = new VectorSolutionComparator(problem
				.getFitnessFunction());
		VectorSolution sol = pso.solve(problem.getFitnessFunction());
		
		DeploymentPlan plan = new DeploymentPlan(problem, sol);
		
		return plan;
	}
}
