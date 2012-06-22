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

package org.ascent.deployment.benchmarks;

import org.ascent.Util;
import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.Node;
import org.ascent.deployment.ResourceResidual;

public class BenchmarkData {

	private int numNodes_ = 0;
	private int bandwidthUsed_ = 0;
	private long time_ = 0;
	private int score_;
	private String alg_ = "";
	private DeploymentConfig config_;
	private DeploymentPlan deploymentPlan_;

	public BenchmarkData(DeploymentConfig config) {
		config_ = config;
	}

	public int getNumNodes() {
		return numNodes_;
	}

	public void setNumNodes(int numNodes) {
		numNodes_ = numNodes;
	}

	public int getBandwidthUsed() {
		return bandwidthUsed_;
	}

	public void setBandwidthUsed(int bandwidthUsed) {
		bandwidthUsed_ = bandwidthUsed;
	}

	public String getAlg() {
		return alg_;
	}

	public void setAlg(String alg) {
		alg_ = alg;
	}

	public int getComponents() {
		return config_.getComponents().length;
	}

	public int getInteractions() {
		return config_.getInteractions().length;
	}

	public long getTime() {
		return time_;
	}

	public void setTime(long time) {
		time_ = time;
	}

	public DeploymentConfig getConfig() {
		return config_;
	}

	public void setConfig(DeploymentConfig config) {
		config_ = config;
	}

	public DeploymentPlan getDeploymentPlan() {
		return deploymentPlan_;
	}

	public void setDeploymentPlan(DeploymentPlan deploymentPlan) {
		deploymentPlan_ = deploymentPlan;
	}

	public int getScore() {
		return score_;
	}

	public void setScore(int score) {
		score_ = score;
	}

	/**
	 * Chris, make sure and provide a nice implementation of this method.
	 */
	public String toString() {
		
		ResourceResidual resid = new ResourceResidual(config_);
		resid.deploy(deploymentPlan_);

		String output = "Benchmark Data for " + alg_ + "\n";
		output += "----------------------------------\n";
		output += "Planning Time: " + time_ + "(ms)\n";
		output += "Score: "+score_+"\n";
		output += "Bandwidth Used: " + bandwidthUsed_ + "\n";
		output += "Nodes: " + numNodes_ + "\n";
		output += "----------------------------------\n";
		output += "Deployment Plan:\n";
		if (!deploymentPlan_.isValid())
			output += "!!! Invalid Deployment Plan !!!\n";
		for (Node n : config_.getNodes()) {
			output += n.getLabel() + " Residuals:"+ Util.toString(resid.getResourceResiduals(n))+" {";
			Component[] hosted = deploymentPlan_.getHostedComponents(n);
			for (int i = 0; i < hosted.length; i++) {
				output += "\n  " + hosted[i];
				if (i == hosted.length - 1)
					output += "\n";
			}
			output += "}\n";
		}
		return output;
	}
}
