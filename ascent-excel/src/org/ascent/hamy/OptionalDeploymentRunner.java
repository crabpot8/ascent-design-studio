package org.ascent.hamy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ascent.VectorSolution;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.NetMinConfig;
import org.ascent.deployment.excel.ExcelDeploymentConfig;
import org.ascent.deployment.excel.ExcelDeploymentConfig.ExcelComponent;
import org.ascent.pso.Pso;

public class OptionalDeploymentRunner {
	private static final Logger log = Logger.getLogger(OptionalDeploymentRunner.class.getName());

	private static String INPUT_FILE = "data/problem_large_mod.xls";

	public static void main(String[] args) throws Exception {
		log.fine("Starting");
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		HashMap<String, ExcelComponent> optional_remaining = config
				.getAndTrimOptionalComponentIDs(new File(INPUT_FILE));

		List<ExcelComponent> optional_included = new ArrayList<ExcelComponent>();

		// While we still have work to do
		while (optional_remaining.size() != 0) {
			// Get one optional component
			ExcelComponent optional_current_attempt = optional_remaining
					.remove(optional_remaining.keySet().iterator().next());

			log.fine("Attempting " + optional_current_attempt
					+ " with " + optional_included.toString());

			if (valid(optional_current_attempt, optional_included)) {
				optional_included.add(optional_current_attempt);
				log.fine("Included");
			} else
				log.fine("No Luck");
		}

	}

	public static boolean valid(final ExcelComponent current,
			final List<ExcelComponent> included) {
		log.finer("");
		
		NetMinConfig problem = new NetMinConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();

		try {
			config.loadOptionalComponents(current, included);
			config.load(new File("data/temp2.xls"), problem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		problem.init();

		log.finest("Initializing PSO with problem");
		Pso pso = new Pso(problem);
		pso.setTotalParticles(200);
		pso.setIterations(2);

		VectorSolution sol = pso.solve(problem.getFitnessFunction());
		DeploymentPlan plan = problem.getDeploymentPlan(sol);

		if (plan.isValid()) 
			return true;
		return false;
		
	}

}
