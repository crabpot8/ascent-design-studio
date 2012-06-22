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

package org.ascent.mmkp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ascent.binpacking.ValueFunction;

/**
 * This class is an implementation of a particle
 * swarm optimization algorithm for MMKP problems.
 * The solver is adaptive and will iteratively
 * expand the number of particle movement steps
 * and total particles until a solution is found.
 * 
 * @author Jules White
 *
 */
public class PsoMMKP extends SeedableMMKP {

	private ValueFunction<Solution> fitnessFunction_ = new ValueFunction<Solution>() {

		public double getValue(Solution src) {
			if (src.getOverflow() > 0) {
				return src.getOverflow() * -1;
			} else {
				return valueFunction_.getValue(src.getItems());
			}
		}
	};

	public class Particle {
		private Solution position_;
		private int[] velocity_;

		public Particle(Solution pos) {
			position_ = pos;
			velocity_ = new int[pos.getItems().size()];
		}

		public String toString() {
			String str = "Particle[";
			for (Item i : position_.getItems()) {
				str += indexedItems_[i.getIndex()] + ",";
			}
			str += "] v{";
			for (int i = 0; i < velocity_.length; i++) {
				str += velocity_[i] + ",";
			}
			str += "}";
			if (position_.getOverflow() == 0)
				str += "*";
			return str;
		}

		public void update() {
			int[] veloc = calclateNewVelocity(globalBest_, localBest_,
					position_);
			position_ = updatePosition(position_, veloc);
			velocity_ = veloc;
			// if (position_.getOverflow() == 0) {
			double val = fitnessFunction_.getValue(position_);
			if (val > globalBestValue_) {
				globalBest_ = position_;
				globalBestValue_ = val;
				leader_ = this;
			}
			if (val > localBestValue_) {
				localBest_ = position_;
				localBestValue_ = val;
			}
			// }
		}

		public int[] calclateNewVelocity(Solution gbest, Solution lbest,
				Solution curr) {
			int[] veloc = new int[gbest.getItems().size()];
			for (int i = 0; i < veloc.length; i++) {
				if (curr.getItems().size() > i) {
					int loc = indexedItems_[curr.getItems().get(i).getIndex()];
					int gloc = indexedItems_[gbest.getItems().get(i).getIndex()];
					int lloc = indexedItems_[lbest.getItems().get(i).getIndex()];
					veloc[i] = ((int) Math.rint(inertia_ * velocity_[i]))
							+ ((int) Math.rint(globalLearningRate_
									* (gloc - loc)))
							+ ((int) Math.rint(localLearningRate_
									* (lloc - loc)));
//					veloc[i] = velocity_[i]
//					+ (gloc - loc)
//					+ (lloc - loc);

					if (veloc[i] > 0)
						veloc[i] = Math.min(veloc[i], velocityMax_);
					else if (veloc[i] < 0)
						veloc[i] = Math.max(veloc[i], -1 * velocityMax_);
				}
			}
			return veloc;
		}

		public Solution updatePosition(Solution curr, int[] veloc) {
			List<Item> npos = new ArrayList<Item>(veloc.length);
			for (int i = 0; i < veloc.length; i++) {
				Item it = null;
				if(curr.getItems().size() > i){
					it = curr.getItems().get(i);
				}
				else {
					it = getSets().get(i).getItems().get(0);
				}
				
				int loc = indexedItems_[it.getIndex()];
				int nloc = loc + veloc[i];
				if (nloc < 0)
					nloc = 0;
				else if (nloc > it.getSet().getItems().size() - 1)
					nloc = it.getSet().getItems().size() - 1;

				Item n = it.getSet().getItems().get(nloc);
				npos.add(n);
			}
			return new Solution(npos,getProblem());
		}

		public boolean equals(Object o) {
			Particle p = (Particle) o;
			for (int i = 0; i < position_.getItems().size(); i++) {
				if (position_.getItems().get(i) != p.position_.getItems()
						.get(i))
					return false;
			}
			for (int i = 0; i < velocity_.length; i++) {
				if (velocity_[i] != p.velocity_[i])
					return false;
			}
			return true;
		}
	}

