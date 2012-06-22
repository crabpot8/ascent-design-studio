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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.ResourceConsumptionPolicy;

public class BinPackingProblem {

	private List<Bin> bins_ = new ArrayList<Bin>();
	private List<Item> items_ = new ArrayList<Item>();
	private Map<Item, Bin> preAllocations_ = new HashMap<Item, Bin>();
	
	private Map<Object,ResourceConsumptionPolicy> resourcePolicies_ = new HashMap<Object,ResourceConsumptionPolicy>();

	public List<Bin> getBins() {
		return bins_;
	}

	public void setBins(List<Bin> bins) {
		bins_ = bins;
	}

	public List<Item> getItems() {
		return items_;
	}

	public void setItems(List<Item> items) {
		items_ = items;
	}

	public Map<Object, ResourceConsumptionPolicy> getResourcePolicies() {
		return resourcePolicies_;
	}

	public void setResourcePolicies(
			Map<Object, ResourceConsumptionPolicy> resourcePolicies) {
		resourcePolicies_ = resourcePolicies;
	}
	
	public int[][] getItemSizes(){
		return getSizes(items_);
	}
	
	public int[][] getBinSizes(){
		return getSizes(bins_);
	}
	
	public Map<Item, Bin> getPreAllocations() {
		return preAllocations_;
	}

	public void setPreAllocations(Map<Item, Bin> preAllocations) {
		preAllocations_ = preAllocations;
	}

	protected int[][] getSizes(List<? extends AbstractItem> items){
		int[][] sizes = new int[items.size()][items.get(0).getSize().length];
		for(int i = 0; i < sizes.length; i++){
			AbstractItem it = items.get(i);
			int[] size = it.getSize();
			for(int j = 0; j < sizes[i].length; j++){
				if(j <= size.length-1)
					sizes[i][j] = size[j];
				else {
					sizes[i][j] = 0;
				}
			}
		}
		return sizes;
	}
}
