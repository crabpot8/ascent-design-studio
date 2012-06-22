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

package org.ascent;


public class ProblemConfigImpl implements ProblemConfig {

	private VectorSolution[] seeds_;
	protected int[][] boundaries_;
	
	public ProblemConfigImpl(int positions, int bmin, int bmax) {
		boundaries_ = new int[positions][2];
		for (int i = 0; i < boundaries_.length; i++) {
			boundaries_[i] = new int[] { bmin, bmax };
		}
	}

	public VectorSolution[] createInitialSolutions(int count) {
		VectorSolution[] solutions = new VectorSolution[count];

		
		int start = 0;
		if (seeds_ != null) {
			start = seeds_.length;
			for (int i = 0; i < seeds_.length; i++)
				solutions[i] = seeds_[i];
		}

		for (int i = start; i < count; i++) {
			int[] pos = new int[boundaries_.length];
			for (int j = 0; j < boundaries_.length; j++) {
				pos[j] = Util.random(boundaries_[j][0], boundaries_[j][1]);
			}
			solutions[i] = new VectorSolution(pos);
			
		}
		return solutions;
	}

	public int[][] getPositionBoundaries() {
		return boundaries_;
	}

	public boolean isFeasible(VectorSolution sol) {
		return true;
	}

	public VectorSolution[] getSeeds() {
		return seeds_;
	}

	public void setSeeds(VectorSolution[] seeds) {
		seeds_ = seeds;
	}

}
