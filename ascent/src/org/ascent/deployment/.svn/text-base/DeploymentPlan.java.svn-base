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

import org.ascent.VectorSolution;

public class DeploymentPlan {
	private Map<Node,Integer> nodeLookup_;
	private Map<Component, Integer> componentLookup_;
	private VectorSolution solution_;
	private DeploymentConfig deploymentConfiguration_;
	
	public DeploymentPlan(DeploymentConfig config, VectorSolution solution) {
		super();
		solution_ = solution;
		deploymentConfiguration_ = config;
		
		initComponentLookup();
		initNodeLookup();
	}
	
	public DeploymentPlan(DeploymentConfig conf, Map<Component,Node> plan){
		super();
		deploymentConfiguration_ = conf;
		int[] sol = new int[plan.size()];
		
		initComponentLookup();
		initNodeLookup();
		
		for(int i = 0; i < conf.getComponents().length; i++)
			sol[i] = nodeLookup_.get(plan.get(conf.getComponents()[i]));
		
		solution_ = new VectorSolution(sol);
	}
	
	protected void initComponentLookup(){
		componentLookup_ = new HashMap<Component, Integer>();
		for(int i = 0; i < deploymentConfiguration_.getComponents().length; i++)
			componentLookup_.put(deploymentConfiguration_.getComponents()[i], i);
	}
	
	protected void initNodeLookup(){
		nodeLookup_ = new HashMap<Node, Integer>();
		for(int i = 0; i < deploymentConfiguration_.getNodes().length; i++)
			nodeLookup_.put(deploymentConfiguration_.getNodes()[i], i);
	}

	public VectorSolution getSolution() {
		return solution_;
	}

	public void setSolution(VectorSolution solution) {
		solution_ = solution;
	}

	public DeploymentConfig getDeploymentConfiguration() {
		return deploymentConfiguration_;
	}

	public void setDeploymentConfiguration(DeploymentConfig deploymentConfiguration) {
		deploymentConfiguration_ = deploymentConfiguration;
	}

	public Node getHost(Component c) {
		int hostid = solution_.getPosition()[c.id_];
		return deploymentConfiguration_.getNodes()[hostid];
	}
	
	public void moveTo(Component c, Node host){
		solution_.getPosition()[componentLookup_.get(c)] = nodeLookup_.get(host);
	}

	public NetworkLink getChannel(Interaction i) {
		Node[] hosts = getHosts(i.getParticipants());
		NetworkLink[] links = deploymentConfiguration_.getLinks(hosts);
		if (links.length > 0)
			return links[0];
		else
			return null;
	}

	public Node[] getHosts(Component[] comps) {
		List<Node> nodes = new ArrayList<Node>();
		for (Component c : comps) {
			Node n = getHost(c);
			if (!nodes.contains(n)) {
				nodes.add(n);
			}
		}
		return nodes.toArray(new Node[0]);
	}

	public Component[] getHostedComponents(Node n) {
		List<Component> hosted = new ArrayList<Component>();
		for (int i = 0; i < solution_.getPosition().length; i++) {
			int host = solution_.getPosition()[i];
			if (host == n.id_) {
				hosted.add(deploymentConfiguration_.getComponents()[i]);
			}
		}
		return hosted.toArray(new Component[0]);
	}
	
	public boolean isValid(){
		ResourceResidual resid = new ResourceResidual(deploymentConfiguration_);
		resid.deploy(this);
		if(!resid.valid())
			return false;
		
		for(DeploymentConstraint con : deploymentConfiguration_.getConstraints())
			if(!con.isEnforced(this))
				return false;
		
		return true;
	}
	
	public String toString(){
		return toString(false);
	}
	
	public DeploymentPlan clonePlan(){
		int[] pos = solution_.getPosition();
		int[] cpos = new int[pos.length];
		System.arraycopy(pos, 0, cpos, 0, cpos.length);
		DeploymentPlan plan = new DeploymentPlan(deploymentConfiguration_,new VectorSolution(cpos));
		return plan;
	}

	public String toString(boolean showlinks) {
		String str = "Deployment Plan {\n";
		for (Component c : deploymentConfiguration_.getComponents()) {
			str += "\t\t" + c.label_ + "-->" + getHost(c).label_ + "\n";
		}
		if (showlinks) {
			for (Interaction inter : deploymentConfiguration_.getInteractions()) {
				NetworkLink l = getChannel(inter);
				if (l != null) {
					str += "\t\t" + inter.label_ + "-->" + l.label_ + "\n";
				} else {
					str += "\t\t" + inter.label_ + "--> Disconnected\n";
				}
			}
		}
		str += "}\n";
		return str;
	}
}
