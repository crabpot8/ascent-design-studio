package org.ascent.hamy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ascent.VectorSolution;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.NetMinConfig;
import org.ascent.deployment.excel.ExcelDeploymentConfig;
import org.ascent.deployment.excel.ExcelDeploymentConfig.ExcelComponent;
import org.ascent.pso.Pso;

public class OptionalDeploymentRunner {

	private static String INPUT_FILE = "data/problem_large_mod.xls";

	public static void main(String[] args) throws Exception {
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		HashMap<String, ExcelComponent> optional_remaining = config
				.getAndTrimOptionalComponentIDs(new File(INPUT_FILE));

		List<ExcelComponent> optional_included = new ArrayList<ExcelComponent>();

		// While we still have work to do
		while (optional_remaining.size() != 0) {
			// Get one optional component
			ExcelComponent optional_current_attempt = optional_remaining
					.remove(optional_remaining.keySet().iterator().next());

			System.out.println("Attempting " + optional_current_attempt
					+ " with " + optional_included.toString());

			if (valid(optional_current_attempt, optional_included)) {
				optional_included.add(optional_current_attempt);
				System.out.println("Included");
			} else
				System.out.println("No Luck");
		}

	}

	public static boolean valid(final ExcelComponent current,
			final List<ExcelComponent> included) throws Exception {

		// Modify the workbook file to remove all unnecessary components
		// so that the ExcelDeploymentConfig is happy

		int max_restarts = 15;
		for (int i = 0; i < 4; i++) {
			NetMinConfig problem = new NetMinConfig();
			ExcelDeploymentConfig config = new ExcelDeploymentConfig(
					new OptionalComponentCallback() {

						@Override
						public boolean shouldIncludeComponent(String id,
								int[] cresources) {

							return true;
							/*
							 * // If this component is not optional return true
							 * if (id.startsWith("Opt") == false) return true;
							 * 
							 * if (current.equalsIgnoreCase(id) ||
							 * included.contains(id)) return true; return false;
							 */
						}
					});
			try {
				config.loadOptionalComponents(current, included);

				config.load(new File("data/temp2.xls"), problem);

				problem.init();

				Pso pso = new Pso(problem);
				pso.setTotalParticles(200);
				pso.setIterations(2);

				VectorSolution sol = pso.solve(problem.getFitnessFunction());

				DeploymentPlan plan = new DeploymentPlan(problem, sol);

				if (plan.isValid()) {
					return true;
				}
			} catch (Exception e) {
				if (max_restarts == 0)
					return false;
				i = 0;
				max_restarts--;
			}
		}

		return false;
	}

}
