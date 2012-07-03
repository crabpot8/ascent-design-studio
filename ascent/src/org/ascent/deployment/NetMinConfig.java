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

import java.util.logging.Logger;

import org.ascent.VectorSolution;
import org.ascent.binpacking.ValueFunction;

/**
 * NetMinConfig is a DeploymentConfiguration that represents a solution as a
 * packing order for components. NetMinConfig generally performs much better
 * than representing actual deployment locations.
 * 
 * @author jules
 * 
 */
public class NetMinConfig extends DeploymentConfig {
	private static final Logger log = Logger.getLogger(NetMinConfig.class
			.getName());

	private ValueFunction<VectorSolution> fitnessFunction_ = new ValueFunction<VectorSolution>() {

		public double getValue(VectorSolution src) {
			log.finest("Scoring a VectorSolution");
			if (deployer_ == null)
				deployer_ = new OrderedDeployer(NetMinConfig.this);

			if (src.getArtifact() == null) {
				DeploymentPlan plan = deployer_.deploy(src);
				int score = scoreDeployment(plan);
				src.setArtifact(score);
			}
			return (Integer) src.getArtifact();
		}

		private final Logger log = Logger.getLogger(NetMinConfig.class
				.getName() + "$" + ValueFunction.class.getCanonicalName());
	};

	private OrderedDeployer deployer_;
	
	public NetMinConfig() {
		super(0, 0, 0);
		log.finer("");
	}
	
	public NetMinConfig(DeploymentConfig toclone) {
		super(toclone);
		log.finer("");
		
	}

	@Override
	public void setComponents(Component[] components) {
		components_ = components;
		orderElements();
	}

	public NetMinConfig(Node[] nodes, NetworkLink[] networks,
			Component[] components, Interaction[] interactions) {
		super(components.length, 0, nodes.length - 1);
		nodes_ = nodes;
		networks_ = networks;
		components_ = components;
		interactions_ = interactions;

		orderElements();
	}

	@Override
	public void init() {
		log.finer("");
		super.init();

		boundaries_ = new int[components_.length][2];
		for (int i = 0; i < boundaries_.length; i++) {
			if (components_[i].isOptional())
				boundaries_[i] = new int[] { -1, components_.length - 1 };
			else
				boundaries_[i] = new int[] { 0, components_.length - 1 };
		}
	}

	@Override
	public DeploymentPlan getDeploymentPlan(VectorSolution vs) {
		if (deployer_ == null) {
			deployer_ = new OrderedDeployer(this);
		}
		return deployer_.deploy(vs);
	}

	@Override
	public ValueFunction<VectorSolution> getFitnessFunction() {
		return fitnessFunction_;
	}

	@Override
	public int scoreDeployment(DeploymentPlan plan) {
		if (plan.isValid()) {
			int score = 0;
			for (Interaction i : interactions_) {
				if (plan.getChannel(i) instanceof LocalHostLink) {
					score += (i.getResources()[0] * i.getRate());
				}
			}
			return score;
		} else {
			ResourceResidual residual = new ResourceResidual(this);
			residual.deploy(plan);
			return -1
					* (residual.getLinkExhaustions().size()
							+ residual.getHostExhaustions().size() + residual
							.getDisconnections().size());
		}
		// boolean valid = true;
		//
		// ResourceResidual residual = new ResourceResidual(this);
		// residual.deploy(plan);
		//
		// if (residual.valid()) {
		// int[] capacity = new int[networks_[0].resources_.length];
		// for (NetworkLink nl : networks_) {
		// capacity = sum(capacity, residual.getResourceResiduals(nl));
		// }
		// int score = 0;
		// for (int i = 0; i < capacity.length; i++) {
		// int coeff = (networkResourceCoeffs_ == null) ? 1
		// : networkResourceCoeffs_[i];
		// score += (capacity[i] * coeff);
		// }
		//
		// return score;
		// } else {
		// return -1
		// * (residual.linkExhaustions_.size()
		// + residual.hostExhaustions_.size() + residual.disconnections_
		// .size());
		// }
	}

}
