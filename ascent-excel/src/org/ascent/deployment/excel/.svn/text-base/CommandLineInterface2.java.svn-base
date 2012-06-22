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

package org.ascent.deployment.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Comparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ascent.VectorSolution;
import org.ascent.VectorSolutionComparator;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.NetMinConfig;
import org.ascent.deployment.PSODeployer;
import org.ascent.deployment.RateMonotonicPessimisticResource;
import org.ascent.deployment.RateMonotonicResource;
import org.ascent.deployment.RateMonotonicResponseTimeResource;
import org.ascent.deployment.benchmarks.BenchmarkData;
import org.ascent.deployment.benchmarks.DeploymentBenchmark;
import org.ascent.deployment.excel.output.ExcelDeploymentPlan;
import org.ascent.deployment.output.HtmlUtil;
import org.ascent.pso.Pso;

public class CommandLineInterface2 {

	private static final String GENERATE_HTML = "html";
	private static final String PESSIMISTIC_69_4 = "pessimistic_69_4";
	private static final String HARMONIC = "harmonic";
	private static final String RESPONSE_TIME = "response-time";
	private static final String LIU_LAYLAND = "liu-layland";
	private static final String SCHEDULING_METHOD = "scheduling_method";
	private static final String POPULATION = "population";
	private static final String ITERATIONS = "iterations";
	public static final String OUTPUT_FILE = "output";
	public static final String INPUT_FILE = "file";

	
	
	public static void main(String[] args) {
		Options options = new Options();

		Option file = OptionBuilder
				.withArgName(INPUT_FILE)
				.hasArg()
				.withDescription(
						"the excel file containing the deployment problem specification")
				.create(INPUT_FILE);

		Option out = OptionBuilder
				.withArgName(OUTPUT_FILE)
				.hasArg()
				.withDescription(
						"the name of the file to write the generated deployment plan to")
				.create(OUTPUT_FILE);
		
		Option particlesopt = OptionBuilder
		.withArgName(POPULATION)
		.hasArg()
		.withDescription(
				"the total number of population members involved in the search")
		.create(POPULATION);
		
		Option iter  = OptionBuilder
		.withArgName(ITERATIONS)
		.hasArg()
		.withDescription(
				"the total number of search iterations")
		.create(ITERATIONS);
		
		Option sched  = OptionBuilder
		.withArgName(SCHEDULING_METHOD)
		.hasArg()
		.withDescription(
				"the method to use when calculating schedulability ("+LIU_LAYLAND+","+RESPONSE_TIME+","+HARMONIC+","+PESSIMISTIC_69_4+")")
		.create(SCHEDULING_METHOD);

		Option html  = OptionBuilder
		.withArgName(GENERATE_HTML)
		.hasArg()
		.withDescription(
				"should an html copy of the deployment plan be generated (true|false)")
		.create(GENERATE_HTML);


		options.addOption(file);
		options.addOption(out);
		options.addOption(particlesopt);
		options.addOption(iter);
		options.addOption(sched);
		options.addOption(html);

		CommandLine line = null;
		CommandLineParser parser = new GnuParser();
		try {
			line = parser.parse(options, args);

		} catch (ParseException exp) {
			System.err.println("Invalid arguments.  Reason: "
					+ exp.getMessage());
			return;
		}

		if (!line.hasOption(INPUT_FILE) || !line.hasOption(OUTPUT_FILE)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ScatterD", options);
			return;
		}

		String input = line.getOptionValue(INPUT_FILE);
		String output = line.getOptionValue(OUTPUT_FILE);
		int iterations = (line.hasOption(ITERATIONS))? Integer.parseInt(line.getOptionValue(ITERATIONS)) : 20; 
		int particles = (line.hasOption(POPULATION))? Integer.parseInt(line.getOptionValue(POPULATION)) : 20; 

		NetMinConfig problem = new NetMinConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		try {
			config.load(new File(input), problem);
		} catch (Exception e) {
			System.out.println("Error loading Excel file <"+input+">:" + e.getMessage());
			return;
		}

		if(line.hasOption(SCHEDULING_METHOD)){
			String method = line.getOptionValue(SCHEDULING_METHOD);
			if(method.equals(LIU_LAYLAND)){
				problem.getResourceConsumptionPolicies().put(0, new RateMonotonicResource());
			}
			else if(method.equals(RESPONSE_TIME)){
				problem.getResourceConsumptionPolicies().put(0, new RateMonotonicResponseTimeResource());
			}
			else if(method.equals(HARMONIC)){
				//nothing needs to be done...assume < 100% is schedulable
			}
			else if(method.equals(PESSIMISTIC_69_4)){
				problem.getResourceConsumptionPolicies().put(0, new RateMonotonicPessimisticResource());
			}
			else {
				System.out.println("Unknown scheduling method:"+method);
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("ScatterD", options);
				return;
			}
		}
		
		boolean genhtml = false;
		if(line.hasOption(GENERATE_HTML) && line.getOptionValue(GENERATE_HTML).equalsIgnoreCase("true"))
			genhtml = true;
		
		
		problem.init();

		PSODeployer pso = new PSODeployer();
		pso.setTotalParticles(particles);
		BenchmarkData data = (new DeploymentBenchmark(problem)).test(pso);
		

		try{
			ExcelDeploymentPlan.write(data.getDeploymentPlan(), new File(output));
			
			if(genhtml){
				String htmlout = HtmlUtil.toHtml(data);
				FileWriter fw = new FileWriter(output+".htm");
				fw.write(htmlout);
				fw.flush();
				fw.close();
			}
		}catch (Exception e) {
			System.err.println("Error writing deployment plan to disk!");
			e.printStackTrace();
		}
	}
}
