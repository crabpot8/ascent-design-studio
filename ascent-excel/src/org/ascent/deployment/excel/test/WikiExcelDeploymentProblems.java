package org.ascent.deployment.excel.test;

import java.io.File;
import java.util.Comparator;

import junit.framework.TestCase;

import org.ascent.VectorSolution;
import org.ascent.VectorSolutionComparator;
import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.BandwidthMinimizingPSODeploymentPlanner;
import org.ascent.deployment.NetworkBandwidthMinimizingPlanner;
import org.ascent.deployment.excel.ExcelDeploymentConfig;
import org.ascent.pso.Pso;

/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/
public class WikiExcelDeploymentProblems extends TestCase {
	public void testExample() {
		DeploymentConfig problem = new DeploymentConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		try {
			config.load(new File("data/test.xls"), problem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		problem.init();

		
		DeploymentPlan plan = BandwidthMinimizingPSODeploymentPlanner.solveForMinimalBandwidthConsumption(problem);

		
		assertTrue(plan.isValid());
		
		if (plan.isValid()) {
			for (Component c : problem.getComponents()) {
				System.out.println("Deploy " + c.getLabel() + " to "
						+ plan.getHost(c).getLabel());
			}
		} else {
			System.out.println("No solution found.");
		}
	}
	
	public void testDetailedExample() {
		NetworkBandwidthMinimizingPlanner problem = new NetworkBandwidthMinimizingPlanner();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		try {
			config.load(new File("data/test.xls"), problem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		problem.init();

		double grate = 2;// the global learning rate
		double lrate = 0.5;// the local learning rate
		double intertia = 1;// the particle intertia impact
		int maxv = 4;// the max particle velocity
		int particles = 200;// the total number of particles
		int iterations = 20;// the total number of iterations per solver
							// invocation

		Pso pso = new Pso(problem);
		pso.setTotalParticles(particles);
		pso.setVelocityMax(maxv);
		pso.setLocalLearningRate(lrate);
		pso.setGlobalLearningRate(grate);
		pso.setIterations(20);

		Comparator<VectorSolution> comp = new VectorSolutionComparator(problem
				.getFitnessFunction());
		VectorSolution sol = pso.solve(problem.getFitnessFunction());
		assertNotNull(sol);
		
		DeploymentPlan plan = new DeploymentPlan(problem, sol);

		
		assertTrue(plan.isValid());
		
		if (plan.isValid()) {
			for (Component c : problem.getComponents()) {
				System.out.println("Deploy " + c.getLabel() + " to "
						+ plan.getHost(c).getLabel());
			}
		} else {
			System.out.println("No solution found.");
		}
	}
}
