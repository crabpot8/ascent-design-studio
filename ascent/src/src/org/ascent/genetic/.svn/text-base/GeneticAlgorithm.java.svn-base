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

package org.ascent.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ascent.ProblemConfig;
import org.ascent.Util;
import org.ascent.VectorSolution;
import org.ascent.VectorSolutionComparator;
import org.ascent.binpacking.ValueFunction;

public class GeneticAlgorithm {

	private double mutationProbability_ = 0.05;
	private double breedingCutoff_ = 0.25;
	private int generations_ = 100;
	private int populationSize_ = 100;
	private int crossOver_ = 50;
	private VectorSolution best_;
	private ProblemConfig configuration_;

	

	public GeneticAlgorithm(ProblemConfig configuration) {
		super();
		configuration_ = configuration;
	}

	public List<VectorSolution> generatePopulation(int size) {
		List<VectorSolution> sols = Arrays.asList(configuration_.createInitialSolutions(size));
		return new ArrayList<VectorSolution>(sols);
	}

	public List<VectorSolution> getNFittestIndividuals(List<VectorSolution> sols,
			Comparator<VectorSolution> comp, int n) {
		Collections.sort(sols, comp);
		return sols.subList(sols.size() - n, sols.size());
	}

	public VectorSolution mateSolutions(VectorSolution a, VectorSolution b) {
		
		int size = Util.random(1, a.getPosition().length-1);

		int[] npos = new int[a.getPosition().length];
		for (int i = 0; i < size; i++) {
			npos[i] = a.getPosition()[i];
		}
		for (int i = size; i < b.getPosition().length; i++) {
			npos[i] = b.getPosition()[i];
		}
		for (int i = 0; i < npos.length; i++) {
			int mutate = Util.random(0, 100);
			if (mutate < mutationProbability_ * 100) {
				npos[i] = Util.random(configuration_.getPositionBoundaries()[i][0], configuration_.getPositionBoundaries()[i][1]);
			}
		}
		return new VectorSolution(npos);
	}

	public VectorSolution[] selectMates(List<VectorSolution> sols) {
		int a = Util.random(0, sols.size() - 1);
		int b = Util.random(0, sols.size() - 1);
		return new VectorSolution[] { sols.get(a), sols.get(b) };
	}

	public VectorSolution solve(ValueFunction<VectorSolution> goal) {
		List<VectorSolution> pop = generatePopulation(populationSize_);

		Comparator comp = new VectorSolutionComparator(goal);

		for (int i = 0; i < generations_; i++) {
			int breed = (int) Math.rint(breedingCutoff_ * populationSize_);
			List<VectorSolution> fittest = getNFittestIndividuals(pop, comp, breed);
			VectorSolution best = fittest.get(fittest.size()-1);
			if(best_ == null || goal.getValue(best_) < goal.getValue(best))
				best_ = best;
			
			ArrayList<VectorSolution> newpop = new ArrayList<VectorSolution>(
					populationSize_);
			newpop.addAll(pop.subList(pop.size() - crossOver_, pop.size()));
			for (int j = 0; j < populationSize_ - crossOver_; j++) {
				VectorSolution[] mates = selectMates(fittest);
				VectorSolution newmate = mateSolutions(mates[0], mates[1]);
				newpop.add(newmate);
			}
			pop = newpop;

		}
		VectorSolution sol = getNFittestIndividuals(pop, comp, 1).get(0);

		if (!configuration_.isFeasible(sol))
			return null;

		return sol;
	}
	

	public double getMutationProbability() {
		return mutationProbability_;
	}

	public void setMutationProbability(double mutationProbability) {
		mutationProbability_ = mutationProbability;
	}

	public double getBreedingCutoff() {
		return breedingCutoff_;
	}

	public void setBreedingCutoff(double breedingCutoff) {
		breedingCutoff_ = breedingCutoff;
	}

	public int getGenerations() {
		return generations_;
	}

	public void setGenerations(int generations) {
		generations_ = generations;
	}

	public int getPopulationSize() {
		return populationSize_;
	}

	public void setPopulationSize(int populationSize) {
		populationSize_ = populationSize;
	}

	public int getCrossOver() {
		return crossOver_;
	}

	public void setCrossOver(int crossOver) {
		crossOver_ = crossOver;
	}

	public VectorSolution getBest() {
		return best_;
	}

	public void setBest(VectorSolution best) {
		best_ = best;
	}

	public ProblemConfig getConfiguration() {
		return configuration_;
	}

	public void setConfiguration(ProblemConfig configuration) {
		configuration_ = configuration;
	}

	public static void main(String[] args) {
		
	}
}
