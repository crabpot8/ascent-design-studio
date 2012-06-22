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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ascent.binpacking.ValueFunction;

public class GeneticMMKP extends SeedableMMKP {

	private double mutationProbability_ = 0.05;
	private double breedingCutoff_ = 0.25;
	private int generations_ = 1000;
	private int populationSize_ = 100;
	private int crossOver_ = 50;
	private Solution best_;

	public GeneticMMKP(MMKPProblem problem) {
		super(problem);
		generations_ = 20 * getSets().size();
		populationSize_ = 2 * getSets().size();
		if (populationSize_ % 2 != 0)
			populationSize_++;
		populationSize_ = Math.max(50, populationSize_);
		generations_ = Math.max(50, generations_);
		crossOver_ = populationSize_ / 2;
	}
	
	public GeneticMMKP(MMKPProblem problem, int popsize, int gen) {
		super(problem);
		generations_ = gen;
		populationSize_ = popsize;
		crossOver_ = populationSize_ / 2;
	}

	public List<Solution> generatePopulation(int size,
			ValueFunction<Collection> valfunc) {
		ArrayList<Solution> pop = new ArrayList<Solution>(size);
		
		for(List<Item> seed : getSeeds()){
			pop.add(new Solution(seed,getProblem()));
			size--;
		}
		
		for (int i = 0; i < size; i++) {
//			ArrayList<Item> its = new ArrayList<Item>();
//			for (Set s : getSets()) {
//				int it = random(0, s.getItems().size() - 1);
//				its.add(s.getItems().get(it));
//			}
			List<Item> its = ((new RandomMMKP(getProblem()))).solve(valfunc);
			pop.add(new Solution(its,getProblem()));
		}
		return pop;
	}

	public List<Solution> getNFittestIndividuals(List<Solution> sols,
			Comparator<Solution> comp, int n) {
		Collections.sort(sols, comp);
		return sols.subList(sols.size() - n, sols.size());
	}

	public Solution mateSolutions(Solution a, Solution b) {
		if(a.getItems() == null)
			return b;
		else if(b.getItems() == null)
			return a;
		
		int size = random(1, a.getItems().size());

		ArrayList<Item> newsol = new ArrayList<Item>(a.getItems().size());
		for (int i = 0; i < size; i++) {
			newsol.add(a.getItems().get(i));
		}
		for (int i = size; i < b.getItems().size(); i++) {
			newsol.add(b.getItems().get(i));
		}
		for (int i = 0; i < newsol.size(); i++) {
			int mutate = random(0, 100);
			if (mutate < mutationProbability_ * 100) {
				if(a.getItems().size() > i){
					MMKPSet s = a.getItems().get(i).getSet();
					int ng = random(0, s.getItems().size() - 1);
					newsol.set(i, s.getItems().get(ng));
				}
			}
		}
		return new Solution(newsol,getProblem());
	}

	public Solution[] selectMates(List<Solution> sols) {
		int a = random(0, sols.size() - 1);
		int b = random(0, sols.size() - 1);
		return new Solution[] { sols.get(a), sols.get(b) };
	}

	public List<Item> solve(ValueFunction<Collection> goal) {
		List<Solution> pop = generatePopulation(populationSize_, goal);

		Comparator comp = new SolutionComparator(goal);

		for (int i = 0; i < generations_; i++) {
			int breed = (int) Math.rint(breedingCutoff_ * populationSize_);
			List<Solution> fittest = getNFittestIndividuals(pop, comp, breed);
			Solution best = fittest.get(fittest.size()-1);
			if(best_ == null || best_.getValue() < best.getValue())
				best_ = best;
			
			ArrayList<Solution> newpop = new ArrayList<Solution>(
					populationSize_);
			newpop.addAll(pop.subList(pop.size() - crossOver_, pop.size()));
			for (int j = 0; j < populationSize_ - crossOver_; j++) {
				Solution[] mates = selectMates(fittest);
				Solution newmate = mateSolutions(mates[0], mates[1]);
				newpop.add(newmate);
			}
			pop = newpop;

		}
		Solution sol = getNFittestIndividuals(pop, comp, 1).get(0);

		if (sol.getOverflow() > 0)
			return null;

		return sol.getItems();
	}

	public void printPopulation(List<Solution> sols) {
		System.out.println("{");
		for (Solution s : sols) {
			printSolution(s);
		}
		System.out.println("}");
	}

	public void printSolution(Solution s) {
		System.out.print("[");
		for (Item i : s.getItems())
			System.out.print(i.getIndex() + ",");
		System.out.print("] Overflow:" + s.getOverflow() + "\n");
	}

	

	public static void main(String[] args) {
		
		// for(int i = 10; i <= 100; i += 10){
		int i = 50;
		MMKPProblem p = MMKPProblem.genWithOpt(i, 10, 20, 2, 50, 250, 50, 350, 5,
				50);
		// System.out.println(p);
		long start = System.currentTimeMillis();
		JHEU solver2 = new JHEU(p);
		List<Item> solution2 = solver2.solve(DEFAULT_GOAL);
		System.out.println("JHEU Time:"+(System.currentTimeMillis()-start));
		
		long pstart = System.currentTimeMillis();
		PsoMMKP psolver = new PsoMMKP(p);
		
		psolver.getSeeds().add(solution2);
		List<Item> solution3 = psolver.solve(DEFAULT_GOAL);
		System.out.println("PSO Time:"+(System.currentTimeMillis()-pstart));
		
//		GeneticMMKP solver = new GeneticMMKP(p,30,300);
//		solver.getSeeds().add(solution3);
//		solver.getSeeds().add(solution2);
//		List<Item> solution = solver.solve(DEFAULT_GOAL);
		
		long gstart = System.currentTimeMillis();
		GeneticMMKP solver4 = new GeneticMMKP(p,30,30);
		if(solution3 != null)
			solver4.getSeeds().add(solution3);
		if(solution2 != null)
			solver4.getSeeds().add(solution2);
//		if(solution != null)
//			solver4.getSeeds().add(solution);
		List<Item> solution4 = solver4.solve(DEFAULT_GOAL);
		System.out.println("GeneticSeeded Time:"+(System.currentTimeMillis()-gstart));
		

		if (solution3 != null)
			System.out.println("Particle Sets:" + i + " Opt:"
					+ ((DEFAULT_GOAL.getValue(solution3) / (i * 50)) * 100)
					+ "%");
		else
			System.out.println("No solution found");

		if (solution2 != null)
			System.out.println("JHEU Sets:" + i + " Opt:"
					+ ((DEFAULT_GOAL.getValue(solution2) / (i * 50)) * 100)
					+ "%");
		else
			System.out.println("No solution found");
//		if (solution != null)
//			System.out.println("Genetic Sets:" + i + " Opt:"
//				+ ((DEFAULT_GOAL.getValue(solution) / (i * 50)) * 100)
//				+ "%");
//		else
//			System.out.println("No solution found");
		if (solution4 != null)
			System.out.println("Genetic Seeded Sets:" + i + " Opt:"
				+ ((DEFAULT_GOAL.getValue(solution4) / (i * 50)) * 100)
				+ "%");
		else
			System.out.println("No solution found");
		// }
		long time = System.currentTimeMillis()-start;
		System.out.println("Total Time:"+time);
	}
}
