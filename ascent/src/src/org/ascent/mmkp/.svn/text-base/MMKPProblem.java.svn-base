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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MMKPProblem {
	private Object[] items_;
	private int[] sets_;
	private int[][] consumedResources_;
	private int[] values_;
	private int[] resources_;

	public MMKPProblem(Object[] items, int[] sets, int[][] consumedResources,
			int[] values, int[] resources) {
		super();
		items_ = items;
		sets_ = sets;
		consumedResources_ = consumedResources;
		values_ = values;
		resources_ = resources;
	}

	public Object[] getItems() {
		return items_;
	}

	public void setItems(Object[] items) {
		items_ = items;
	}

	public int[] getSets() {
		return sets_;
	}

	public void setSets(int[] sets) {
		sets_ = sets;
	}

	public int[][] getConsumedResources() {
		return consumedResources_;
	}

	public void setConsumedResources(int[][] consumedResources) {
		consumedResources_ = consumedResources;
	}

	public int[] getValues() {
		return values_;
	}

	public void setValues(int[] values) {
		values_ = values;
	}

	public int[] getResources() {
		return resources_;
	}

	public void setResources(int[] resources) {
		resources_ = resources;
	}

	public static MMKPProblem gen(int tsets, int smin, int smax, int rtotal,
			int rmin, int rmax, int irmin, int irmax, int vmin, int vmax) {

		int[] resources = new int[rtotal];
		for (int i = 0; i < rtotal; i++)
			resources[i] = random(rmin, rmax);
		int[] sleft = new int[tsets];
		int titems = 0;

		for (int i = 0; i < tsets; i++) {
			int ssize = random(smin, smax);
			sleft[i] = ssize;
			titems += ssize;
		}

		int[] sets = new int[titems];
		int curr = 0;
		for (int i = 0; i < tsets; i++) {
			for (int j = 0; j < sleft[i] - 1; j++) {
				curr++;
				sets[curr] = i;
			}
		}

		String[] items = new String[titems];
		for (int i = 0; i < items.length; i++)
			items[i] = "i:" + i;

		int[][] cons = new int[titems][rtotal];
		for (int i = 0; i < titems; i++)
			for (int j = 0; j < rtotal; j++)
				cons[i][j] = random(irmin, irmax);

		int[] values = new int[titems];
		for (int i = 0; i < titems; i++)
			values[i] = random(vmin, vmax);

		return new MMKPProblem(items, sets, cons, values, resources);
	}

	public static MMKPProblem genWithOpt(int tsets, int smin, int smax,
			int rtotal, int rmin, int rmax, int irmin, int irmax, int vmin,
			int vmax) {

		int[] resid = new int[rtotal];
		int[] resources = new int[rtotal];
		for (int i = 0; i < rtotal; i++) {
			resources[i] = random(rmin, rmax);
			resid[i] = resources[i];
		}
		int[][] solcons = new int[tsets][rtotal];
		for (int i = 0; i < tsets - 1; i++) {
			for (int j = 0; j < rtotal; j++) {
				solcons[i][j] = random(irmin, Math.min(irmax, resid[j]));
				resid[j] -= solcons[i][j];
			}
		}
		for (int j = 0; j < rtotal; j++) {
			solcons[tsets - 1][j] = resid[j];
		}
		int[] solvals = new int[tsets];
		for (int i = 0; i < tsets; i++) {
			solvals[i] = vmax;
		}
		String[] sitems = new String[tsets];
		for (int i = 0; i < tsets; i++)
			sitems[i] = "s:" + i;

		vmax--;

		int[] sleft = new int[tsets];
		int titems = 0;

		for (int i = 0; i < tsets; i++) {
			int ssize = random(smin, smax);
			sleft[i] = ssize;
			titems += ssize;
		}

		int[] sets = new int[titems];
		int curr = 0;
		for (int i = 0; i < tsets; i++) {
			for (int j = 0; j < sleft[i] - 1; j++) {
				curr++;
				sets[curr] = i;
			}
		}

		String[] items = new String[titems];
		for (int i = 0; i < items.length; i++)
			items[i] = "i:" + i;

		int[][] cons = new int[titems][rtotal];
		for (int i = 0; i < titems; i++)
			for (int j = 0; j < rtotal; j++)
				cons[i][j] = random(irmin, irmax);

		int[] values = new int[titems];
		for (int i = 0; i < titems; i++)
			values[i] = random(vmin, vmax);

		int cset = -1;
		for (int i = 0; i < sets.length; i++) {
			int set = sets[i];
			if (set >= cset) {
				items[i] = sitems[set];
				values[i] = solvals[set];
				for (int j = 0; j < rtotal; j++)
					cons[i][j] = solcons[set][j];
				cset = set + 1;
			}
		}

		return new MMKPProblem(items, sets, cons, values, resources);
	}

	public static int random(int min, int max) {
		double r = Math.random();
		int range = max - min;
		int delta = (int) Math.rint(r * range);
		return min + delta;
	}

	public double netWeight(int item) {
		int w = 0;
		for (int i = 0; i < consumedResources_[item].length; i++)
			w += (consumedResources_[item][i] * consumedResources_[item][i]);

		return Math.sqrt(w);
	}

	public double netValue(int item) {
		return ((double) values_[item]) / netWeight(item);
	}

	public boolean validSolution(List<Item> sb) {
		int[] res = new int[resources_.length];
		System.arraycopy(resources_, 0, res, 0, res.length);
		for (Item f : sb) {
			for (int i = 0; i < res.length; i++) {
				if (consumedResources_[f.getIndex()].length > 0) {
					res[i] -= consumedResources_[f.getIndex()][i];
					if (res[i] < 0)
						return false;
				}
			}
		}
		return true;
	}

	public Map<Integer, List<Integer>> getSetsMap() {
		HashMap<Integer, List<Integer>> sets = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < getItems().length; i++) {
			int set = getSets()[i];
			List<Integer> members = sets.get(set);
			if (members == null) {
				members = new ArrayList<Integer>();
				sets.put(set, members);
			}
			members.add(i);
		}
		return sets;
	}

	public MMKPProblem cloneProblem() {
		int[] sets = new int[sets_.length];
		System.arraycopy(sets_, 0, sets, 0, sets_.length);
		int[][] cr = new int[consumedResources_.length][consumedResources_[0].length];
		for (int i = 0; i < cr.length; i++) {
			if (cr[i].length != consumedResources_[i].length)
				consumedResources_[i] = new int[cr[i].length];
			System.arraycopy(consumedResources_[i], 0, cr[i], 0, cr[i].length);
		}
		int[] res = new int[resources_.length];
		System.arraycopy(resources_, 0, res, 0, res.length);
		int[] v = new int[values_.length];
		System.arraycopy(values_, 0, v, 0, v.length);
		MMKPProblem p = new MMKPProblem(getItems(), sets, cr, v, res);
		return p;
	}

	public String toString() {
		String p = "-----------MMKP Problem-----------\n";
		p += "  -Resources[";
		for (int i = 0; i < resources_.length; i++)
			p += resources_[i] + " ";
		p += "]\n";
		// p += "  -Items[\n";
		// for (int i = 0; i < items_.length; i++) {
		// p += "   " + items_[i] + " V:" + values_[i] + " [";
		// for (int j = 0; j < consumedResources_[i].length; j++)
		// p += consumedResources_[i][j] + " ";
		// p += "]";
		// p += " (" + sets_[i] + ")\n";
		// }
		// p += "  ]";

		for (int s = 0; s < sets_.length; s++) {
			p += "  -Set[\n";
			boolean found = false;
			for (int i = 0; i < sets_.length; i++) {
				if (sets_[i] == s) {
					found = true;
					p += "   " + items_[i] + " V:" + values_[i] + " [";
					for (int j = 0; j < consumedResources_[i].length; j++)
						p += consumedResources_[i][j] + " ";
					p += "] NV:" + netValue(i);
					p += " (" + sets_[i] + ")\n";
				}
			}
			if (!found)
				break;

			p += "  ]\n";
		}

		return p;
	}

	public int getOverflow(int[] solution) {

		int overflow = 0;
		for (int i = 0; i < getResources().length; i++) {
			int consumed = 0;
			for (int j = 0; j < solution.length; j++)
				consumed += getConsumedResources()[solution[j]][i];

			int delta = getResources()[i] - consumed;
			if (delta < 0)
				overflow += -1 * delta;
		}

		return overflow;
	}
}
