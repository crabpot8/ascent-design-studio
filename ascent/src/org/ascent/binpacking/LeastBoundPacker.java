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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeastBoundPacker extends FFDBinPacker {

	private Map<Object,Integer> validCount_ = new HashMap<Object, Integer>();
	
	public static class BinSorter implements Comparator {
		private Map<Object, Double> vals_;

		public BinSorter(Map<Object, Double> vals) {
			super();
			vals_ = vals;
		}

		public int compare(Object arg0, Object arg1) {
			return (int) Math.rint((100 * (vals_.get(arg1) - vals_.get(arg0))));
		}

	}
	
	private class ItemBoundComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			Integer c1 = validCount_.get(arg0);
			Integer c2 = validCount_.get(arg1);
			int a = (c1 != null)? c1 : Integer.MAX_VALUE;
			int b = (c2 != null)? c2 : Integer.MAX_VALUE;
			if(a != b)
				return b-a;
			else {
				int e1 =  getExcluded(Arrays.asList(new Object[]{arg0})).size();
				int e2 =  getExcluded(Arrays.asList(new Object[]{arg1})).size();
//				if(e1 != e2)
//					return e1 - e2;
//				else {
//					return (int)Math.rint(100 * (getSourceState(arg1).getWeight() - getSourceState(arg0).getWeight()));
//				}
				return e1 - e2;
			}
		}

	}

	public LeastBoundPacker() {
		super();
		setItemSortingStrategy(new ItemBoundComparator());
	}

	public LeastBoundPacker(BinPackingProblem p) {
		super(p);
		setItemSortingStrategy(new ItemBoundComparator());
	}

	
	
	public void init() {
		super.init();
		updateBoundsAndSort();
	}

	public void postIterate() {
		if (getPriorityPackingQueue().size() == 0 && getQueue() != null) {
			updateBoundsAndSort();
		}
		super.postIterate();
	}
	
	public void updateBoundsAndSort(){
		for (Object o : getQueue()) {
			ItemState st = getSourceState(o);
			List valid = (st.getValid() == null) ? getTargets() : st
					.getValid();
			intersectValidTargets(valid, getRequired(o));
			int count = 0;
			for (Object bin : valid) {
				if(willFit(st, getTargetState(bin))){
					count++;
				}
			}
			validCount_.put(o, count);
		}
		sortItems();
	}

}
