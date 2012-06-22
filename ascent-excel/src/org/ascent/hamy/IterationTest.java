package org.ascent.hamy;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.ascent.VectorSolution;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.NetMinConfig;
import org.ascent.deployment.excel.ExcelDeploymentConfig;
import org.ascent.pso.Pso;

public class IterationTest {

	private static String INPUT_FILE = "data/problem_alan.xls";
	private static String OUPUT_FILE = "benchmark.txt";

	private static int PARTICLES = 400;
	private static int ITERATIONS = 3;

	/**
	 * Defines an upper bound on the number of data points you will receive for
	 * every particle-iteration combination
	 */
	private static int TRIALS = 1000;

	/**
	 * How many exceptions are we willing to put up with before abandoning one
	 * data point for one particle-iteration combination. Each point gets this
	 * many tries (if needed)
	 */
	private static int EXCEPTION_RETRIES = 100;

	public static void main(String[] args) throws Exception {

		PrintWriter output = new PrintWriter(OUPUT_FILE);
		output.println("result,time");

		for (int trials = 0; trials < TRIALS; trials++) {

			Result r = null;

			boolean success = false;
			int retries = EXCEPTION_RETRIES;
			while (!success && retries-- > 0) {
				try {
					r = run(ITERATIONS, PARTICLES);
					success = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			output.append(r.success ? "1" : "0").append(',').append(
					Long.toString(r.time)).println();

			output.flush();
		}

		output.close();
	}

	public static Result run(int iterations, int particles) throws Exception {
		NetMinConfig problem = new NetMinConfig();
		ExcelDeploymentConfig config = new ExcelDeploymentConfig();
		config.load(new File(INPUT_FILE), problem);

		problem.init();

		Pso pso = new Pso(problem);
		pso.setTotalParticles(particles);
		pso.setIterations(iterations);

		long start_time = System.currentTimeMillis();
		VectorSolution sol = pso.solve(problem.getFitnessFunction());
		long end_time = System.currentTimeMillis() - start_time;
		DeploymentPlan plan = new DeploymentPlan(problem, sol);

		Result r = new Result();
		r.success = plan.isValid();
		r.time = end_time;

		return r;
	}

	static class Result {
		public boolean success = false;

		/** Execution time in milliseconds */
		public long time = -1;
	}

}
