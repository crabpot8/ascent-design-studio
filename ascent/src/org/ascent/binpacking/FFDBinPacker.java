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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ascent.ReverseComparator;

public class FFDBinPacker extends RefreshBinPackingCore {

	public class FFDSourceComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			ItemState st = getSourceState(o1);
			ItemState et = getSourceState(o2);

			if (st.needsFurtherMapping() && !et.needsFurtherMapping())
				return 1;
			else if (et.needsFurtherMapping() && !st.needsFurtherMapping())
				return -1;

			if (st.getMinInstances() > 0 && et.getMinInstances() < 1)
				return 1;
			else if (et.getMinInstances() > 0 && st.getMinInstances() < 1)
				return -1;

			if (st.getWeight() < et.getWeight())
				return -1;
			else if (st.getWeight() == et.getWeight())
				return 0;
			else
				return 1;
		}
	};

	public class FFDTargetComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			BinState st = getTargetState(o1);
			BinState et = getTargetState(o2);

			if (st.getWeight() < et.getWeight())
				return -1;
			else if (st.getWeight() == et.getWeight())
				return 0;
			else
				return 1;
		}

	};

	
	private int current_ = 0;
	private ArrayList queue_;
	private ArrayList preSelectionQueue_ = new ArrayList();
	private ArrayList preTargetedQueue_ = new ArrayList();
	private Comparator itemSortingStrategy_ = new FFDSourceComparator();
	private Comparator binSortingStrategy_ = new ReverseComparator(new FFDTargetComparator());
	private WeightUpdateStrategy weightingStrategy_;

	public FFDBinPacker() {
		super();
	}

	public FFDBinPacker(BinPackingProblem p) {
		super(p);
	}
	
	

	@Override
	public void configure(BinPackingProblem p) {
		super.configure(p);
		
		//This takes care of any items that are specified
		//to be preallocated to bins
		for (Item it : p.getPreAllocations().keySet()) {
			Bin bin = p.getPreAllocations().get(it);
			
			preSelectionQueue_.add(it);
			preTargetedQueue_.add(bin);
		}
	}

	@Override
	public boolean done() {
		return queue_ != null && (queue_.size() == 0 || current_ == -1);// queue_
		// !=
		// null
		// &&
		// queue_.size() == 0;
	}

	public ArrayList getQueue() {
		return queue_;
	}

	public void setQueue(ArrayList queue) {
		queue_ = queue;
	}

	@Override
	public Object selectSource() {
		if (queue_ == null)
			init();

		while (preSelectionQueue_.size() > 0){
			Object src = preSelectionQueue_.remove(preSelectionQueue_.size() - 1);
			if(queue_.contains(src)){
				return src;
			}
		}
		
		return nextSource();
	}

	public Object nextSource() {
		return queue_.get(queue_.size() - 1);
	}

	public void removeSource(Object src) {
		queue_.remove(src);
	}

	public void init() {
		queue_ = new ArrayList();
		queue_.addAll(getSources());

		for (Object o : queue_)
			updateSize(o);
		for (Object o : queue_)
			updateWeight(getSourceState(o));
		for (Object o : getTargets())
			updateTargetSize(o);
		for (Object o : getTargets())
			updateItemWeight(getTargetState(o));

		sortItems();
		
		current_ = queue_.size() - 1;

		sortBins();
		// Collections.reverse(getTargets());
	}
	
	public void sortItems(){
		if(itemSortingStrategy_ != null && queue_ != null)
			Collections.sort(queue_, itemSortingStrategy_);
	}

	public void sortBins(){
		if(binSortingStrategy_ != null)
			Collections.sort(getTargets(), binSortingStrategy_);
	}
	
	public void updateTargetSize(Object o) {
	}

	public void updateItemWeight(AbstractState st) {
		updateItemWeight(st, st.getSize(), 0);
	}

	public void updateItemWeight(AbstractState st, int[] size, double weight) {
		double w = 0;
		for (int i = 0; i < size.length; i++) {
			w += size[i] * size[i];
		}
		w += weight;
		st.setWeight(Math.sqrt(w));
	}

	public void updateWeight(ItemState st) {
		if(weightingStrategy_ == null){
		updateItemWeight(st, st.getSizeWithDependencies(), (st.getRequired()
				.size() * st.getRequired().size()));
		}
		else {
			st.setWeight(weightingStrategy_.getWeight(st));
		}
	}

	@Override
	public Object selectTarget(Object source) {
		Object target = null;
		ItemState ss = getSourceState(source);
		boolean pretarget = false;

		if (preTargetedQueue_.size() > 0) {
			target = preTargetedQueue_.remove(preTargetedQueue_.size() - 1);
			pretarget = true;
		} else {

			Dependencies deps = getDependencies(source);
			List valid = (ss.getValid() == null) ? getTargets() : ss.getValid();

			intersectValidTargets(valid, getRequired(source));

			if (valid != null)
				target = selectTarget(ss, valid);

			if (target != null) {
				for (Object o : getRequired(source)) {
					preSelectionQueue_.add(o);
					preTargetedQueue_.add(target);
				}
			}
		}

		if ((ss.getCurrentInstances() >= ss.getMinInstances() - 1 && target != null)
				|| target == null)
			removeSource(source);

		return target;
	}

	public Object selectTarget(ItemState ss, List valid) {
		for (Object t : valid) {
			BinState ts = getTargetState(t);
			if (willFit(ss, ts)) {
				return t;
			}
			else {
				boolean a = true;
			}
		}
		return null;
	}

	public void updateSize(Object src) {
		List all = getRequired(src);
		all.add(src);
		getSourceState(src).setSizeWithDependencies(calculateSize(all));
	}

	public List getRequired(Object src) {
		List all = new ArrayList();
		Dependencies deps = getDependencies(src);
		if (deps.size() > 0) {
			collectRequired(deps, all);
		}
		return all;
	}
	
	public List getExcluded(Object src) {
		return getExcluded(Arrays.asList(new Object[]{src}));
	}

	public List getExcluded(List srcs) {
		List exc = new ArrayList();
		for (Object o : srcs) {
			List ex = new ArrayList();
			collect(getDependencies(o), ex, Excludes.class);
			for (Object e : ex) {
				if (!exc.contains(e))
					exc.add(e);
			}
		}
		return exc;
	}

	public void collect(Dependencies deps, List all, Class type) {
		for (Dependency d : deps) {
			if (type.isAssignableFrom(d.getClass())) {
				List refs = d.getDependencies();
				for (Object o : refs) {
					if (!all.contains(o)) {
						all.add(o);
					}
				}
			}
		}
	}

	public void collectRequired(Dependencies deps, List all) {
		for (Dependency d : deps) {
			if (d instanceof Requires) {
				List refs = d.getDependencies();
				for (Object o : refs) {
					if (!all.contains(o)) {
						Dependencies odeps = getDependencies(o);
						collectRequired(odeps, all);
						all.add(o);
					}
				}
			}
		}
	}

	public int[] calculateSize(List all) {
		int[] size = new int[getSourceState(all.get(0)).getSize().length];
		for (int i = 0; i < all.size(); i++) {
			int[] delta = getSourceState(all.get(i)).getSize();
			for (int j = 0; j < size.length; j++) {
				size[j] += delta[j];
			}
		}
		return size;
	}

	public void intersectValidTargets(List intersection, List items) {
		for (Object o : items) {
			ItemState s = getSourceState(o);
			List v = s.getValid();
			if (v != null) {
				intersection.retainAll(v);
			} else if (s.getExcluded() != null) {
				intersection.removeAll(s.getExcluded());
			}
		}
	}

	public void intersectValidPresetTargets(List intersection, List items) {
		for (Object o : items) {
			ItemState s = getSourceState(o);
			List v = s.getValidPresets();
			if (v != null) {
				intersection.retainAll(v);
			}
		}
	}

	public void mapTo(Object src, Object trg) {
		getSourceState(src).addTarget(trg);
	}

	public List<Item> getPriorityPackingQueue() {
		return preSelectionQueue_;
	}

	public Comparator getItemSortingStrategy() {
		return itemSortingStrategy_;
	}

	public void setItemSortingStrategy(Comparator itemSortingStrategy) {
		itemSortingStrategy_ = itemSortingStrategy;
	}

	public Comparator getBinSortingStrategy() {
		return binSortingStrategy_;
	}

	public void setBinSortingStrategy(Comparator binSortingStrategy) {
		binSortingStrategy_ = binSortingStrategy;
	}

	public WeightUpdateStrategy getWeightingStrategy() {
		return weightingStrategy_;
	}

	public void setWeightingStrategy(WeightUpdateStrategy weightingStrategy) {
		weightingStrategy_ = weightingStrategy;
	}

	public ArrayList getPreSelectionQueue() {
		return preSelectionQueue_;
	}

	public void setPreSelectionQueue(ArrayList preSelectionQueue) {
		preSelectionQueue_ = preSelectionQueue;
	}

	public ArrayList getPreTargetedQueue() {
		return preTargetedQueue_;
	}

	public void setPreTargetedQueue(ArrayList preTargetedQueue) {
		preTargetedQueue_ = preTargetedQueue;
	}

	
	/*
	 * public List findValidTargets(Object src, SourceState ss, Dependencies
	 * deps) { RefreshVectorCore core = new RefreshVectorCore(); List valid =
	 * (ss.getValid() == null) ? getTargets() : ss.getValid(); List srcs = new
	 * ArrayList(); collectSources(deps, srcs); srcs.add(src);
	 * core.setSetsToMap(srcs, valid); core.requireMapped(src);
	 * 
	 * for (Object o : srcs) insertDependencies(core, o, getDependencies(o));
	 * 
	 * ArrayList v = new ArrayList(); Map<Object, List> sol =
	 * core.nextMapping(); while (sol != null) { v.addAll(sol.get(src)); sol =
	 * core.nextMapping(); }
	 * 
	 * return v; }
	 * 
	 * public void collectSources(Dependencies deps, List all) { for (Dependency
	 * d : deps) { if (!(d instanceof Excludes)) { List refs =
	 * d.getDependencies(); for (Object o : refs) { if (!refs.contains(o)) {
	 * Dependencies odeps = getDependencies(o); collectSources(odeps, all);
	 * all.add(o); } } } } }
	 * 
	 * public void insertDependencies(RefreshCore core, Object src, Dependencies
	 * deps) { SourceState st = getSourceState(src); for (Dependency dep : deps)
	 * { if (!(dep instanceof Excludes)) dep.apply(core); } }
	 */
}
