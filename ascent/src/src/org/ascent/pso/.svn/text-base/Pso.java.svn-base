 /**************************************************************************
 * Copyright 2008 Jules White                                              *
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

package org.ascent.pso;

import org.ascent.ProblemConfig;
import org.ascent.VectorSolution;
import org.ascent.binpacking.ValueFunction;

/**
 * This class is an implementation of a particle swarm optimization algorithm
 * for MMKP problems. The solver is adaptive and will iteratively expand the
 * number of particle movement steps and total particles until a solution is
 * found.
 * 
 * @author Jules White
 * 
 */
public class Pso {

	private ProblemConfig configuration_;
	private boolean globalBestMustBeFeasible_ = false;
	private int adaptiveRetries_ = 10;
	private boolean adaptive_ = true;
	private Particle leader_;
	private double globalLearningRate_ = 2;
	private double localLearningRate_ = 2;
	private double inertia_ = 1;
	private int velocityMax_ = 35;
	private double globalBestValue_ = Integer.MIN_VALUE;
	private VectorSolution globalBest_;
	private ValueFunction<VectorSolution> fitnessFunction_;
	private int iterations_ = 10;
	private int totalParticles_ = 20;
	private int[][] positionBoundaries_;
	private int particlesPerGroup_ = 5;
	

	public Pso(ProblemConfig config) {
		super();
		configuration_ = config;
		positionBoundaries_ = config.getPositionBoundaries();
	}

	public Pso() {
		super();
	}

	public Particle[] createParticles(int size) {
		VectorSolution[] sols = configuration_.createInitialSolutions(size);
		Particle[] parts = new Particle[sols.length];
		
		int gcount = 0;
		ParticleGroupImpl group = new ParticleGroupImpl();
		for(int i = 0; i < sols.length; i++){
			parts[i] = new Particle(sols[i],this,group);
			group.getParticles().add(parts[i]);
			gcount++;
			if(gcount == particlesPerGroup_){
				gcount = 0;
				group = new ParticleGroupImpl();
			}
		}
		return parts;
	}

	public boolean isFeasible(VectorSolution sol) {
		return configuration_.isFeasible(sol);
	}

	public int[][] getPositionBoundaries() {
		return positionBoundaries_;
	}

	public void setPositionBoundaries(int[][] positionBoundaries) {
		positionBoundaries_ = positionBoundaries;
	}

	public void printState(String label, Particle[] parts) {
		System.out.println(label);
		System.out.println("{");
		for (Particle p : parts)
			System.out.println(p);
		System.out.println("}");
	}

	public VectorSolution solve(ValueFunction<VectorSolution> goal) {
		int retry = adaptiveRetries_;
		fitnessFunction_ = goal;
		VectorSolution solution = solveImpl(goal);
		while (solution == null && retry > 0) {
			solution = solveImpl(goal);
			retry--;
			setIterations(getIterations() * 2);
			setTotalParticles(getTotalParticles() + 10);
		}
		return solution;
	}

	protected VectorSolution solveImpl(ValueFunction<VectorSolution> goal) {

		Particle[] particles = createParticles(totalParticles_);

		for (Particle p : particles) {
			// if (p.position_.getOverflow() == 0) {
			double val = goal.getValue(p.getPosition());
			if (val > globalBestValue_) {
				globalBest_ = p.getPosition().cloneSolution();
				globalBestValue_ = val;
				leader_ = p;
			}
			
		}

//	    printState("Initial Positions", particles);

		int iter = iterations_;
		while (iter > 0) {
			preIterate(iter);
			for (Particle p : particles) {
				p.update();
			}

			boolean converged = true;
			for (Particle p : particles) {
				if (!p.equals(leader_)) {
					converged = false;
					break;
				}
			}

			if (converged)
				return globalBest_;

			iter--;
			postIterate(iter);
//			printState("Iterations "+iterations_, particles);
		}
		
		if (!isFeasible(globalBest_))
			return null;

		return globalBest_;
	}
	
	protected void preIterate(int inum){}
	
	protected void postIterate(int inum){}

	public int getIterations() {
		return iterations_;
	}

	public void setIterations(int iterations) {
		iterations_ = iterations;
	}

	public int getTotalParticles() {
		return totalParticles_;
	}

	public void setTotalParticles(int totalParticles) {
		totalParticles_ = totalParticles;
	}

	public boolean isAdaptive() {
		return adaptive_;
	}

	public void setAdaptive(boolean adaptive) {
		adaptive_ = adaptive;
	}

	public static void main(String[] args) {
		// int i = 50;
		// MMKPProblem p = MMKPProblem.genWithOpt(i, 2, 8, 2, 50, 250, 30, 150,
		// 5,
		// 50);
		// // System.out.println(p);
		//		
		// long pstart = System.currentTimeMillis();
		// AbstractPso psolver = new AbstractPso(p);
		// // psolver.getSeeds().add(solution2);
		// List<T> solution3 = psolver.solve(DEFAULT_GOAL);
		// System.out.println("PSO Time:"+(System.currentTimeMillis()-pstart));
		// System.out.println("foo");
		// if (solution3 != null)
		// System.out.println("Genetic Seeded Sets:" + i + " Opt:"
		// + ((DEFAULT_GOAL.getValue(solution3) / (i * 50)) * 100)
		// + "%");
		// else
		// System.out.println("No solution found");
	}

	public boolean isGlobalBestMustBeFeasible() {
		return globalBestMustBeFeasible_;
	}

	public void setGlobalBestMustBeFeasible(boolean globalBestMustBeFeasible) {
		globalBestMustBeFeasible_ = globalBestMustBeFeasible;
	}

	public int getAdaptiveRetries() {
		return adaptiveRetries_;
	}

	public void setAdaptiveRetries(int adaptiveRetries) {
		adaptiveRetries_ = adaptiveRetries;
	}

	public Particle getLeader() {
		return leader_;
	}

	public void setLeader(Particle leader) {
		leader_ = leader;
	}

	public double getGlobalLearningRate() {
		return globalLearningRate_;
	}

	public void setGlobalLearningRate(double globalLearningRate) {
		globalLearningRate_ = globalLearningRate;
	}

	public double getLocalLearningRate() {
		return localLearningRate_;
	}

	public void setLocalLearningRate(double localLearningRate) {
		localLearningRate_ = localLearningRate;
	}

	public double getInertia() {
		return inertia_;
	}

	public void setInertia(double inertia) {
		inertia_ = inertia;
	}

	public int getVelocityMax() {
		return velocityMax_;
	}

	public void setVelocityMax(int velocityMax) {
		velocityMax_ = velocityMax;
	}

	public double getGlobalBestValue() {
		return globalBestValue_;
	}

	public void setGlobalBestValue(double globalBestValue) {
		globalBestValue_ = globalBestValue;
	}

	public VectorSolution getGlobalBest() {
		return globalBest_;
	}

	public void setGlobalBest(VectorSolution globalBest) {
		globalBest_ = globalBest;
	}

	public ValueFunction<VectorSolution> getFitnessFunction() {
		return fitnessFunction_;
	}

	public void setFitnessFunction(ValueFunction<VectorSolution> fitnessFunction) {
		fitnessFunction_ = fitnessFunction;
	}

}