	private int adaptiveRetries_ = 10;
	private boolean adaptive_ = true;
	private Particle leader_;
	private double globalLearningRate_ = 2;
	private double localLearningRate_ = 2;
	private double inertia_ = 1;
	private int velocityMax_ = 35;
	private double globalBestValue_ = Integer.MIN_VALUE;
	private double localBestValue_ = Integer.MIN_VALUE;
	private Solution globalBest_;
	private Solution localBest_;
	private ValueFunction<Collection> valueFunction_;
	private int iterations_ = 10;
	private int totalParticles_ = 11;

	public PsoMMKP(MMKPProblem problem) {
		super(problem);
	}

	public Particle[] createParticles(int size) {
		Particle[] particles = new Particle[size];
		for (int i = 0; i < size; i++) {
			// ArrayList<Item> its = new ArrayList<Item>();
			// for (Set s : getSets()) {
			// int it = random(0, s.getItems().size() - 1);
			// its.add(s.getItems().get(it));
			// }
			List<Item> its = ((new RandomMMKP(getProblem())))
					.solve(valueFunction_);
			// pop.add(new Solution(its));
			particles[i] = new Particle(new Solution(its,getProblem()));
		}
		return particles;
	}

	public void printState(String label, Particle[] parts) {
		System.out.println(label);
		System.out.println("{");
		for (Particle p : parts)
			System.out.println(p);
		System.out.println("}");
	}
	
	public List<Item> solve(ValueFunction<Collection> goal) {
		int retry = adaptiveRetries_;
		List<Item> solution = solveImpl(goal);
		while(solution == null && retry > 0){
			solution = solveImpl(goal);
			retry--;
			setIterations(getIterations() * 2);
			setTotalParticles(getTotalParticles() + 10);
		}
		return solution;
	}

	protected List<Item> solveImpl(ValueFunction<Collection> goal) {

		valueFunction_ = goal;

		Particle[] particles = createParticles(totalParticles_);
		// JHEU solver2 = new JHEU(getProblem());
		// List<Item> solution2 = solver2.solve(DEFAULT_GOAL);
		// particles[totalParticles_-1] = new Particle(new Solution(solution2));
		//		
		for (int i = 0; i < getSeeds().size(); i++) {
			particles[i] = new Particle(new Solution(getSeeds().get(i),getProblem()));
		}

		for (Particle p : particles) {
			// if (p.position_.getOverflow() == 0) {
			double val = fitnessFunction_.getValue(p.position_);
			if (val > globalBestValue_) {
				globalBest_ = p.position_;
				globalBestValue_ = val;
				leader_ = p;
			}
			if (val > localBestValue_) {
				localBest_ = p.position_;
				localBestValue_ = val;
			}
			// }
		}

		// printState("Initial Positions", particles);

		while (iterations_ > 0) {
			localBestValue_ = 0;

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
				return globalBest_.getItems();

			iterations_--;
			printState("Iterations "+iterations_, particles);
		}

		if (globalBest_.getOverflow() > 0)
			return null;

		return globalBest_.getItems();
	}
	
	

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

	public static void main(String[] args){
		int i = 50;
		MMKPProblem p = MMKPProblem.genWithOpt(i, 2, 8, 2, 50, 250, 30, 150, 5,
				50);
		// System.out.println(p);
		
		long pstart = System.currentTimeMillis();
		PsoMMKP psolver = new PsoMMKP(p);
//		psolver.getSeeds().add(solution2);
		List<Item> solution3 = psolver.solve(DEFAULT_GOAL);
		System.out.println("PSO Time:"+(System.currentTimeMillis()-pstart));
		System.out.println("foo");
		if (solution3 != null)
			System.out.println("Genetic Seeded Sets:" + i + " Opt:"
				+ ((DEFAULT_GOAL.getValue(solution3) / (i * 50)) * 100)
				+ "%");
		else
			System.out.println("No solution found");
	}
}
