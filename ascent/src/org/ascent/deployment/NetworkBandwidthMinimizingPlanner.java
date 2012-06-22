package org.ascent.deployment;

import org.ascent.VectorSolution;


public class NetworkBandwidthMinimizingPlanner extends
		DeploymentWithNetworkMinimizationConfig {

	
	public NetworkBandwidthMinimizingPlanner() {
		super();
	}

	public NetworkBandwidthMinimizingPlanner(DeploymentConfig toclone) {
		super(toclone);
	}

	public NetworkBandwidthMinimizingPlanner(Node[] nodes,
			NetworkLink[] networks, Component[] components,
			Interaction[] interactions) {
		super(nodes, networks, components, interactions);
	}

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
	}
	
	public int scoreDeployment(VectorSolution sol){
		return scoreDeployment(new DeploymentPlan(this,sol));
	}

}
