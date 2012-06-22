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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.binpacking.ValueFunction;

public class MMKP {

	public static final ValueFunction<Collection> DEFAULT_GOAL = new ValueFunction<Collection>() {
		public double getValue(Collection src) {
			double v = 0;
			if(src == null)
				return 0;
			for (Object o : src) {
				Item i = (Item) o;
				v += i.getValue();
			}
			return v;
		}
	};

	public static final Comparator<Item> VALUE_COMPARATOR = new Comparator<Item>() {
		public int compare(Item o1, Item o2) {
			if (o1 == null)
				return 1;
			else if (o2 == null)
				return -1;
			else if (o1.getValue() < o2.getValue())
				return -1;
			else if (o1.getValue() > o2.getValue())
				return 1;
			else
				return 0;
		}
	};

	public static final Comparator<Item> NETVALUE_COMPARATOR = new Comparator<Item>() {
		public int compare(Item o1, Item o2) {
			return (int) Math
					.rint(1000 * (o1.getNetValue() - o2.getNetValue()));
		}
	};

	public static final Comparator<MMKPSet> SETS_BY_NETVALUE_COMPARATOR = new Comparator<MMKPSet>() {
		public int compare(MMKPSet s1, MMKPSet s2) {
			Item o1 = s1.getItems().get(s1.getItems().size() - 1);
			Item o2 = s2.getItems().get(s2.getItems().size() - 1);
			if (o1.getNetValue() < o2.getNetValue())
				return -1;
			else if (o1.getNetValue() > o2.getNetValue())
				return 1;
			else
				return 0;
		}
	};

	public class SolutionComparator implements Comparator<Solution> {

		private ValueFunction<Collection> comparator_;

		public SolutionComparator(ValueFunction<Collection> comp) {
			super();
			comparator_ = comp;
		}

		public int compare(Solution arg0, Solution arg1) {
			if (arg0.getOverflow() > 0 && arg1.getOverflow() == 0)
				return -1;
			else if (arg1.getOverflow() > 0 && arg0.getOverflow() == 0)
				return 1;
			else if (arg1.getOverflow() > 0 && arg0.getOverflow() > 0)
				return arg1.getOverflow() - arg0.getOverflow();
			else {
				int aval = arg0.getValue();
				int bval = arg1.getValue();
				if (aval < 0) {
					aval = (int) comparator_.getValue(arg0.getItems());
					arg0.setValue(aval);
				}
				if (bval < 0) {
					bval = (int) comparator_.getValue(arg1.getItems());
					arg1.setValue(bval);
				}
				return aval - bval;
			}
		}
	}

	public class PropertyComparator implements Comparator<Item> {
		private int resource_;

		public PropertyComparator(int resource) {
			super();
			resource_ = resource;
		}

		public int compare(Item i1, Item i2) {
			return consumedResources_[i1.getIndex()][resource_]
					- consumedResources_[i2.getIndex()][resource_];
		}
	}

	public class ResourceFit {
		private int resource_;
		private int count_;

		public ResourceFit(int res, int count) {
			resource_ = res;
			count_ = count;
		}

		public int getResource() {
			return resource_;
		}

		public int getCount() {
			return count_;
		}
	}

	
	public class SwapData {
		private List<Item> items_;
		private List<Item> newSolution_;

		public SwapData(Item item, List<Item> nsol) {
			items_ = new ArrayList<Item>(1);
			items_.add(item);
			newSolution_ = nsol;
		}

		public SwapData(List<Item> items, List<Item> nsol) {
			items_ = items;
			newSolution_ = nsol;
		}

		public void doSwap() {
			for (Item item : items_) {
				MMKPSet s = item.getSet();
				Item prev = s.getCurrentItem();
				for (int i = 0; i < resources_.length; i++) {
					int val = currentResources_[i];
					if (prev != null)
						val += getRes(prev.getIndex(), i);
					val -= getRes(item.getIndex(), i);
					currentResources_[i] = val;
				}
				s.setCurrentItem(item);
			}
		}

		public List<Item> getNewSolution() {
			return newSolution_;
		}

		public void setNewSolution(List<Item> newSolution) {
			newSolution_ = newSolution;
		}

		public List<Item> getItems() {
			return items_;
		}

		public void setItems(List<Item> items) {
			items_ = items;
		}

	}

	

	private int[][] consumedResources_;
	private List<Item> items_;
	private List<MMKPSet> sets_;
	private int[] resources_;
	private int[] currentResources_;
	protected int[] indexedItems_;
	private List<Item> initialSolution_;
	private List<Item> solution_;
	double currentValue_ = 0;
	private MMKPProblem problem_;

