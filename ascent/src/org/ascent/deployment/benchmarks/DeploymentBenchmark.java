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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.DeploymentPlanner;
import org.ascent.deployment.Interaction;
import org.ascent.deployment.LocalHostLink;
import org.ascent.deployment.Node;
import org.ascent.deployment.ResourceResidual;

public class DeploymentBenchmark {
	
	protected DeploymentPlanner planner_;
	protected DeploymentConfig config_;
	/**
	 * This method takes a DeploymentPlanner as input and
	 * runs a suite of deployment benchmarks on it. The
	 * results are returned in a BenchmarkData object. 
	 * 
	 * @param planner
	 * @return
	 */
	public BenchmarkData test(DeploymentPlanner planner){

		planner_ = planner;
		long start = System.currentTimeMillis();
		DeploymentPlan plan = planner_.deploy(config_);
		long finish = System.currentTimeMillis();
		
		//Jakarta Monitoring library profiling
		
		BenchmarkData temp = executeTest(plan);
		temp.setTime(finish-start);
		return temp;
		
		
	}
	
	public DeploymentBenchmark (DeploymentConfig conf){
		config_ = conf;
	}
	
	
	public BenchmarkData executeTest(DeploymentPlan plan){
		
		BenchmarkData data = new BenchmarkData(config_);
		if (planner_ != null){
			data.setAlg(planner_.getClass().getName().substring(planner_.getClass().getName().lastIndexOf('.') + 1));
		}else{
			data.setAlg("Unknown");
		}
		
		
		int bw = 0;
		
		//There is a bug in the following loop for the rate calculation
        for (Interaction i : plan.getDeploymentConfiguration().getInteractions()) {
                if (!(plan.getChannel(i) instanceof LocalHostLink)) {
                        bw += (i.getResources()[0] * i.getRate());
                }
        }
        
        data.setBandwidthUsed(bw);
        int nodesFree = 0;
        ResourceResidual resid = new ResourceResidual(config_);
        
        resid.deploy(plan);
        for (Node n : plan.getDeploymentConfiguration().getNodes()) {
			if (resid.getHostedCount()[n.getId()] == 0) {
				++nodesFree;
			}
        }
        
        data.setScore(config_.scoreDeployment(plan));
        data.setTime(0);
        data.setNumNodes(plan.getDeploymentConfiguration().getNodes().length - nodesFree);
		data.setDeploymentPlan(plan);
		
		return data;
	}
	public static void writeToFile(BenchmarkData[] data){
		File file = new File("data.csv");
		
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("Components, Interactions, Algorithm, Nodes, Bandwidth");
			bw.newLine();
			
			for (int i = 0; i < data.length; ++i){
				bw.write(data[i].getComponents() + ", " + data[i].getInteractions() + ", " + data[i].getAlg() + ", " + data[i].getNumNodes() + ", " + data[i].getBandwidthUsed());
				bw.newLine();
			}
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
