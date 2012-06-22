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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceResidual {
	private DeploymentConfig deploymentConfiguration_;
	protected List<Interaction> disconnections_ = new ArrayList<Interaction>();
	protected List<NetworkLink> linkExhaustions_ = new ArrayList<NetworkLink>();
	protected List<Node> hostExhaustions_ = new ArrayList<Node>();
	protected Map<Node, List<Component>> hostedMap_ = new HashMap<Node, List<Component>>();

	private int[][] nodeResiduals_;
	private int[][] networkResiduals_;
	private int[] hostedCount_;

	public ResourceResidual(DeploymentConfig config) {
		deploymentConfiguration_ = config;
		nodeResiduals_ = new int[deploymentConfiguration_.getNodes().length][deploymentConfiguration_.getNodes()[0].resources_.length];
		for (Node n : deploymentConfiguration_.getNodes()) {
			int[] resid = new int[n.resources_.length];
			System.arraycopy(n.resources_, 0, resid, 0, resid.length);
			// nodeResiduals_.put(n, resid);
			nodeResiduals_[n.id_] = resid;
		}

		networkResiduals_ = new int[deploymentConfiguration_.getNetworks().length][deploymentConfiguration_.getNetworks()[0].resources_.length];
		for (NetworkLink n : deploymentConfiguration_.getNetworks()) {
			int[] resid = new int[n.resources_.length];
			System.arraycopy(n.resources_, 0, resid, 0, resid.length);
			networkResiduals_[n.id_] = resid;
		}
	}

	public List<Component> getHosted(Node n) {
		List<Component> hosted = hostedMap_.get(n);
		if (hosted == null) {
			hosted = new ArrayList<Component>();
			hostedMap_.put(n, hosted);
		}
		return hosted;
	}

	public List<Interaction> getDisconnections() {
		return disconnections_;
	}

	public void setDisconnections(List<Interaction> disconnections) {
		disconnections_ = disconnections;
	}

	public List<NetworkLink> getLinkExhaustions() {
		return linkExhaustions_;
	}

	public void setLinkExhaustions(List<NetworkLink> linkExhaustions) {
		linkExhaustions_ = linkExhaustions;
	}

	public List<Node> getHostExhaustions() {
		return hostExhaustions_;
	}

	public void setHostExhaustions(List<Node> hostExhaustions) {
		hostExhaustions_ = hostExhaustions;
	}

	public int[][] getNodeResiduals() {
		return nodeResiduals_;
	}

	public void setNodeResiduals(int[][] nodeResiduals) {
		nodeResiduals_ = nodeResiduals;
	}

	public int[][] getNetworkResiduals() {
		return networkResiduals_;
	}

	public void setNetworkResiduals(int[][] networkResiduals) {
		networkResiduals_ = networkResiduals;
	}

	public int[] getHostedCount() {
		return hostedCount_;
	}

	public void setHostedCount(int[] hostedCount) {
		hostedCount_ = hostedCount;
	}

	public void deploy(Interaction inter, NetworkLink nl) {
		int[] resid = deploymentConfiguration_.residuals(getResourceResiduals(nl), inter.resources_);
		// networkResiduals_.put(nl, resid);
		networkResiduals_[nl.id_] = resid;
	}

	public void deploy(Component c, Node n) {
		int[] resid = deploymentConfiguration_.getPacker().insert(c, getHosted(n), n);
		getHosted(n).add(c);
		nodeResiduals_[n.id_] = resid;
	}

	public int[] getResourceResiduals(Node n) {
		// return nodeResiduals_.get(n);
		return nodeResiduals_[n.id_];
	}

	public int[] getResourceResiduals(NetworkLink nl) {
		// return networkResiduals_.get(nl);
		return networkResiduals_[nl.id_];
	}

	public void deploy(DeploymentPlan plan) {
		hostedCount_ = new int[deploymentConfiguration_.getNodes().length];

		for (Component c : deploymentConfiguration_.getComponents()) {
			Node n = plan.getHost(c);
			hostedCount_[n.id_]++;
			deploy(c, n);
		}
		for (Interaction inter : deploymentConfiguration_.getInteractions()) {
			NetworkLink l = plan.getChannel(inter);
			if (l == null) {
				disconnections_.add(inter);
			} else {
				deploy(inter, l);
			}
		}
		for (Node n : deploymentConfiguration_.getNodes()) {
			if (exhausted(getResourceResiduals(n))) {
				hostExhaustions_.add(n);
			}
		}
		for (NetworkLink link : deploymentConfiguration_.getNetworks()) {
			if (exhausted(getResourceResiduals(link))) {
				linkExhaustions_.add(link);
			}
		}
	}

	public boolean exhausted(int[] residual) {
		for (int i = 0; i < residual.length; i++) {
			if (residual[i] < 0)
				return true;
		}
		return false;
	}

	public boolean valid() {
		return linkExhaustions_.size() == 0 && disconnections_.size() == 0
				&& hostExhaustions_.size() == 0;
	}

	public String toString() {
		String str = "Residual Resources {\n";

		for (Node n : deploymentConfiguration_.getNodes()) {
			int[] resid = getResourceResiduals(n);
			str += "\t\t"
					+ n.label_
					+ "("
					+ n.id_
					+ ")"
					+ DeploymentWithNetworkMinimizationConfig
							.toString(resid) + "\n";
		}
		for (NetworkLink n : deploymentConfiguration_.getNetworks()) {
			int[] resid = getResourceResiduals(n);
			str += "\t\t-->"
					+ n.label_
					+ "("
					+ n.id_
					+ ")"
					+ DeploymentWithNetworkMinimizationConfig
							.toString(resid) + "\n";
		}
		str += "}\n";
		return str;
	}
}