	public MMKP(MMKPProblem problem) {
		problem_ = problem;
		sets_ = new ArrayList<MMKPSet>(problem.getSets().length);
		items_ = new ArrayList<Item>(problem.getItems().length);
		resources_ = problem.getResources();
		consumedResources_ = problem.getConsumedResources();
		Map<Integer, MMKPSet> sets = new HashMap<Integer, MMKPSet>();
		indexedItems_ = new int[problem.getSets().length];
		for (int i = 0; i < problem.getSets().length; i++) {
			Integer snum = problem.getSets()[i];
			MMKPSet s = sets.get(snum);
			if (s == null) {
				s = new MMKPSet(new ArrayList<Item>());
				sets.put(snum, s);
				sets_.add(s);
			}
			Object item = problem.getItems()[i];
			double v = problem.getValues()[i];
			Item it = new Item(s, item, i, v);
			indexedItems_[i] = s.getItems().size();
			items_.add(it);
			s.getItems().add(it);
		}
		//		
		// indexedItems_ = new ArrayList<Item[]>(getSets().size());
		// for(Set s : getSets()){
		// Item[] its = new Item[s.getItems().size()];
		// indexedItems_.add(its);
		// for(int i = 0; i < its.length; i++){
		// its[i] = s.getItems().get(i);
		// }
		// }
	}

	public void init(ValueFunction<Collection> goal) {
		if (currentResources_ == null)
			currentResources_ = new int[resources_.length];
		for (int i = 0; i < resources_.length; i++)
			currentResources_[i] = resources_[i];
		for (MMKPSet s : sets_) {
			s.setCurrentItem(null);
			for (Item i : s.getItems()) {
				i.setNetWeight(netWeight(i));
				i.setNetValue(netValue(i));
			}
		}

		solution_ = initialSolution();
		currentValue_ = goal.getValue(solution_);
	}

	public PropertyComparator propComparator(int res) {
		return new PropertyComparator(res);
	}

	public List<Item> sortedDup(List<Item> set, int res) {
		return sortedDup(set, propComparator(res));
	}

	public List<Item> sortedDup(List<Item> set, Comparator comp) {
		List dup = new ArrayList(set);
		Collections.sort(dup, comp);
		return dup;
	}

	public Item largest(List<Item> set, int res) {
		List<Item> dup = sortedDup(set, res);
		return dup.get(dup.size() - 1);
	}

	public Item largestValued(List<Item> set) {
		List<Item> dup = sortedDup(set, VALUE_COMPARATOR);
		return dup.get(dup.size() - 1);
	}

	public Item smallest(List<Item> set, int res) {
		return sortedDup(set, res).get(0);
	}

	public double upperBound() {
		int max = tightestFitResource().getCount();
		List<Item> hv = new ArrayList<Item>();
		for (MMKPSet s : sets_)
			hv.add(largestValued(s.getItems()));

		double vsum = 0;
		hv = sortedDup(hv, VALUE_COMPARATOR);
		for (int i = hv.size() - 1; i > (hv.size() - 1) - max; i--)
			vsum += hv.get(i).getValue();
		return vsum;
	}

	public ResourceFit tightestFitResource() {
		int least = Integer.MAX_VALUE;
		int tres = -1;
		for (int i = 0; i < resources_.length; i++) {
			int resval = resources_[i];
			int curr = 0;
			double val = resval;
			Comparator comp = propComparator(i);
			List<Item> sitems = new ArrayList<Item>();
			for (MMKPSet s : sets_)
				sitems.add(smallest(s.getItems(), i));

			for (Item o : sitems) {
				int ival = consumedResources_[o.getIndex()][i];
				if (val >= ival) {
					val -= ival;
					curr++;
				} else {
					break;
				}
			}
			if (curr < least) {
				least = curr;
				tres = i;
			}
		}
		return new ResourceFit(tres, least);
	}

	public boolean fits(Item it) {
		for (int i = 0; i < resources_.length; i++) {
			if (consumedResources_[it.getIndex()][i] > currentResources_[i])
				return false;
		}
		return true;
	}
	
	public int totalResourceConsumption(List<Item> sol, int res){
		int total = 0;
		for (Item it : sol) {
			total += consumedResources_[it.getIndex()][res];
		}
		return total;
	}
	
	public int residualResourceValue(List<Item> sol, int res){
		return getResources()[res] - totalResourceConsumption(sol, res);
	}
	
	public Item smallestItemInSet(MMKPSet s){
		Comparator<Item> comp = new Comparator<Item>() {
		
			public int compare(Item o1, Item o2) {
				return ((int)Math.rint(netWeight(o1) - netWeight(o2)));
			}
		};
		ArrayList<Item> items = new ArrayList<Item>(s.getItems());
		Collections.sort(items,comp);
		return items.get(0);
	}

	public boolean fitsWithSwap(Item it) {
		Item ex = it.getSet().getCurrentItem();
		for (int i = 0; i < resources_.length; i++) {
			int delta = (ex == null) ? 0 : consumedResources_[ex.getIndex()][i];
			if (consumedResources_[it.getIndex()][i] > currentResources_[i]
					+ delta)
				return false;
		}
		return true;
	}

