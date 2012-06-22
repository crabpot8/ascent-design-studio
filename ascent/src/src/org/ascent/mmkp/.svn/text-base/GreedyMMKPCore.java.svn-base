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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ascent.binpacking.DependencyManager;
import org.ascent.binpacking.ItemState;
import org.ascent.binpacking.StateProvider;
import org.ascent.binpacking.BinState;
import org.ascent.binpacking.ValueFunction;

public class GreedyMMKPCore extends MMKPCore {
	
	private Comparator itemComparator_ = new Comparator() {
	
		public int compare(Object arg0, Object arg1) {
			return (int)(getSortValue(arg0) - getSortValue(arg1));
		}
	
	};

	public GreedyMMKPCore(ValueFunction valueFunction,
			ValueFunction<Collection> optimizationFunction,
			DependencyManager dependencyManager, StateProvider stateProvider,
			Collection initialSolution, BinState target) {
		super(valueFunction, optimizationFunction, dependencyManager,
				stateProvider, initialSolution, target);
		
	}

	
	
	@Override
	public void init() {
		for(SetState st : getSetStates()){
			Collections.sort(st.getSet(),itemComparator_);
		}
		super.init();
	}



	@Override
	public Object chooseAlternateItem(SetState permset) {
		Object alt = null;
		List set = permset.getSet();
		int index = set.indexOf(permset.getCurrentItem());
		if(index < set.size() - 1)
			return set.get(index + 1);
		else if(index > 0)
			return set.get(index - 1);
		
		return null;
	}

	@Override
	public SetState choosePermutationSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public double getSortValue(Object item){
		double value = getItemValue(item);
		ItemState st = getStateProvider().getSourceState(item);
		double weight = st.getWeight();
		return value/weight;
	}

}
