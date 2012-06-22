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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
public class EnergyMinConfig extends DeploymentConfig {

	private ValueFunction<VectorSolution> fitnessFunction_ = new ValueFunction<VectorSolution>() {

		public double getValue(VectorSolution src) {
			if (deployer_ == null){
				deployer_ = new OrderedDeployer(EnergyMinConfig.this);
				deployer_.setRandomizeNodes(true);
			}

			if (src.getArtifact() == null) {
				DeploymentPlan plan = null;
				if (usePackingOrderVectors_)
					plan = deployer_.deploy(src);
				else
					plan = new DeploymentPlan(EnergyMinConfig.this, src);
				int score = scoreDeployment(plan);
				src.setArtifact(score);
			}
			return (Integer) src.getArtifact();
		}
	};

	private boolean sortCPUSOnPower_ = true;
	private double communicationScalingValue_ = 0.000001;
	private double cpuScalingValue_ = 0.000001;
	
	private int baseScore_ = Integer.MAX_VALUE;
	private boolean usePackingOrderVectors_ = true;
	private Map<NetworkLink, Double> communicationCost_ = new HashMap<NetworkLink, Double>();
	private Map<Node, Double> cpuCost_ = new HashMap<Node, Double>();

	private OrderedDeployer deployer_;

	public EnergyMinConfig(DeploymentConfig toclone) {
		super(toclone);
	}

	public void setComponents(Component[] components) {
		components_ = components;
		orderElements();
	}

	public EnergyMinConfig(Node[] nodes, NetworkLink[] networks,
			Component[] components, Interaction[] interactions) {
		super(components.length, 0, nodes.length - 1);
		nodes_ = nodes;
		networks_ = networks;
		components_ = components;
		interactions_ = interactions;

		orderElements();
	}

	public EnergyMinConfig() {
		super(0, 0, 0);
	}

	public void init() {
		super.init();

		boundaries_ = new int[components_.length][2];
		for (int i = 0; i < boundaries_.length; i++) {
			boundaries_[i] = new int[] { 0, components_.length - 1 };
		}
	}

	public DeploymentPlan getDeploymentPlan(VectorSolution vs) {
		if (usePackingOrderVectors_) {
			if (deployer_ == null) {
				deployer_ = new OrderedDeployer(this);
				deployer_.setRandomizeNodes(true);
			}
			return deployer_.deploy(vs);
		} else {
			return new DeploymentPlan(this,vs);
		}
	}

	public double getCommunicationCost(Interaction i, NetworkLink chnl) {
		return i.getRate() * i.getSize()[0] * communicationCost_.get(chnl);
	}

	public double getCPUSavings(Component c, Node n) {
		double cost = 0;
		for (RealTimeTask t : c.getRealTimeTasks()) {
			cost += (1/t.getPeriod()) * t.getUtilization();
		}
		double savings = 100 - cost;
		savings = savings * cpuCost_.get(n);
		return cost;
	}
	
//	public double getTotalCPUCost(Node n, DeploymentPlan p){
//		Component[] comps = p.getHostedComponents(n);
//		
//	}
	

	public int scoreDeployment(DeploymentPlan plan) {
		if (plan.isValid()) {
			double score = 0;
			for (Interaction i : interactions_) {
				NetworkLink chnl = plan.getChannel(i);
				if (chnl instanceof LocalHostLink) {
					score += ((i.getResources()[0] * i.getRate() * 8) * 0.000005);
				}
			}
			for (Component c : plan.getDeploymentConfiguration()
					.getComponents()) {
				Node n = plan.getHost(c);
				score += (getCPUSavings(c, n) );
			}
			for (Node n : plan.getDeploymentConfiguration().getNodes()){
				if(plan.getHostedComponents(n).length < 1){
					score += cpuCost_.get(n);
				}
			}
			score = score / 1000;
			return (int)Math.rint(score);
		} else {
			ResourceResidual residual = new ResourceResidual(this);
			residual.deploy(plan);
			return -100000000
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
	
	public void updatePowerConsumptions(){
		if(sortCPUSOnPower_){
		Arrays.sort(nodes_,new Comparator<Node>() {
		
			public int compare(Node o1, Node o2) {
				double costd = cpuCost_.get(o1) - cpuCost_.get(o2);
				if(costd > 0)
					return 1;
				else if (costd < 0)
					return -1;
				else
					return 0;
			}
		});
		}
	}

	public void setFitnessFunction(ValueFunction<VectorSolution> fitnessFunction) {
		fitnessFunction_ = fitnessFunction;
	}

	public Map<NetworkLink, Double> getCommunicationCostTable() {
		return communicationCost_;
	}

	public void setCommunicationCostTable(
			Map<NetworkLink, Double> communicationCost) {
		this.communicationCost_ = communicationCost;
	}

	public Map<Node, Double> getCPUCostTable() {
		return cpuCost_;
	}

	public void setCPUCostTable(Map<Node, Double> cpuCost) {
		this.cpuCost_ = cpuCost;
	}

	public boolean usingPackingOrderVectors() {
		return usePackingOrderVectors_;
	}

	public void setUsePackingOrderVectors(boolean usePackingOrderVectors) {
		usePackingOrderVectors_ = usePackingOrderVectors;
	}

	public double getEnergyConsumption(double score){
		return baseScore_ - score;
	}

	public double getCommunicationScalingValue() {
		return communicationScalingValue_;
	}

	public void setCommunicationScalingValue(double communicationScalingValue) {
		communicationScalingValue_ = communicationScalingValue;
	}

	public double getCpuScalingValue() {
		return cpuScalingValue_;
	}

	public void setCpuScalingValue(double cpuScalingValue) {
		cpuScalingValue_ = cpuScalingValue;
	}

	public boolean isSortingCPUSOnPower() {
		return sortCPUSOnPower_;
	}

	public void setSortCPUSOnPower(boolean sortCPUSOnPower) {
		sortCPUSOnPower_ = sortCPUSOnPower;
	}
	
}