	public List<Item> solveSingleSet(MMKPSet s) {
		Collections.sort(s.getItems(), Collections.reverseOrder(VALUE_COMPARATOR));
		for (Item curr : s.getItems()) {
			if (fits(curr)) {
				ArrayList<Item> sol = new ArrayList<Item>();
				sol.add(curr);
				currentValue_ += curr.getValue();
				return sol;
			}
		}
		return null;
	}

	public double netValue(Item item) {
		double nw = item.getNetWeight();
		double val = item.getValue();
		if (val == 0)
			return 0;
		else if (nw == 0)
			return Integer.MAX_VALUE - (100000 - val);
		if (nw == 0)
			return val;
		double nv = val / nw;
		return nv;
	}

	public double netWeight(Item item) {
		double total = 0;
		int[] rvals = consumedResources_[item.getIndex()];
		for (int i = 0; i < rvals.length; i++) {
			total += (rvals[i] * rvals[i]);
		}
		return Math.sqrt(total);
	}

	List<Item> initialSolution() {
		if (initialSolution_ == null)
			return new ArrayList<Item>();
		else
			return initialSolution_;
	}

	public void setInitialSolution(List<Item> l) {
		initialSolution_ = l;
	}

	List<Item> solve(ValueFunction<Collection> goal) {
		init(goal);

		// if (sets_.size() < 4) {
		// for (Set s : sets_) {
		// Collections.sort(s.items_, Collections
		// .reverseOrder(VALUE_COMPARATOR));
		// }
		// Collections.sort(sets_, Collections
		// .reverseOrder(new Comparator<Set>() {
		//
		// public int compare(Set o1, Set o2) {
		// double v = o1.getItems().get(0).getValue()
		// - o2.getItems().get(0).getValue();
		// if (v > 0)
		// return 1;
		// else if (v < 0)
		// return -1;
		// else
		// return 0;
		// }
		//
		// }));
		// ArrayList<Item> sol = new ArrayList<Item>();
		// for (Set s : sets_) {
		// sol.addAll(solveSingleSet(s));
		// }
		// return sol;
		// }

		while (!done()) {
			Item item = choosePermuationItem();
			log(" -permuation item: ${item}");
			if (item == null)
				break;

			SwapData rslt = swapIn(item);
			log(" -new sol to evaluate:${rslt.solution}");

			List<Item> nsol = rslt.getNewSolution();
			double nval = goal.getValue(nsol);
			if (nval > currentValue_) {
				log(" -new sol is better, taking it");
				rslt.doSwap();
				solution_ = nsol;
				currentValue_ = nval;
				log(" -new sol value:${currentValue}");
			}
		}
		if((new Solution(solution_,getProblem())).getOverflow()>0 || solution_.size() != getSets().size())
			return null;
		return solution_;
	}

	public Item choosePermuationItem() {
		return null;
	}

	public SwapData swapIn(Item item) {
		List nsol = new ArrayList(solution_);
		MMKPSet set = item.getSet();
		if (set.getCurrentItem() != null)
			nsol.remove(set.getCurrentItem());
		nsol.add(item);
		return new SwapData(item, nsol);
	}

	public void log(String msg) {
		// System.out.println(msg);
	}

	public boolean done() {
		return true;
	}

	public int[][] getConsumedResources() {
		return consumedResources_;
	}

	public void setConsumedResources(int[][] consumedResources) {
		consumedResources_ = consumedResources;
	}

	public List<Item> getItems() {
		return items_;
	}

	public void setItems(List<Item> items) {
		items_ = items;
	}

	public List<MMKPSet> getSets() {
		return sets_;
	}

	public void setSets(List<MMKPSet> sets) {
		sets_ = sets;
	}

	public int[] getResources() {
		return resources_;
	}

	public void setResources(int[] resources) {
		resources_ = resources;
	}

	public int[] getCurrentResources() {
		return currentResources_;
	}

	public void setCurrentResources(int[] currentResources) {
		currentResources_ = currentResources;
	}

	public List getSolution() {
		return solution_;
	}

	public void setSolution(List solution) {
		solution_ = solution;
	}

	public double getCurrentValue() {
		return currentValue_;
	}

	public void setCurrentValue(double currentValue) {
		currentValue_ = currentValue;
	}

	public int getRes(int fi, int ri) {
		if (getConsumedResources()[fi].length > ri)
			return getConsumedResources()[fi][ri];
		else
			return 0;
	}

	public MMKPProblem getProblem() {
		return problem_;
	}

	public void setProblem(MMKPProblem problem) {
		problem_ = problem;
	}
	public int random(int min, int max) {
		double rand = Math.random();
		double range = max - min;
		double delta = range * rand;
		return (int) Math.rint((min + delta));
	}
}
