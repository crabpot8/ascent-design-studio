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

package org.ascent.binpacking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ascent.ReverseComparator;

public class MultiStrategyBinPacker {

	private FFDBinPacker solver_;
	private List<FFDBinPacker> packers_ = new ArrayList<FFDBinPacker>();

	public MultiStrategyBinPacker() {
		initPackers();
	}

	protected void initPackers() {
		packers_.add(new FFDBinPacker());

		FFDBinPacker solver = new FFDBinPacker();
		solver.setItemSortingStrategy(new ReverseComparator(solver
				.getItemSortingStrategy()));
		packers_.add(solver);

		solver = new FFDBinPacker();
		solver.setWeightingStrategy(new WeightUpdateStrategy() {

			public double getWeight(ItemState st) {
				double w = 0;
				for (int i = 0; i < st.getSize().length; i++) {
					w += st.getSize()[i] * st.getSize()[i];
				}
				return Math.sqrt(w);
			}
		});
		packers_.add(solver);

		solver = new FFDBinPacker();
		solver_ = solver;
		solver.setWeightingStrategy(new WeightUpdateStrategy() {

			public double getWeight(ItemState st) {
				double w = 0;
				for (int i = 0; i < st.getSize().length; i++) {
					w += st.getSize()[i] * st.getSize()[i];
				}
				return Math.sqrt(w)
						+ (1000 * solver_.getExcluded(
								Arrays.asList(new Object[] { st.getItem() }))
								.size());
			}
		});

		packers_.add(solver);

		solver = new FFDBinPacker();
		solver_ = solver;
		solver.setBinSortingStrategy(new ReverseComparator(solver
				.getBinSortingStrategy()));
		solver.setWeightingStrategy(new WeightUpdateStrategy() {

			public double getWeight(ItemState st) {
				double w = 0;
				for (int i = 0; i < st.getSize().length; i++) {
					w += st.getSize()[i] * st.getSize()[i];
				}
				return Math.sqrt(w)
						+ (1000 * solver_.getExcluded(
								Arrays.asList(new Object[] { st.getItem() }))
								.size());
			}
		});
		packers_.add(solver);

		solver = new LeastBoundPacker();
		packers_.add(solver);

		solver = new FFDBinPacker();
		solver.setBinSortingStrategy(new ReverseComparator(solver
				.getBinSortingStrategy()));
		packers_.add(solver);

	}

	public List<FFDBinPacker> getPackers() {
		return packers_;
	}

	public void setPackers(List<FFDBinPacker> packers) {
		packers_ = packers;
	}

	public List<Map<Object, List>> pack(BinPackingProblem p) {
		List<Map<Object, List>> sols = new ArrayList<Map<Object, List>>();
		for (FFDBinPacker core : packers_) {
			core.configure(p);
			addSolution(sols, core.nextMapping());
		}

		return sols;
	}

	protected void addSolution(List<Map<Object, List>> sols, Map<Object, List> s) {
		if (s != null) {
			sols.add(s);
		}
	}
}
