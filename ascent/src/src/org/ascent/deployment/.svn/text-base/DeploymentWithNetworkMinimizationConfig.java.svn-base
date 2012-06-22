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

import org.ascent.VectorSolution;
import org.ascent.binpacking.ValueFunction;

public class DeploymentWithNetworkMinimizationConfig extends DeploymentConfig {

	private ValueFunction<VectorSolution> fitnessFunction_ = new ValueFunction<VectorSolution>() {

		public double getValue(VectorSolution src) {
			if (src.getArtifact() == null) {
				DeploymentPlan plan = new DeploymentPlan(
						DeploymentWithNetworkMinimizationConfig.this, src);
				int score = scoreDeployment(plan);
				src.setArtifact(score);
			}
			return (Integer) src.getArtifact();
		}
	};

	public DeploymentWithNetworkMinimizationConfig(DeploymentConfig toclone) {
		super(toclone);
	}

	public DeploymentWithNetworkMinimizationConfig(Node[] nodes,
			NetworkLink[] networks, Component[] components,
			Interaction[] interactions) {
		super(components.length, 0, nodes.length - 1);
		nodes_ = nodes;
		networks_ = networks;
		components_ = components;
		interactions_ = interactions;

		orderElements();
	}

	public DeploymentWithNetworkMinimizationConfig() {
		super(0, 0, 0);
	}

	public int scoreDeployment(DeploymentPlan plan) {
		boolean valid = true;

		ResourceResidual residual = new ResourceResidual(this);
		residual.deploy(plan);

		if (residual.valid()) {
			int[] capacity = new int[networks_[0].resources_.length];
			for (NetworkLink nl : networks_) {
				capacity = sum(capacity, residual.getResourceResiduals(nl));
			}
			int score = 0;
			for (int i = 0; i < capacity.length; i++) {
				int coeff = (networkResourceCoeffs_ == null) ? 1
						: networkResourceCoeffs_[i];
				score += (capacity[i] * coeff);
			}

			return score;
		} else {
			return -1
					* (residual.linkExhaustions_.size()
							+ residual.hostExhaustions_.size() + residual.disconnections_
							.size());
		}
	}

	public ValueFunction<VectorSolution> getFitnessFunction() {
		return fitnessFunction_;
	}

	public void setFitnessFunction(ValueFunction<VectorSolution> fitnessFunction) {
		fitnessFunction_ = fitnessFunction;
	}
}
