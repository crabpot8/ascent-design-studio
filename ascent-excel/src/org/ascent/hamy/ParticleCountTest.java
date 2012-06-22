package org.ascent.hamy;

import java.io.File;
import java.io.PrintWriter;
import java.util.Comparator;

import org.ascent.VectorSolution;
import org.ascent.VectorSolutionComparator;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.NetMinConfig;
import org.ascent.deployment.OrderedDeployer;
import org.ascent.deployment.excel.ExcelDeploymentConfig;
import org.ascent.pso.Pso;

public class ParticleCountTest {

	private static int test_number = 1;
	private static String INPUT_FILE = "data/problem_large_mod.xls";
	private static int ITERATIONS = 200;

	public static void main(String[] args) {

		for (int test = 0; test < 20; test++) {

			try {
				PrintWriter data_out_time = new PrintWriter("data/pop_time_out"
						+ test_number + ".txt");
				PrintWriter data_out_success = new PrintWriter(
						"data/pop_success_out" + test_number + ".txt");

				for (int part = 25; part < 250; part += 25) {

					System.out.println("Running test#" + test + " with " + part
							+ " particles");
					Result r = run(ITERATIONS, part);

					System.out.println("Finished in " + r.time + " with "
							+ r.success);
					data_out_success.write(r.success ? "1\n" : "0\n");
					data_out_time.write(Long.toString(r.time) + "\n");

					data_out_success.flush();
					data_out_time.flush();
				}

				test_number++;
				data_out_success.close();
				data_out_time.close();

			} catch (Exception e) {
				System.err.println("Aborting test " + test + " due to error");
				e.printStackTrace();
			}

		}
	}

	public static Result run(int iterations, int particles) throws Exception {
		NetMinConfig problem = new NetMinConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		config.load(new File(INPUT_FILE), problem);

		problem.init();

		Pso pso = new Pso(problem);
		pso.setTotalParticles(particles);
		pso.setIterations(iterations);

		Comparator<VectorSolution> comp = new VectorSolutionComparator(problem
				.getFitnessFunction());

		long start_time = System.currentTimeMillis();
		VectorSolution sol = pso.solve(problem.getFitnessFunction());
		long end_time = System.currentTimeMillis() - start_time;
		end_time /= 1000; // Convert to seconds
		OrderedDeployer deployer = new OrderedDeployer(problem);
		DeploymentPlan plan = deployer.deploy(sol);

		Result r = new Result();
		r.success = plan.isValid();
		r.time = end_time;

		return r;
	}

	static class Result {
		public boolean success = false;
		public long time = -1;
	}

}
