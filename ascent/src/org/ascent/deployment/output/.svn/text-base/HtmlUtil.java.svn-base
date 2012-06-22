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


package org.ascent.deployment.output;

import org.ascent.Util;
import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.Node;
import org.ascent.deployment.ResourceResidual;
import org.ascent.deployment.benchmarks.BenchmarkData;

public class HtmlUtil {

	public static String toHtml(DeploymentPlan plan){
		String html = "<div><h2>Deployment Plan:</h2>";
		
		Component[] comps = plan.getDeploymentConfiguration()
		.getComponents();
		Node[] nodes = plan.getDeploymentConfiguration().getNodes();
		
		for(Node n : nodes){
			
			html += "<p><b>"+n.getLabel()+"</b>";
			
			Component[] hosted = plan.getHostedComponents(n);
			if(hosted == null || hosted.length == 0){
			   html +=  "is idle.";	
			}
			else {
				html +=  " hosts:<ul>";
			for(Component c : hosted){
				html += "<li>"+c+"</li>";
			}
			}
			html += "</ul></p>";
		}
		
		html += "</div>";
		return html;
	}
	
	public static String toHtml(BenchmarkData data){
		String html = "<div><h2>Deployment Benchmark Results:</h2>";
		
		ResourceResidual resid = new ResourceResidual(data.getConfig());
		resid.deploy(data.getDeploymentPlan());

		String output = "<b>Benchmark Data for " + data.getAlg() + "</b><br/>\n";
		output += "<hr/>\n";
		output += "<b>Planning Time: " + data.getTime() + "(ms)</b><br/>\n";
		output += "<b>Score: "+data.getScore()+"</b><br/>\n";
		output += "<b>Bandwidth Used: " + data.getBandwidthUsed() + "</b><br/>\n";
		output += "<b>Nodes Used: " + data.getNumNodes() + "</b><br/>\n";
		output += "<hr/>\n";
		output += "<b>Deployment Plan:</b><br/>\n";
		output += "<ul>";
		if (!data.getDeploymentPlan().isValid())
			output += "!!! Invalid Deployment Plan !!!<br/>\n";
		for (Node n : data.getConfig().getNodes()) {
			Component[] hosted = data.getDeploymentPlan().getHostedComponents(n);
			output += "<p><b>"+n.getLabel() + "</b> Residual Resources:"+ Util.toString(resid.getResourceResiduals(n));
			if(hosted.length > 0){
				output += ", Hosted Components: <ul>";
			}
			else{
				output += ", IDLE";
			}
			
			for (int i = 0; i < hosted.length; i++) {
				output += "<li>" + hosted[i] + "</li>";
			}
			if(hosted.length > 0){
				output += "</ul>";
			}
			output += "</p>";
		}
		
		output += "<hr/>";
		html += "</div>";
		return output;
	}
}
