package org.ascent.hamy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.ascent.VectorSolution;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.NetMinConfig;
import org.ascent.deployment.excel.ExcelDeploymentConfig;
import org.ascent.pso.Pso;

public class OptionalDeploymentRunner {
	private static final Logger log = Logger
			.getLogger(OptionalDeploymentRunner.class.getName());

	private static String INPUT_FILE = "data/problem_large_mod.xls";
	private static final File REQUIRED_CONFIG_STORAGE = new File(
			"data/reqired.xls");
	private static File OPTIONAL_CONFIG_STORAGE = new File("data/optional.xls");

	private static DeploymentPlan lastValidDeploymentPlan = null;

	public static void main(String[] args) throws Exception {
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		HashMap<String, ExcelComponent> optional_remaining = config
				.getAndTrimOptionalComponentIDs(new File(INPUT_FILE),
						REQUIRED_CONFIG_STORAGE);

		// Ensure we can deploy zero
		try {
			NetMinConfig reqConf = new NetMinConfig();
			ExcelDeploymentConfig excel = new ExcelDeploymentConfig();
			excel.load(REQUIRED_CONFIG_STORAGE, reqConf);
			reqConf.init();
			Pso pso = new Pso(reqConf);
			pso.setTotalParticles(20);
			pso.setVelocityMax(4);
			pso.setLocalLearningRate(0.5);
			pso.setGlobalLearningRate(2);
			pso.setIterations(20);
			VectorSolution packingOrder = pso.solve(reqConf
					.getFitnessFunction());
			DeploymentPlan plan = reqConf.getDeploymentPlan(packingOrder);
			if (plan.isValid())
				log.fine("Deployed required successfully");
			else {
				log.severe("ABORTING: Unable to deploy required components");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<ExcelComponent> optional_included = new ArrayList<ExcelComponent>();
		int optional_total = optional_remaining.size();
		// While we still have work to do
		while (optional_remaining.size() != 0) {
			// Get one optional component
			ExcelComponent optional_current_attempt = optional_remaining
					.remove(optional_remaining.keySet().iterator().next());

			log.fine("Attempting " + optional_current_attempt + " with "
					+ optional_included.toString());

			if (valid(optional_current_attempt, optional_included)) {
				optional_included.add(optional_current_attempt);
				log.fine("Included");
			} else
				log.fine("No Luck");
		}

		log.fine("Managed to include " + optional_included.size() + " out of "
				+ optional_total);

		log.fine("Deployment Plan is: \n"
				+ lastValidDeploymentPlan.toString(true));

	}

	public static boolean valid(final ExcelComponent current,
			final List<ExcelComponent> included) {
		log.finer("");

		NetMinConfig problem = new NetMinConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();

		try {
			config.loadOptionalComponents(current, included,
					REQUIRED_CONFIG_STORAGE, OPTIONAL_CONFIG_STORAGE);
			config.load(OPTIONAL_CONFIG_STORAGE, problem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		problem.init();

		log.finest("Initializing PSO with problem");
		double grate = 2;// the global learning rate
		double lrate = 0.5;// the local learning rate
		int maxv = 4;// the max particle velocity
		int particles = 20;// the total number of particles
		int iterations = 20;// the total number of iterations per solver
							// invocation

		Pso pso = new Pso(problem);
		pso.setTotalParticles(particles);
		pso.setVelocityMax(maxv);
		pso.setLocalLearningRate(lrate);
		pso.setGlobalLearningRate(grate);
		pso.setIterations(iterations);

		VectorSolution sol = pso.solve(problem.getFitnessFunction());
		if (sol == null)
			return false;

		DeploymentPlan plan = problem.getDeploymentPlan(sol);
		if (plan.isValid()) {
			lastValidDeploymentPlan = plan;
			return true;
		}
		return false;

	}

}
