package org.ascent.deployment.excel.test;

import java.io.File;
import java.util.Comparator;

import org.ascent.VectorSolution;
import org.ascent.VectorSolutionComparator;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.NetMinConfig;
import org.ascent.deployment.NetworkBandwidthMinimizingPlanner;
import org.ascent.deployment.NetworkGravityOptimizer;
import org.ascent.deployment.OrderedDeployer;
import org.ascent.deployment.excel.ExcelDeploymentConfig;
import org.ascent.genetic.GeneticAlgorithm;
import org.ascent.pso.Pso;

import junit.framework.TestCase;

/******************************************************************************
 * Copyright (c) 2007 Jules White.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jules White - initial API and implementation 
 ****************************************************************************/
public class NetMinConfigLargeDeploymentConfigTest extends TestCase {

	public void testWorkbookLoad(){
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		try{
			config.load(new File("../ascent-data/xls/large.xls"), new NetworkBandwidthMinimizingPlanner());
		}catch (Exception e) {
			e.printStackTrace();
			fail("An exception was thrown loading the workbook and should not have been.");
		}
	}
	
	public void testPSO(){
		NetMinConfig problem = new NetMinConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		try{
			config.load(new File("../ascent-data/xls/large.xls"), problem);
		}catch (Exception e) {
			e.printStackTrace();
			fail("An exception was thrown loading the workbook and should not have been.");
		}
		
		
		problem.init();
		
		OrderedDeployer deployer = new OrderedDeployer(problem);
		
		
		long time = 0;
		VectorSolution best = null;
		for(int i=0; i < 1; i++){
			long start = System.currentTimeMillis();
			double grate = 2;//the global learning rate
			double lrate = 0.5;//the local learning rate
			double intertia = 1;//the particle intertia impact
			int maxv = 4;//the max particle velocity
			int particles = 20;//the total number of particles
			int iterations = 20;//the total number of iterations per solver invocation
			
			Pso pso = new Pso(problem);
			pso.setTotalParticles(particles);
			pso.setVelocityMax(maxv);
			pso.setLocalLearningRate(lrate);
			pso.setGlobalLearningRate(grate);
			pso.setIterations(iterations);

			
			Comparator<VectorSolution> comp = new VectorSolutionComparator(problem.getFitnessFunction());
			VectorSolution sol = pso.solve(problem.getFitnessFunction());
			assertNotNull(sol);
			time += (System.currentTimeMillis()-start);
			
			
			DeploymentPlan plan = deployer.deploy(sol);
						
			
//			assertTrue(plan.isValid());
			
			boolean better = false;
			if(best != null){
				if(comp.compare(sol,best) > 0){
					best = sol;
					better = true;
				}
			}
			else {
				best = sol;
			}
			problem.printSolutionStats(plan);
			System.out.println("Total Time:"+time);
			
//			System.out.println("### Squeezing with Network Gravity ###");
//			NetworkGravityOptimizer opt = new NetworkGravityOptimizer();
//			opt.optimize(plan);
//			problem.printSolutionStats(plan.getSolution());
			
		}
		
//		System.out.println("### Network Gravity from Scratch ###");
//		int[] sol = new int[problem.getComponents().length];
//		for(int i = 0; i < sol.length; i++){
//			sol[i] = i;
//		}
//		DeploymentPlan plan = new DeploymentPlan(problem, new VectorSolution(sol));
//		NetworkGravityOptimizer opt = new NetworkGravityOptimizer();
//		opt.optimize(plan);
//		problem.printSolutionStats(plan.getSolution());
	}
	
	public void testGenetic(){
		NetMinConfig problem = new NetMinConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		try{
			config.load(new File("../ascent-data/xls/large.xls"), problem);
		}catch (Exception e) {
			e.printStackTrace();
			fail("An exception was thrown loading the workbook and should not have been.");
		}
		
		
		problem.init();
		
		OrderedDeployer deployer = new OrderedDeployer(problem);
		
		
		long time = 0;
		VectorSolution best = null;
		for(int i=0; i < 1; i++){
			long start = System.currentTimeMillis();
			double grate = 2;//the global learning rate
			double lrate = 0.5;//the local learning rate
			double intertia = 1;//the particle intertia impact
			int maxv = 4;//the max particle velocity
			int particles = 20;//the total number of particles
			int iterations = 20;//the total number of iterations per solver invocation
			
			GeneticAlgorithm alg = new GeneticAlgorithm(problem);
			alg.setPopulationSize(200);
			alg.setGenerations(2);

			
			Comparator<VectorSolution> comp = new VectorSolutionComparator(problem.getFitnessFunction());
			VectorSolution sol = alg.solve(problem.getFitnessFunction());
			assertNotNull(sol);
			time += (System.currentTimeMillis()-start);
			
			
			DeploymentPlan plan = deployer.deploy(sol);
						
			
//			assertTrue(plan.isValid());
			
			boolean better = false;
			if(best != null){
				if(comp.compare(sol,best) > 0){
					best = sol;
					better = true;
				}
			}
			else {
				best = sol;
			}
			problem.printSolutionStats(plan);
			System.out.println("Total Time:"+time);
			
//			System.out.println("### Squeezing with Network Gravity ###");
//			NetworkGravityOptimizer opt = new NetworkGravityOptimizer();
//			opt.optimize(plan);
//			problem.printSolutionStats(plan.getSolution());
			
		}
		
//		System.out.println("### Network Gravity from Scratch ###");
//		int[] sol = new int[problem.getComponents().length];
//		for(int i = 0; i < sol.length; i++){
//			sol[i] = i;
//		}
//		DeploymentPlan plan = new DeploymentPlan(problem, new VectorSolution(sol));
//		NetworkGravityOptimizer opt = new NetworkGravityOptimizer();
//		opt.optimize(plan);
//		problem.printSolutionStats(plan.getSolution());
	}
}
